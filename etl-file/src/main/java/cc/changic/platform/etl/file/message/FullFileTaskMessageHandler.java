package cc.changic.platform.etl.file.message;

import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.service.JobService;
import cc.changic.platform.etl.file.execute.ExecutableFileJob;
import cc.changic.platform.etl.file.util.LogFileUtil;
import cc.changic.platform.etl.protocol.anotation.MessageToken;
import cc.changic.platform.etl.protocol.exception.ETLException;
import cc.changic.platform.etl.protocol.message.DuplexMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessageHeader;
import cc.changic.platform.etl.protocol.stream.ETLChunkedFile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import static cc.changic.platform.etl.protocol.rmi.ETLMessageType.REQUEST;

/**
 * 全量拉取文件消息处理
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@MessageToken(id = 0x0001)
public class FullFileTaskMessageHandler extends DuplexMessage {

    private Logger logger = LoggerFactory.getLogger(FullFileTaskMessageHandler.class);

    @Autowired
    private JobService jobService;
    private ExecutableFileJob responseFileJob;
    private ETLMessage message;
    private RandomAccessFile attachFile;
    private RandomAccessFile storageFile;

    @Override
    public ETLMessage getMessage() {
        return message;
    }

    public void setMessage(ETLMessage message) {
        Assert.notNull(message, "ETL message is null");
        Assert.notNull(message.getHeader(), " ETL message's header is null");
        Assert.notNull(message.getBody(), "ETL message's body is null");
        if (!(message.getBody() instanceof ExecutableFileJob))
            throw new ClassCastException("ETL message's body is not instance of " + ExecutableFileJob.class);
        this.message = message;
    }

    @Override
    public ChunkedInput getChunkAttach(ByteBuf chunkHeader) {
        if (null == attachFile)
            return null;
        try {
            return new ETLChunkedFile(chunkHeader, attachFile);
        } catch (IOException e) {
            logger.error("Get chunk file error:{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void write(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(this);
    }

    @Override
    public void read(ChannelHandlerContext ctx, ETLMessage message) throws Exception {
        ETLMessageHeader header = message.getHeader();
        if (header.getMessageType() == REQUEST.type()) {
            doRequest(message);
        } else {
            doResponse(ctx, message);
        }
    }

    private void doRequest(ETLMessage message) throws FileNotFoundException {
        if (null == message.getBody())
            throw new ETLException("Request message body can not be null");
        if (!(message.getBody() instanceof ExecutableFileJob))
            throw new ETLException("Request message body must be instance of " + ExecutableFileJob.class);
        this.message = message;
        ExecutableFileJob fileJob = (ExecutableFileJob) message.getBody();
        File sourceFile = getSourceFile(fileJob);
        if (null == sourceFile) {
            return;
        }
        if (!(sourceFile.exists() && sourceFile.isFile())) {
            fileJob.getJob().setStatus(ExecutableJob.FAILED);
            fileJob.getJob().setOptionDesc("未找到日志文件，文件：" + sourceFile.getAbsolutePath() + "不存在");
            return;
        }
        if (null == attachFile) {
            attachFile = new RandomAccessFile(sourceFile, "r");
        }
        fileJob.setFileName(sourceFile.getName());
        // TODO md5
    }

    private File getSourceFile(ExecutableFileJob fileJob) {
        File sourceFile;
        try {
            // 计算源文件夹
            String sourceDir = fileJob.getSourceDir();
            logger.info("Calculated job source file: job_id={}, source_dir={}", fileJob.getJob().getId(), sourceDir);
            if (null != fileJob.getJob().getLastRecordTime()) {
                // 最后记录时间为空时为第一次拉取，获取最老的文件
                sourceFile = LogFileUtil.getOldestLogFile(sourceDir);
                if (null == sourceFile) {
                    fileJob.getJob().setStatus(ExecutableJob.FAILED);
                    fileJob.getJob().setOptionDesc("未找到日志文件,源文件夹[" + sourceDir + "]下无符合规则的文件");
                }
            } else {
                String fileName = fileJob.getFileName();
                logger.info("Calculated job source file: job_id={}, source_dir={}, file_name={}", fileJob.getJob().getId(), sourceDir, fileName);
                sourceFile = new File(sourceDir, fileName);
            }
        } catch (Exception e) {
            logger.error("Build source file error: job_id={}, desc={}", fileJob.getJob().getId(), e.getMessage(), e);
            fileJob.getJob().setStatus(ExecutableJob.FAILED);
            fileJob.getJob().setOptionDesc("构建日志文件错误：" + e.getMessage());
            sourceFile = null;
        }
        return sourceFile;
    }

    private void doResponse(ChannelHandlerContext ctx, ETLMessage message) throws Exception {
        // 附件中不带Body
        if (null != message.getBody() && message.getBody() instanceof ExecutableFileJob) {
            this.message = message;
            responseFileJob = (ExecutableFileJob) message.getBody();
            // 判断是否在拉取数据的时候就出现了错误
            if (responseFileJob.getJob().getStatus() == ExecutableJob.FAILED) {
                jobService.doError(responseFileJob.getJob(), "客户端错误:" + responseFileJob.getJob().getOptionDesc());
            } else {
                // 先构造存储文件
                if (null == storageFile) {
                    String storageDir = responseFileJob.getStorageDir();
                    File tmpFile = new File(storageDir, responseFileJob.getFileName());
                    if (tmpFile.exists()) {
                        logger.error("Write file error: exists file [{}]", tmpFile.getAbsolutePath());
                        jobService.doError(responseFileJob.getJob(), "已存在文件:" + tmpFile.getAbsolutePath());
                        ctx.close();
                        return;
                    }
                    storageFile = new RandomAccessFile(tmpFile, "rw");
                }
            }
        } else {
            // 处理附件
            if (null != storageFile) {
                if (null == message.getAttachment() || null == message.getAttachment().getData()) {
                    logger.error("Write file error, attachment is null: job_id={}", responseFileJob.getJob().getId());
                    jobService.doError(responseFileJob.getJob(), "附件为空.");
                    ctx.close();
                } else if (!(message.getAttachment().getData() instanceof ByteBuf)) {
                    logger.error("Write file error, attachment type error: job_id={}", responseFileJob.getJob().getId());
                    jobService.doError(responseFileJob.getJob(), "附件类型错误.");
                    ctx.close();
                } else {
                    ByteBuf buf = null;
                    try {
                        buf = (ByteBuf) message.getAttachment().getData();
                        FileChannel channel = storageFile.getChannel();
                        channel.write(buf.nioBuffers());
                        if (message.getHeader().isLastPackage()) {
                            storageFile.close();
                            doFinish();
                        }
                    } finally {
                        if (null != buf)
                            buf.release();
                    }
                }
            } else {
                logger.error("Write file error, not init storage file: job_id={}", responseFileJob.getJob().getId());
                jobService.doError(responseFileJob.getJob(), "未初始化用于写入的文件.");
                ctx.close();
            }
        }
    }

    private void doFinish() {
        // TODO 写完文件后计算MD5和入库等
    }
}
