package cc.changic.platform.etl.file.message;

import cc.changic.platform.etl.base.common.ExecutableJobType;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.service.JobService;
import cc.changic.platform.etl.base.util.LogFileUtil;
import cc.changic.platform.etl.base.util.MD5Checksum;
import cc.changic.platform.etl.base.util.TimeUtil;
import cc.changic.platform.etl.file.exec.ImportDataCmdExecutor;
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
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
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
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static cc.changic.platform.etl.protocol.rmi.ETLMessageType.REQUEST;

/**
 * 全量拉取文件消息处理
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@MessageToken(id = 0x0001)
public class FullFileTaskMessageHandler extends DuplexMessage {

    private Logger logger = LoggerFactory.getLogger(FullFileTaskMessageHandler.class);
    private Gson gson = new Gson();

    @Autowired(required = false)
    @Qualifier(ExecutableJobType.FILE_FULLY)
    private JobService jobService;

    @Autowired(required = false)
    private ImportDataCmdExecutor cmdExecutor;

    private ExecutableFileJob reJob;
    private ETLMessage message;
    private RandomAccessFile attachFile;
    private RandomAccessFile storageFile;
    boolean doError = false;

    @Override
    public ETLMessage getMessage() {
        return message;
    }

    public void setMessage(ETLMessage message) {
        Assert.notNull(message, "ETL message is null");
        Assert.notNull(message.getHeader(), " ETL message's header is null");
        Assert.notNull(message.getBody(), "ETL message's body is null");
        if (!(message.getBody() instanceof FileJobProto.FileJob))
            throw new ClassCastException("ETL message's body is not instance of " + FileJobProto.FileJob.class);
        this.message = message;
    }

    @Override
    public ChunkedInput getChunkAttach(ByteBuf chunkHeader) {
        try {
            if (null == attachFile)
                return null;
            if (attachFile.length() == 0) {
                attachFile.close();
                return null;
            }
            return new ETLChunkedFile(chunkHeader, attachFile);
        } catch (IOException e) {

            logger.error("Get chunk file error:{}", e.getMessage(), e);
        }
        return null;
    }

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

    private void doRequest(ETLMessage message) throws Exception {
        if (null == message.getBody())
            throw new ETLException("Request message body can not be null");
        if (!(message.getBody() instanceof ExecutableFileJob))
            throw new ETLException("Request message body must be instance of " + ExecutableFileJob.class);
        this.message = message;
        ExecutableFileJob fileJob = (ExecutableFileJob) message.getBody();
        try {
            File sourceFile = getSourceFile(fileJob);
            if (null == sourceFile) {
                logger.error("File not found [job_id={}, file=null]", fileJob.getJobID());
                return;
            }
            if (!(sourceFile.exists() && sourceFile.isFile())) {
                fileJob.getJob().setStatus(ExecutableJob.FAILED);
                fileJob.getJob().setOptionDesc("未找到日志文件，文件：" + sourceFile.getAbsolutePath() + "不存在");
                logger.error("File not found [job_id={}, file={}]", fileJob.getJobID(), sourceFile.getAbsolutePath());
                return;
            }
            if (null == attachFile) {
                attachFile = new RandomAccessFile(sourceFile, "r");
            }
            // 设置有附件，让编码器编码时不认为交互结束
            if (attachFile.length() > 0)
                this.message.setAttachment(new ETLMessageAttachment());
            // 设置文件名
            fileJob.setFileName(sourceFile.getName());
            // 设置MD5
            fileJob.setMd5(MD5Checksum.getFileMD5Checksum(sourceFile.getAbsolutePath()));
            fileJob.getJob().setStatus(ExecutableJob.SUCCESS);
            Short fileDeleteInterval = fileJob.getGameZone().getFileDeleteInterval();
            // 删除过老的文件
            if (null != fileDeleteInterval && fileDeleteInterval > 0)
                LogFileUtil.deleteTooOldFile(sourceFile, fileDeleteInterval);
        } catch (Exception e) {
            logger.error("Get source file error: {}", e.getMessage(), e);
            fileJob.getJob().setStatus(ExecutableJob.FAILED);
            fileJob.getJob().setOptionDesc("终端获取日志文件异常：" + e.getMessage());
        }
    }

    private File getSourceFile(ExecutableFileJob fileJob) {
        File sourceFile;
        try {
            // 计算源文件夹
            String sourceDir = fileJob.getSourceDir();
            logger.info("Calculating job source file: job_id={}, source_dir={}", fileJob.getJob().getId(), sourceDir);
            if (Strings.isNullOrEmpty(fileJob.getJob().getLastRecordTime())) {
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
                // 服务器停机文件空档处理
                if (!sourceFile.exists()) {
                    Date lastRecordTime = TimeUtil.dateTime(fileJob.getJob().getLastRecordTime());
                    String baseName = LogFileUtil.getLogFileBaseName(sourceFile.getAbsolutePath());
                    if (!Strings.isNullOrEmpty(baseName)) {
                        File baseFile = new File(baseName);
                        if (baseFile.exists()) {
                            long lastModified = baseFile.lastModified();
                            // 计算出分钟间隔
                            int interval = (int) ((lastModified - lastRecordTime.getTime()) / (1000 * 60));
                            // 计算出间隔倍数
                            int multiple = interval / fileJob.getNextInterval();
                            if (multiple > 0) {
                                // 使用时间倍数换算日志后缀
                                for (int i = 1; i <= multiple; i++) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(lastRecordTime);
                                    calendar.add(Calendar.MINUTE, i * fileJob.getNextInterval());
                                    String suffix = TimeUtil.getLogSuffix(calendar.getTime());
                                    File intervalLogFile = new File(baseName + "." + suffix);
                                    if (intervalLogFile.exists()) {
                                        sourceFile = intervalLogFile;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
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
            reJob = (ExecutableFileJob) message.getBody();
            // 判断是否在拉取数据的时候就出现了错误

            if (reJob.getJob().getStatus().equals(ExecutableJob.FAILED)) {
                jobService.onFailed(reJob, "客户端错误:" + reJob.getJob().getOptionDesc());
                doError = true;
            } else {
                // 先构造存储文件
                if (null == storageFile) {
                    String storageDir = reJob.getStorageDir();
                    File tmpFile = new File(storageDir, reJob.getFileName());
                    if (tmpFile.exists()) {
                        boolean deleted = tmpFile.delete();
                        // 当文件存在，并且删除失败的时候，做错误处理，删除失败的原因可能是权限问题
                        if (!deleted) {
                            doError = true;
                            logger.error("Write file error: exists file but can not delete, file=[{}]", tmpFile.getAbsolutePath());
                            jobService.onFailed(reJob, "已存在文件:" + tmpFile.getAbsolutePath());
                            ctx.close();
                            return;
                        } else {
                            logger.info("Delete file on full file task response, file={}", tmpFile.getAbsolutePath());
                        }
                    }
                    if (!tmpFile.getParentFile().exists())
                        Files.createParentDirs(tmpFile);
                    storageFile = new RandomAccessFile(tmpFile, "rw");
                }
            }
        } else {
            // 处理附件
            if (null != storageFile) {
                if (null == message.getAttachment() || null == message.getAttachment().getData()) {
                    logger.error("Write file error, attachment is null: job_id={}", reJob.getJob().getId());
                    jobService.onFailed(reJob, "附件为空.");
                    doError = true;
                    ctx.close();
                } else if (!(message.getAttachment().getData() instanceof ByteBuf)) {
                    logger.error("Write file error, attachment type error: job_id={}", reJob.getJob().getId());
                    jobService.onFailed(reJob, "附件类型错误.");
                    doError = true;
                    ctx.close();
                } else {
                    ByteBuf buf = null;
                    try {
                        buf = (ByteBuf) message.getAttachment().getData();
                        FileChannel channel = storageFile.getChannel();
                        channel.write(buf.nioBuffers());
                    } finally {
                        if (null != buf)
                            buf.release();
                    }
                }
            } else if (!doError) {
                logger.error("Write file error, not init storage file: job_id={}", reJob.getJob().getId());
                jobService.onFailed(reJob, "未初始化用于写入的文件." + this.toString());
                doError = true;
                ctx.close();
            }
        }

        if (message.getHeader().isLastPackage()) {
            if (!doError)
                doFinish();
        }
    }

    private void doFinish() {
        boolean success = false;
        File targetFile = null;
        try {
            targetFile = new File(reJob.getStorageDir(), reJob.getFileName());
            // 判断配置是否在重新加载、判断携带版本号是否正确
            if (jobService.canUpdate(reJob)) {
                // 校验MD5
                if (reJob.getMd5().equalsIgnoreCase(MD5Checksum.getFileMD5Checksum(targetFile.getAbsolutePath()))) {
                    try {
                        String result = cmdExecutor.exec(targetFile);
                        logger.info("Import data: result={}", result);
                        Map<String, Integer> jsonMap;
                        try {
                            jsonMap = gson.fromJson(result, new TypeToken<Map<String, Integer>>() {
                            }.getType());
                        } catch (Exception e) {
                            jsonMap = Maps.newHashMap();
                            logger.error("Json format error: {}", e.getMessage(), e);
                        }
                        // 判断入库是否成功
                        if (null != jsonMap.get("code") && jsonMap.get("code") == ImportDataCmdExecutor.IMPORT_SUCCESS) {
                            success = jobService.onSuccess(reJob, reJob.getMd5());
                        } else {
                            jobService.onFailed(reJob, "入库错误：返回值=" + result);
                        }
                    } catch (Exception e) {
                        jobService.onFailed(reJob, "入库错误:" + e.getMessage());
                        logger.error("Import data error: {}", e.getMessage(), e);
                    }
                } else {
                    jobService.onFailed(reJob, "MD5校验错误!");
                }
            }
        } catch (Exception e) {
            jobService.onFailed(reJob, e.getMessage());
            logger.error("Do finish error: {}", e.getMessage(), e);
        } finally {
            if (null != storageFile) {
                try {
                    storageFile.close();
                } catch (IOException e) {
                    logger.error("关闭文件流异常:{}", e.getMessage(), e);
                }
            }
        }
        if (!success && null != targetFile && targetFile.exists()) {
            targetFile.delete();
        }
    }

    @Override
    public void handlerNettyException(String errorMessage) {
        logger.error("do error in message");
        try {
            ExecutableFileJob job = (ExecutableFileJob) message.getBody();
            jobService.onFailed(job, "Netty异常,File=" + job.getFileTask().getTaskName() + "[" + errorMessage + "]");
        } catch (Exception e) {
            logger.error("do error in message error:{}", e.getMessage(), e);
        }
    }
}
