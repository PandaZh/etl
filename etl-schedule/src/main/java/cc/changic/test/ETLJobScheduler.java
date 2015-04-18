package cc.changic.test;

import cc.changic.platform.etl.base.annotation.TaskTable;
import cc.changic.platform.etl.base.common.ExecutableJobType;
import cc.changic.platform.etl.base.dao.ConfigVersionMapper;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.ConfigVersion;
import cc.changic.platform.etl.base.model.db.FileTask;
import cc.changic.platform.etl.base.model.db.GameZoneKey;
import cc.changic.platform.etl.base.model.db.Job;
import cc.changic.platform.etl.base.schedule.ETLScheduler;
import cc.changic.platform.etl.base.service.JobService;
import cc.changic.platform.etl.file.execute.ExecutableFileJob;
import cc.changic.platform.etl.protocol.exception.ETLException;
import cc.changic.platform.etl.schedule.cache.ConfigCache;
import cc.changic.platform.etl.schedule.job.VersionJob;
import cc.changic.platform.etl.schedule.scheduler.ReloadConfig;
import cc.changic.platform.etl.schedule.util.ExecutableJobUtil;
import com.google.common.collect.Maps;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Panda.Z
 */
public class ETLJobScheduler implements ETLScheduler {

    private final static AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    private final static AtomicBoolean START_VERSION_MONITORING = new AtomicBoolean(false);
    private final static AtomicBoolean IS_LOADING = new AtomicBoolean(false);
    private final static AtomicInteger JOB_COUNT = new AtomicInteger(0);

    private Logger logger = LoggerFactory.getLogger(ETLJobScheduler.class);

    private final ConcurrentMap<GameZoneKey, Queue<ExecutableJob>> queueMap = Maps.newConcurrentMap();

//    @Autowired
    private ConfigurableApplicationContext context;
//    @Autowired
    private ConfigCache cache;
//    @Autowired
    private ConfigVersionMapper versionMapper;
//    @Autowired
    private ReloadConfig reloadConfig;
//    @Autowired
//    @Qualifier(ExecutableJobType.FILE_FULLY)
    private JobService jobService;

    private ConfigVersion configVersion;

    @Override
    public void init() {
        try {
            if (START_VERSION_MONITORING.compareAndSet(false, true)) {
                configVersion = versionMapper.selectLatest();
                Assert.notNull(configVersion, "初始化配置版本信息错误,版本信息为空");
                new Timer("Config_Version_Job").schedule(new VersionJob(versionMapper, this, reloadConfig), 0, reloadConfig.getInterval() * 1000);
            }
            if (INITIALIZED.compareAndSet(false, true)) {
                logger.info("Initialize ETL scheduler configuration");
                // 初始化工作队列
                queueMap.clear();
//                scheduler.start();
//                scheduler.resumeAll();
                Map<Integer, Job> jobMap = cache.getJobMap();
                for (Job etlJob : jobMap.values()) {
                    addJob(etlJob);
                }
//                IS_RELOADING.set(false);
//                scheduleOnInit();
            }
        } catch (Exception e) {
            throw new ETLException("Init ETLScheduler error", e.getCause());
        }
    }

    @Override
    public void reload() {
        clear();
        boolean loadSuccess = false;
        if (IS_LOADING.compareAndSet(false, true)) {
            try {
                cache.caching();
                Map<Integer, Job> jobMap = cache.getJobMap();
                for (Job etlJob : jobMap.values()) {
                    addJob(etlJob);
                }
                loadSuccess = true;
            } catch (Exception e) {
                logger.error("加载配置异常");
            }
            IS_LOADING.set(false);
        }
        if (loadSuccess) {
            //TODO 调度任务
        }
    }

    @Override
    public boolean addAndScheduleJob(Job job) {
        return false;
    }

    @Override
    public boolean isReloading() {
        return IS_LOADING.get();
    }

    @Override
    public void clear() {
        cache.destroy();
        queueMap.clear();
    }

    @Override
    public ConfigVersion getCurrentVersion() {
        return configVersion;
    }

    @Override
    public void setCurrentVersion(ConfigVersion version) {
        this.configVersion = version;
    }

    private ExecutableJob addJob(Job job) {
        // 工作队列按照游戏区分组
        GameZoneKey gameZoneKey = new GameZoneKey(job.getAppId(), job.getGameZoneId());
        // 使用优先级队列,时间越小优先级越高
        queueMap.putIfAbsent(gameZoneKey, new PriorityBlockingQueue<ExecutableJob>());
        // 获取分组队列
        Queue<ExecutableJob> jobs = queueMap.get(gameZoneKey);
        // 暂时只处理文件任务
        String taskTable = job.getTaskTable();
        ExecutableFileJob executableJob = null;
        if (taskTable.equals(FileTask.class.getAnnotation(TaskTable.class).tableName())) {
            executableJob = ExecutableJobUtil.buildFileJob(cache, job, configVersion);
            jobs.offer(executableJob);
            logger.info("Add job to queue={}, job={}", gameZoneKey, executableJob.toString());
        } else {
            logger.warn("Not support task_table={} and job_id={}", job.getTaskTable(), job.getId());
        }
        return executableJob;
    }
}
