package cc.changic.platform.etl.schedule.util;

import cc.changic.platform.etl.base.model.ETLTask;
import cc.changic.platform.etl.base.model.db.*;
import cc.changic.platform.etl.base.model.util.ETLTaskKey;
import cc.changic.platform.etl.file.execute.ExecutableFileJob;
import cc.changic.platform.etl.schedule.cache.ConfigCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 可执行任务工具类
 * @author Panda.Z
 */
public class ExecutableJobUtil {

    static Logger LOGGER = LoggerFactory.getLogger(ExecutableJobUtil.class);

    public static ExecutableFileJob buildFileJob(ConfigCache cache, Job job, ConfigVersion version) {
        Assert.notNull(job, "Build File-Job error:[source job is null]");

        App tmpApp = cache.getAppMap().get(job.getAppId());
        Assert.notNull(tmpApp, "Build File-Job error:[no app found, app_id=" + job.getAppId() + "]");

        GameZone tmpGameZone = cache.getGameZoneMap().get(new GameZoneKey(job.getAppId(), job.getGameZoneId()));
        Assert.notNull(tmpGameZone, "Build File-Job error:[no game_zone found, app_id=" + job.getAppId() + " and game_zone_id=" + job.getGameZoneId() + "]");

        ETLTask tmpTask = cache.getEtlTaskMap().get(new ETLTaskKey(job.getTaskId(), job.getTaskTable()));
        Assert.notNull(tmpTask, "Build File-Job error:[no task found, task_id=" + job.getTaskId() + " and task_table=" + job.getTaskTable() + "]");
        if (!(tmpTask instanceof FileTask)) {
            LOGGER.warn("Build File-Job warn:[found task, task_id={} and task_table={}, but not instanceof {}", job.getTaskId(), job.getTaskTable(), FileTask.class);
            return null;
        }

        FileTask instanceTask = (FileTask) tmpTask;
        ODSConfig tmpODS = null;
        if (null != instanceTask.getOdsId() && instanceTask.getOdsId() > 0) {
            tmpODS = cache.getOdsConfigMap().get(instanceTask.getOdsId());
            Assert.notNull(tmpTask, "Build File-Job error:[no ods_config found, task_id=" + job.getTaskId() + " and ods_id=" + instanceTask.getOdsId() + "]");
        }

        return new ExecutableFileJob(tmpApp, tmpGameZone, instanceTask, job, tmpODS, version);
    }
}
