package cc.changic.platform.etl.schedule.scheduler;

import cc.changic.platform.etl.base.annotation.TaskTable;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.FileTask;
import cc.changic.platform.etl.base.model.db.GameZoneKey;
import cc.changic.platform.etl.base.model.db.Job;
import cc.changic.platform.etl.base.schedule.ETLScheduler;
import cc.changic.platform.etl.file.execute.ExecutableFileJob;
import cc.changic.platform.etl.protocol.exception.ETLException;
import cc.changic.platform.etl.schedule.cache.ConfigCache;
import cc.changic.platform.etl.schedule.job.ETLJob;
import cc.changic.platform.etl.schedule.util.ExecutableJobUtil;
import com.google.common.collect.Maps;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 任务调度器
 */
public class ETLSchedulerImpl implements ETLScheduler {

    public final static String ETL_JOB_KEY = "data";
    public final static String SPRING_CONTEXT_KEY = "spring_context";
    private final static AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private Logger logger = LoggerFactory.getLogger(ETLSchedulerImpl.class);

    private final ConcurrentMap<GameZoneKey, Queue<ExecutableJob>> queueMap = Maps.newConcurrentMap();

    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private ConfigCache cache;
    @Autowired
    private Scheduler scheduler;

    @Override
    public void init() {
        try {
            if (INITIALIZED.compareAndSet(false, true)) {
                // 初始化工作队列
                queueMap.clear();
                Map<Integer, Job> jobMap = cache.getJobMap();
                for (Job etlJob : jobMap.values()) {
                    addJob(etlJob);
                }
                scheduleOnInit();
            }
        } catch (Exception e) {
            throw new ETLException("Init ETLScheduler error", e.getCause());
        }
    }

    @Override
    public void clear() {

    }


    public ExecutableJob addJob(Job job) {
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
            executableJob = ExecutableJobUtil.buildFileJob(cache, job);
            jobs.offer(executableJob);
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
        // TODO 检查定时器中的任务数
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
            // 使用GameZoneKey.toString()作为调度组名
            String groupName = job.getGameZoneKey().toString();
            // 设置调度任务所需的数据
            JobDataMap dataMap = new JobDataMap(new HashMap<String, Object>());
            dataMap.put(ETL_JOB_KEY, job);
            dataMap.put(SPRING_CONTEXT_KEY, context);
            Integer jobID = job.getJobID();
            // 创建调度任务
            JobDetail jobDetail = newJob(ETLJob.class).withIdentity("Job[" + jobID + "]", groupName).build();
            // 创建触发器
            Trigger trigger;
            if (null == job.getNextTime()) {
                trigger = newTrigger()
                        .withIdentity("Trigger[" + jobID + "]", groupName)
                        .usingJobData(dataMap)
                        .startNow()
                        .build();
            } else {
                trigger = newTrigger()
                        .withIdentity("Trigger[" + jobID + "]", groupName)
                        .usingJobData(dataMap)
                        .startAt(job.getNextTime())
                        .build();
            }
            scheduler.scheduleJob(jobDetail, trigger);
            return true;
        } catch (Exception e) {
            logger.error("Schedule job error, job_id={}, message={}", job.getJobID(), e.getMessage(), e);
            return false;
        }
    }
}
