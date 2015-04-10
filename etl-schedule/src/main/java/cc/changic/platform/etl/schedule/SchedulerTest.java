package cc.changic.platform.etl.schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by Panda.Z on 2015/4/9.
 */
public class SchedulerTest {

    static Logger logger = LoggerFactory.getLogger(SchedulerTest.class);
    public static void main(String[] args) {
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();
            scheduler.start();
            scheduler.pauseAll();
            scheduler.clear();
            for (int i = 0; i < 2; i++) {
                JobDataMap dataMap = new JobDataMap(new HashMap<String, Object>());
                dataMap.put("count", i);
                JobDetail job = JobBuilder.newJob(HelloJob.class)
                        .withIdentity("job" + i, "group" + i)
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity("trigger" + i, "group" + i)
                        .usingJobData(dataMap)
                        .startNow()
                        .build();
                scheduler.scheduleJob(job, trigger);
            }
//            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }





    public static class HelloJob implements Job {

        public void execute(final JobExecutionContext context) throws JobExecutionException {
            JobDataMap dataMap = context.getMergedJobDataMap();
            final Integer count = (Integer) dataMap.get("count");
            logger.info("Count={}, Thread={}", count, Thread.currentThread().getName());
        }
    }
}
