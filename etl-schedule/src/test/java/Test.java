//package cc.changic.test;
//
//import cc.changic.platform.etl.protocol.dispatcher.MessageDispatcher;
//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import java.util.Calendar;
//import java.util.HashMap;
//
//import static org.quartz.JobBuilder.newJob;
//import static org.quartz.TriggerBuilder.newTrigger;
//
///**
// * Created by Panda.Z on 2015/4/1.
// */
//public class Test {
//
//    private static Logger logger = LoggerFactory.getLogger(Test.class);
//    public final static String JOB_SCHEDULER = "job_scheduler";
//    public final static String VERSION_SCHEDULER = "version_scheduler";
//
//
////    @Autowired
////    private StdSchedulerFactory schedulerFactory;
//
//
////    @Bean(name = JOB_SCHEDULER, destroyMethod = "clear")
////    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
////    public Scheduler Scheduler() throws SchedulerException {
////        return schedulerFactory.getScheduler();
////    }
////
////    @Bean(name = VERSION_SCHEDULER, destroyMethod = "clear")
////    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
////    public Scheduler VersionScheduler() throws SchedulerException {
////        return schedulerFactory.getScheduler();
////    }
//
//    public static void main(String[] args) {
//        ConfigurableApplicationContext context = null;
//        try {
//            context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
//            for (int i = 0; i < 2; i++) {
//                JobDataMap dataMap = new JobDataMap(new HashMap<String, Object>());
//                dataMap.put("count", i);
//                JobDetail job = newJob(HelloJob.class)
//                        .withIdentity("job" + i, "group" + i)
//                        .build();
//
//                Trigger trigger = newTrigger()
//                        .withIdentity("trigger" + i, "group" + i)
//                        .usingJobData(dataMap)
//                        .withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?"))
//                        .build();
//                if (i == 0) {
//                    Scheduler scheduler = context.getBean("schedulerFactory", StdSchedulerFactory.class).getScheduler();
//                    scheduler.start();
//                    scheduler.scheduleJob(job, trigger);
//                } else {
//                    Scheduler scheduler = context.getBean("schedulerFactory2", StdSchedulerFactory.class).getScheduler();
//                    scheduler.start();
//                    scheduler.scheduleJob(job, trigger);
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error("{}", e.getMessage(), e);
//            if (null != context)
//                context.close();
//            System.exit(-1);
//        }
//
//
//    }
//
//    public class Run {
//        void test(String name) {
//            try {
//                Scheduler scheduler = new StdSchedulerFactory().getScheduler();
//                scheduler.start();
//                for (int i = 0; i < 1; i++) {
//                    JobDataMap dataMap = new JobDataMap(new HashMap<String, Object>());
//                    dataMap.put("count", i);
//                    JobDetail job = newJob(HelloJob.class)
//                            .withIdentity("job" + i + name, "group" + i + name)
//                            .build();
//
//                    Trigger trigger = null;
//                    if (i == 0) {
//                        trigger = newTrigger()
//                                .withIdentity("trigger" + i + name, "group" + i + name)
//                                .usingJobData(dataMap)
//                                .withSchedule(CronScheduleBuilder.cronSchedule("*/10 * * * * ?"))
//                                .build();
//                    } else {
//                        Calendar instance = Calendar.getInstance();
//                        instance.add(Calendar.SECOND, 10);
//                        trigger = newTrigger()
//                                .withIdentity("trigger" + i + name, "group" + i + name)
//                                .usingJobData(dataMap)
//                                .startAt(instance.getTime())
//                                .build();
//                    }
//                    scheduler.scheduleJob(job, trigger);
//                }
//
//                Trigger trigger = scheduler.getTrigger(new TriggerKey("trigger0", "group0"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static class HelloJob implements Job {
//        public void execute(final JobExecutionContext context) throws JobExecutionException {
//            JobDataMap dataMap = context.getMergedJobDataMap();
//            final Integer count = (Integer) dataMap.get("count");
//            logger.info("Count={}, Thread={}", count, Thread.currentThread().getName());
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
//////                    while (true) {
////                    try {
////                        Thread.sleep(1000);
////                        logger.info("Count={}, Thread={}", count, Thread.currentThread().getName());
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
//////                    }
////                }
////            }).start();
//        }
//    }
//}
