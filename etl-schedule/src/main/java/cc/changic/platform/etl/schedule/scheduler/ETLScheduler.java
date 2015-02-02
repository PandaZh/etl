package cc.changic.platform.etl.schedule.scheduler;

import cc.changic.platform.etl.base.annotation.TaskTable;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.FileTask;
import cc.changic.platform.etl.base.model.db.Job;
import cc.changic.platform.etl.base.model.util.GameZoneKey;
import cc.changic.platform.etl.file.execute.ExecutableFileJob;
import cc.changic.platform.etl.schedule.cache.ConfigCache;
import cc.changic.platform.etl.schedule.job.ETLJob;
import cc.changic.platform.etl.schedule.util.ExecutableJobUtil;
import com.google.common.collect.Maps;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 任务调度器
 */
public class ETLScheduler {
    public final static String ETL_JOB_KEY = "data";
    public final static String SPRING_CONTEXT_KEY = "spring_context";

    private final ConcurrentMap<GameZoneKey, Queue<ExecutableJob>> queueMap = Maps.newConcurrentMap();
    private final int maxJobInGameZone = 2;
    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private ConfigCache cache;
    @Autowired
    private Scheduler scheduler;

    public void init() throws SchedulerException {
        // 初始化工作队列
        queueMap.clear();
        Map<Integer, Job> jobMap = cache.getJobMap();
        for (Job etlJob : jobMap.values()) {
            // 工作队列按照游戏区分组
            GameZoneKey gameZoneKey = new GameZoneKey(etlJob.getAppId(), etlJob.getGameZoneId());

            // 使用优先级队列,时间越小优先级越高
            if (!queueMap.containsKey(gameZoneKey))
                queueMap.putIfAbsent(gameZoneKey, new PriorityBlockingQueue<ExecutableJob>());
            Queue<ExecutableJob> jobs = queueMap.get(gameZoneKey);

            // 暂时只处理文件任务
            String taskTable = etlJob.getTaskTable();
            if (taskTable.equals(FileTask.class.getAnnotation(TaskTable.class).tableName())) {
                ExecutableFileJob executableJob = ExecutableJobUtil.buildFileJob(cache, etlJob);
                jobs.offer(executableJob);
            }
        }

        for (Map.Entry<GameZoneKey, Queue<ExecutableJob>> entry : queueMap.entrySet()) {
            // 使用GameZoneKey.toString()作为调度组名
            String groupName = entry.getKey().toString();
            Queue<ExecutableJob> queue = entry.getValue();
            for (int i = 0; i < maxJobInGameZone; i++) {
                ExecutableJob executableJob = queue.poll();
                if (null == executableJob)
                    continue;
                JobDataMap dataMap = new JobDataMap(new HashMap<String, Object>());
                dataMap.put(ETL_JOB_KEY, executableJob);
                dataMap.put(SPRING_CONTEXT_KEY, context);
                Integer jobID = executableJob.getJobID();
                // 创建调度任务
                JobDetail job = newJob(ETLJob.class).withIdentity("Job[" + jobID + "]", groupName).build();
                // 创建触发器
                Trigger trigger;
                if (null == executableJob.getNextTime()) {
                    trigger = newTrigger()
                            .withIdentity("Trigger[" + jobID + "]", groupName)
                            .usingJobData(dataMap)
                            .startNow()
                            .build();
                } else {
                    trigger = newTrigger()
                            .withIdentity("Trigger[" + jobID + "]", groupName)
                            .usingJobData(dataMap)
                            .startAt(executableJob.getNextTime())
                            .build();
                }
                scheduler.scheduleJob(job, trigger);
            }
        }
    }

    public void clear() {

    }
}
