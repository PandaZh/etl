package cc.changic.platform.etl.schedule.scheduler;

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
import cc.changic.platform.etl.schedule.job.DataJob;
import cc.changic.platform.etl.schedule.job.VersionJob;
import cc.changic.platform.etl.schedule.util.ExecutableJobUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 任务调度器
 */
public class ETLSchedulerImpl implements ETLScheduler {

    private final static AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    private final static AtomicBoolean START_VERSION_MONITORING = new AtomicBoolean(false);
    private final static AtomicBoolean IS_RELOADING = new AtomicBoolean(false);
    private final static AtomicInteger JOB_COUNT = new AtomicInteger(0);

    private Logger logger = LoggerFactory.getLogger(ETLSchedulerImpl.class);

    private final ConcurrentMap<GameZoneKey, Queue<ExecutableJob>> queueMap = Maps.newConcurrentMap();

    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private ConfigCache cache;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ConfigVersionMapper versionMapper;
    @Autowired
    private ReloadConfig reloadConfig;
    @Autowired
    @Qualifier(ExecutableJobType.FILE_FULLY)
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
                IS_RELOADING.set(false);
                scheduleOnInit();
            }
        } catch (Exception e) {
            throw new ETLException("Init ETLScheduler error", e.getCause());
        }
    }

    @Override
    public void reload() {
        try {
            if (IS_RELOADING.compareAndSet(false, true)) {
                clear();
                cache.destroy();
                cache.caching();
                init();
            }
        } catch (Exception e) {
            logger.error("Reload configuration error:{}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isReloading() {
        return IS_RELOADING.get();
    }

    @Override
    public ConfigVersion getCurrentVersion() {
        return this.configVersion;
    }

    @Override
    public void setCurrentVersion(ConfigVersion version) {
        this.configVersion = version;
    }

    @Override
    public void clear() {
        try {
            logger.info("Clear ETL scheduler configuration");
//            scheduler.pauseAll();
            // 确保队列在调度器之前重置
            queueMap.clear();
            scheduler.clear();
            INITIALIZED.set(false);
        } catch (Exception e) {
            logger.error("Clear configuration error:{}", e.getMessage(), e);
        }
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

    @Override
    public boolean addAndScheduleJob(Job job) {
        Job cacheJob = cache.getJobMap().get(job.getId());
        cacheJob.setStatus(job.getStatus());
        cacheJob.setNextTime(job.getNextTime());
        cacheJob.setLastRecordTime(job.getLastRecordTime());
        cacheJob.setLastRecordId(job.getLastRecordId());
        cacheJob.setLastRecordOffset(job.getLastRecordOffset());
        ExecutableJob executableJob = addJob(job);
        if (null != executableJob) {
            ExecutableJob pollJob = queueMap.get(executableJob.getGameZoneKey()).poll();
            return scheduleJob(pollJob);
        }
        return false;
    }

    /**
     * 在初始化的时候开始调度任务
     */
    private void scheduleOnInit() {
        for (Map.Entry<GameZoneKey, Queue<ExecutableJob>> entry : queueMap.entrySet()) {
            GameZoneKey gameZoneKey = entry.getKey();
            Queue<ExecutableJob> jobs = entry.getValue();

            int maxRunJob = cache.getGameZoneMap().get(gameZoneKey).getMaxRunJob();
            // 如果单区的任务数大于配置的最大任务数，那么首次添加的
            maxRunJob = maxRunJob <= jobs.size() ? maxRunJob : jobs.size();
            if (maxRunJob > 1) {
                for (int i = 0; i < maxRunJob; i++) {
                    ExecutableJob job = jobs.poll();
                    if (null != job)
                        scheduleJob(job);
                }
            } else {
                ExecutableJob job = jobs.poll();
                if (null != job)
                    scheduleJob(job);
            }
        }
    }

    /**
     * 调度单个任务
     *
     * @param job 可执行的ETL任务
     */
    private boolean scheduleJob(ExecutableJob job) {
        try {
            Integer jobID = job.getJobID();
            int jobCount = JOB_COUNT.incrementAndGet();
            String jobDesc = "[job_id=" + jobID + ", job_count=" + jobCount + "]";
            // 使用GameZoneKey.toString()作为调度组名
            GameZoneKey gameZoneKey = job.getGameZoneKey();
            String groupName = gameZoneKey.toString();
            // 设置调度任务所需的数据
            JobDataMap dataMap = new JobDataMap(new HashMap<String, Object>());
            dataMap.put(DataJob.ETL_JOB_KEY, job);
            dataMap.put(DataJob.SPRING_CONTEXT_KEY, context);
            dataMap.put(DataJob.JOB_SERVICE, jobService);
            // 创建调度任务
            JobDetail jobDetail = newJob(DataJob.class).withIdentity("Job" + jobDesc, groupName).build();
            // 创建触发器
            Trigger trigger;
            if (null == job.getNextTime()) {
                trigger = newTrigger()
                        .withIdentity("Trigger" + jobDesc, groupName)
                        .usingJobData(dataMap)
                        .startNow()
                        .build();
            } else {
                // 避免任务暴增，当数据库下一次时间小于当前时间时，任务的实际执行时间为当前时间后5秒
                Calendar nextTime = Calendar.getInstance();
                nextTime.setTime(job.getNextTime());
                Calendar now = Calendar.getInstance();
                if (now.compareTo(nextTime) >= 0) {
                    now.add(Calendar.SECOND, 5);
                    nextTime = now;
                }
                trigger = newTrigger()
                        .withIdentity("Trigger" + jobDesc, groupName)
                        .usingJobData(dataMap)
                        .startAt(nextTime.getTime())
                        .build();
            }
//            Set<Trigger> triggers = Sets.newHashSet();
//            triggers.add(trigger);
            if (!IS_RELOADING.get()) {
                logger.info("Schedule job queue={}, job={}", gameZoneKey, job.toString());
//                if (scheduler.checkExists(jobDetail.getKey())) {
//                    logger.info("删除JOB,job={}, job_key={}", job.toString(), jobDetail.getKey());
//                    scheduler.deleteJob(jobDetail.getKey());
//                }
                scheduler.scheduleJob(jobDetail, trigger);
//                scheduler.scheduleJob(jobDetail, triggers, true);
                return true;
            }
        } catch (Exception e) {
            logger.error("Schedule job error, job_id={}, message={}", job.getJobID(), e.getMessage(), e);
        }
        return false;
    }
}
