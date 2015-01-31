package cc.changic.platform.etl.file.execute;

import cc.changic.platform.etl.base.cache.ConfigCache;
import cc.changic.platform.etl.base.model.ETLTask;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.*;
import cc.changic.platform.etl.base.model.util.ETLTaskKey;
import cc.changic.platform.etl.base.model.util.GameZoneKey;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import static cc.changic.platform.etl.file.util.LogFileUtil.*;

/**
 * 可执行的文件任务
 */
public class ExecutableFileJob implements ExecutableJob {

    private static Logger logger = LoggerFactory.getLogger(ExecutableFileJob.class);

    // 构造函数数据
    private App app;
    private GameZone gameZone;
    private ODSConfig odsConfig;
    private FileTask fileTask;
    private Job job;

    // 根据构造函数数据计算出的数据
    private String fileName;
    private String sourceDir;
    private String storageDir;

    public ExecutableFileJob(App app, GameZone gameZone, FileTask fileTask, Job job, ODSConfig odsConfig) {
        this.app = app;
        this.gameZone = gameZone;
        this.fileTask = fileTask;
        this.job = job;
        this.odsConfig = odsConfig;
    }

    public App getApp() {
        return app;
    }

    public GameZone getGameZone() {
        return gameZone;
    }

    public ODSConfig getOdsConfig() {
        return odsConfig;
    }

    public FileTask getFileTask() {
        return fileTask;
    }

    public Job getJob() {
        return job;
    }

    public String getClientIP() {
        return getGameZone().getEtlClientIp();
    }

    public String getFileName() {
        if (Strings.isNullOrEmpty(fileName)) {
            fileName = getNextLogFileName(getFileTask().getFileName(), getFileTask().getNextInterval(), getJob().getLastRecordTime());
        }
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSourceDir() throws NoSuchFieldException, IllegalAccessException {
        if (Strings.isNullOrEmpty(sourceDir)){
            sourceDir = getLogFileDir(getFileTask().getSourceDir(), getJob()) ;
        }
        return sourceDir;
    }

    public String getStorageDir() throws NoSuchFieldException, IllegalAccessException {
        if (Strings.isNullOrEmpty(storageDir)){
            storageDir = getLogFileDir(getFileTask().getStorageDir(), getJob());
        }
        return storageDir;
    }

    public static ExecutableFileJob buildFileJob(Job job) {
        Assert.notNull(job, "Build File-Job error:[source job is null]");

        App tmpApp = ConfigCache.getAppMap().get(job.getAppId());
        Assert.notNull(tmpApp, "Build File-Job error:[no app found, app_id=" + job.getAppId() + "]");

        GameZone tmpGameZone = ConfigCache.getGameZoneMap().get(new GameZoneKey(job.getAppId(), job.getGameZoneId()));
        Assert.notNull(tmpGameZone, "Build File-Job error:[no game_zone found, app_id=" + job.getAppId() + " and game_zone_id=" + job.getGameZoneId() + "]");

        ETLTask tmpTask = ConfigCache.getEtlTaskMap().get(new ETLTaskKey(job.getTaskId(), job.getTaskTable()));
        Assert.notNull(tmpTask, "Build File-Job error:[no task found, task_id=" + job.getTaskId() + " and task_table=" + job.getTaskTable() + "]");
        if (!(tmpTask instanceof FileTask)) {
            logger.warn("Build File-Job warn:[found task, task_id={} and task_table={}, but not instanceof {}", job.getTaskId(), job.getTaskTable(), FileTask.class);
            return null;
        }

        FileTask instanceTask = (FileTask) tmpTask;
        ODSConfig tmpODS = null;
        if (null != instanceTask.getOdsId() && instanceTask.getOdsId() > 0) {
            tmpODS = ConfigCache.getOdsConfigMap().get(instanceTask.getOdsId());
            Assert.notNull(tmpTask, "Build File-Job error:[no ods_config found, task_id=" + job.getTaskId() + " and ods_id=" + instanceTask.getOdsId() + "]");
        }

        return new ExecutableFileJob(tmpApp, tmpGameZone, instanceTask, job, tmpODS);
    }
}
