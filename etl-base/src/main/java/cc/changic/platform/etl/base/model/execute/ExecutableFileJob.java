package cc.changic.platform.etl.base.model.execute;

import cc.changic.platform.etl.base.cache.ConfigCache;
import cc.changic.platform.etl.base.model.ETLTask;
import cc.changic.platform.etl.base.model.db.*;
import cc.changic.platform.etl.base.model.util.ETLTaskKey;
import cc.changic.platform.etl.base.model.util.GameZoneKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 可执行的文件任务
 */
public class ExecutableFileJob {

    private static Logger logger = LoggerFactory.getLogger(ExecutableFileJob.class);

    private App app;
    private GameZone gameZone;
    private ODSConfig odsConfig;
    private TaskFile taskFile;
    private Job job;

    public ExecutableFileJob(App app, GameZone gameZone, TaskFile taskFile, Job job, ODSConfig odsConfig) {
        this.app = app;
        this.gameZone = gameZone;
        this.taskFile = taskFile;
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

    public TaskFile getTaskFile() {
        return taskFile;
    }

    public Job getJob() {
        return job;
    }

    private String getClientIP() {
        return getGameZone().getEtlClientIp();
    }

    public static ExecutableFileJob buildFileJob(Job job) {
        Assert.notNull(job, "Build File-Job error:[source job is null]");

        App tmpApp = ConfigCache.getAppMap().get(job.getAppId());
        Assert.notNull(tmpApp, "Build File-Job error:[no app found, app_id=" + job.getAppId() + "]");

        GameZone tmpGameZone = ConfigCache.getGameZoneMap().get(new GameZoneKey(job.getAppId(), job.getGameZoneId()));
        Assert.notNull(tmpGameZone, "Build File-Job error:[no game_zone found, app_id=" + job.getAppId() + " and game_zone_id=" + job.getGameZoneId() + "]");

        ETLTask tmpTask = ConfigCache.getEtlTaskMap().get(new ETLTaskKey(job.getTaskId(), job.getTaskTable()));
        Assert.notNull(tmpTask, "Build File-Job error:[no task found, task_id=" + job.getTaskId() + " and task_table=" + job.getTaskTable() + "]");
        if (!(tmpTask instanceof TaskFile)) {
            logger.warn("Build File-Job warn:[found task, task_id={} and task_table={}, but not instanceof {}", job.getTaskId(), job.getTaskTable(), TaskFile.class);
            return null;
        }

        TaskFile instanceTask = (TaskFile) tmpTask;
        ODSConfig tmpODS = null;
        if (null != instanceTask.getOdsId() && instanceTask.getOdsId() > 0) {
            tmpODS = ConfigCache.getOdsConfigMap().get(instanceTask.getOdsId());
            Assert.notNull(tmpTask, "Build File-Job error:[no ods_config found, task_id=" + job.getTaskId() + " and ods_id=" + instanceTask.getOdsId() + "]");
        }

        return new ExecutableFileJob(tmpApp, tmpGameZone, instanceTask, job, tmpODS);
    }
}
