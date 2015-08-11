package cc.changic.platform.etl.file.message;

import cc.changic.platform.etl.base.common.ExecutableJobType;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.ODSConfig;
import cc.changic.platform.etl.base.service.JobService;
import cc.changic.platform.etl.base.util.LogFileUtil;
import cc.changic.platform.etl.file.execute.ExecutableFileJob;
import cc.changic.platform.etl.file.execute.FileJobTransformer;
import cc.changic.platform.etl.protocol.FileJobProto;
import cc.changic.platform.etl.protocol.anotation.MessageToken;
import cc.changic.platform.etl.protocol.exception.ETLException;
import cc.changic.platform.etl.protocol.message.DuplexMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessageAttachment;
import cc.changic.platform.etl.protocol.rmi.ETLMessageHeader;
import cc.changic.platform.etl.protocol.stream.ETLChunkedFile;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import static cc.changic.platform.etl.protocol.rmi.ETLMessageType.REQUEST;

/**
 * 增量拉取文件消息处理
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@MessageToken(id = 0x0002)
public class IncrementalFileTaskMessageHandler extends DuplexMessage {

    private Logger logger = LoggerFactory.getLogger(IncrementalFileTaskMessageHandler.class);

    private ETLMessage message;
    private Long attachOffset;
    private Long incrementalOffset;
    private RandomAccessFile attachFile;
    private RandomAccessFile storageFile;
    private ETLChunkedFile chunkedFile;
    private ExecutableFileJob job;
    private File tmpFile;

    @Autowired(required = false)
    @Qualifier(ExecutableJobType.FILE_INCREMENTALLY)
    private JobService jobService;

    @Override
    public void read(ChannelHandlerContext ctx, ETLMessage message) throws Exception {
        if (null != message.getBody() && message.getBody() instanceof FileJobProto.FileJob) {
            FileJobProto.FileJob protoJob = (FileJobProto.FileJob) message.getBody();
            ExecutableFileJob fileJob = FileJobTransformer.toExecutableFileJob(protoJob);
            message.setBody(fileJob);
        }
        ETLMessageHeader header = message.getHeader();
        if (header.getMessageType() == REQUEST.type()) {
            doRequest(message);
        } else {
            doResponse(ctx, message);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx) throws Exception {
        if (null != message && null != message.getBody() && message.getBody() instanceof ExecutableFileJob) {
            ExecutableFileJob executableFileJob = (ExecutableFileJob) message.getBody();
            message.setBody(FileJobTransformer.toProtoFileJob(executableFileJob));
        }
        ctx.writeAndFlush(this);
    }

    @Override
    public ETLMessage getMessage() {
        return message;
    }

    @Override
    public void setMessage(ETLMessage message) {
        this.message = message;
    }

    @Override
    public ChunkedInput getChunkAttach(ByteBuf chunkHeader) {
        if (null != chunkedFile)
            return chunkedFile;
        if (null == attachFile)
            return null;
        try {
            if (incrementalOffset == 0) {
                attachFile.close();
                return null;
            }
            chunkedFile = new ETLChunkedFile(chunkHeader, attachFile, attachOffset, attachOffset + incrementalOffset);
        } catch (IOException e) {
            logger.error("Get chunk file error:{}", e.getMessage(), e);
        }
        return chunkedFile;
    }

    private void doRequest(ETLMessage message) throws Exception {
        if (null == message.getBody())
            throw new ETLException("Request message body can not be null");
        if (!(message.getBody() instanceof ExecutableFileJob))
            throw new ETLException("Request message body must be instance of " + ExecutableFileJob.class);
        this.message = message;
        ExecutableFileJob fileJob = (ExecutableFileJob) message.getBody();
        try {
            String sourceDir = fileJob.getSourceDir();
            String fileName = LogFileUtil.getNextLogFileName(fileJob.getFileTask().getFileName(), fileJob.getJob(), (short) 0, null);
            File sourceFile = new File(sourceDir, fileName);
            if (sourceFile.exists()) {
                // 设置附件信息
                attachFile = new RandomAccessFile(sourceFile, "r");
                attachOffset = null == fileJob.getJob().getLastRecordOffset() ? 0 : fileJob.getJob().getLastRecordOffset();
                // 如果数据库记录的偏移量大于整个文件的长度,认为是新文件,需要重头开始
                // TODO 根据时间判断?
                if (attachOffset > attachFile.length())
                    attachOffset = 0l;
                incrementalOffset = attachFile.length() - attachOffset;
                logger.info("Found source file [job_id={}, source_file={}, fileLength={}, lastRecordOffset={}, incrementalOffset={}]", fileJob.getJobID(), sourceFile.getAbsolutePath(), attachFile.length(), attachOffset, incrementalOffset);
                if (incrementalOffset == 0) {
                    // 设置文件名
                    fileJob.setFileName(sourceFile.getName());
                    // 设置状态
                    fileJob.getJob().setStatus(ExecutableJob.NO_DATA_CHANGE);
                } else {
                    // 设置有附件，让编码器编码时不认为交互结束
                    this.message.setAttachment(new ETLMessageAttachment());
                    // 设置文件名
                    fileJob.setFileName(sourceFile.getName());
                    // 设置状态
                    fileJob.getJob().setStatus(ExecutableJob.SUCCESS);
                    fileJob.getJob().setLastRecordOffset(attachOffset);
                    fileJob.setIncrementalOffset(incrementalOffset);
                }
            } else {
                fileJob.getJob().setStatus(ExecutableJob.FAILED);
                StringBuffer error = new StringBuffer();
                error.append("source_dir=").append(fileJob.getFileTask().getSourceDir());
                error.append(", file_name=").append(fileJob.getFileTask().getFileName());
                error.append(", absolute_file=").append(sourceFile.getAbsolutePath());
                fileJob.getJob().setOptionDesc("文件不存在:[" + error.toString() + "]");
                logger.error("File not found [job_id={}, {}]", fileJob.getJobID(), error.toString());
            }
        } catch (Exception e) {
            logger.error("Get source file error: {}", e.getMessage(), e);
            fileJob.getJob().setStatus(ExecutableJob.FAILED);
            fileJob.getJob().setOptionDesc("终端获取日志文件异常：" + e.getMessage());
        }
    }

    private void doResponse(ChannelHandlerContext ctx, ETLMessage message) throws Exception {
        if (null != message.getBody() && !(message.getBody() instanceof ExecutableFileJob)) {
            throw new ETLException("Response message body must be instance of " + ExecutableFileJob.class);
        }
        try {
            if (null != message.getBody()) {
                this.message = message;
                this.job = (ExecutableFileJob) message.getBody();
                logger.info("Incremental task body response: [job_id={}, incrementalOffset={}]", job.getJobID(), job.getIncrementalOffset());
                if (job.getJob().getStatus().equals(ExecutableJob.FAILED)) {
                    jobService.onFailed(job, "客户端错误:" + job.getJob().getOptionDesc());
                } else if (job.getJob().getStatus().equals(ExecutableJob.NO_DATA_CHANGE)) {
                    jobService.onSuccess(job, "No data change, FileName=" + job.getSourceDir() + job.getFileName());
                } else {
                    tmpFile = new File(job.getStorageDir(), "." + job.getFileName() + "." + System.currentTimeMillis());
                    if (!tmpFile.getParentFile().exists())
                        Files.createParentDirs(tmpFile);
                    storageFile = new RandomAccessFile(tmpFile, "rw");
//                    long offset = job.getJob().getLastRecordOffset() == null ? 0 : job.getJob().getLastRecordOffset();
//                    long length = storageFile.length();
//                    if (length != offset) {
//                        logger.error("Write file error: storageFileLength={} but remoteFileLength={}", length, offset);
//                        jobService.doError(job.getJob(), job.getNextInterval(), "偏移量错误,本地偏移量=" + length);
//                        ctx.close();
//                        storageFile.close();
//                    } else {
//                    storageFile.seek(offset);
//                    }
                }
            } else {
                logger.info("Incremental task attachment response: [job_id={}, incrementalOffset={}]", job.getJobID(), job.getIncrementalOffset());
                if (null == message.getAttachment() || null == message.getAttachment().getData()) {
                    logger.error("Write file error, attachment is null: job_id={}", job.getJob().getId());
                    jobService.onFailed(job, "附件为空.");
                    ctx.close();
                    storageFile.close();
                } else if (!(message.getAttachment().getData() instanceof ByteBuf)) {
                    logger.error("Write file error, attachment type error: job_id={}", job.getJob().getId());
                    jobService.onFailed(job, "附件类型错误.");
                    ctx.close();
                    storageFile.close();
                } else {
                    ByteBuf buf = null;
                    try {
                        buf = (ByteBuf) message.getAttachment().getData();
                        FileChannel channel = storageFile.getChannel();
                        channel.write(buf.nioBuffers());
                        if (message.getHeader().isLastPackage()) {
                            doFinish();
                        }
                    } finally {
                        if (null != buf)
                            buf.release();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
            if (null != this.job)
                jobService.onFailed(job, e.getMessage());
        }
    }

    private void doFinish() throws IOException {
        storageFile.seek(0);
        List<String> datas = Lists.newArrayList();
        String data = storageFile.readLine();
        while (data != null) {
            datas.add(data);
            data = storageFile.readLine();
        }
        logger.info("insert data to ODS: data_size={}, sql={}", datas.size(), job.getFileTask().getInsertSql());
        if (datas.size() != 0) {
            Connection connection = null;
            PreparedStatement statement = null;
            try {
                Class.forName("org.postgresql.Driver");
                ODSConfig odsConfig = job.getOdsConfig();
                String url = "jdbc:postgresql://" + odsConfig.getOdsIp() + ":" + odsConfig.getOdsPort() + "/" + odsConfig.getOdsName();
                connection = DriverManager.getConnection(url, odsConfig.getOdsUser(), odsConfig.getOdsPwd());
                statement = connection.prepareStatement(job.getFileTask().getInsertSql());
                char split = 0x01;
                for (String data2 : datas) {
                    Iterable<String> strings = Splitter.on(split).split(data2);
                    Iterator<String> iterator = strings.iterator();
                    int i = 1;
                    while (iterator.hasNext()) {
                        statement.setObject(i++, iterator.next());
                    }
                    statement.addBatch();
                }
                if (jobService.canUpdate(job)) {
                    statement.executeBatch();
                    jobService.onSuccess(job, "FileName=" + job.getSourceDir() + job.getFileName());
                }
            } catch (Exception e) {
                logger.error("Insert error ,sql={}, desc={}", job.getFileTask().getInsertSql(), e.getMessage(), e);
                jobService.onFailed(job, e.getMessage());
            } finally {
                storageFile.close();
//            tmpFile.deleteOnExit();
                try {
                    if (null != statement)
                        statement.close();
                } catch (SQLException e) {
                }
                if (null != connection)
                    try {
                        connection.close();
                    } catch (SQLException e) {
                    }
            }
        } else {
            jobService.onFailed(job, "No data get from client");
        }
    }

    @Override
    public void handlerNettyException(String errorMessage) {
        if (null != jobService) {
            ExecutableFileJob job = (ExecutableFileJob) message.getBody();
            jobService.onFailed(job, "Netty异常" + "[" + errorMessage + "]");
        }
    }
}


