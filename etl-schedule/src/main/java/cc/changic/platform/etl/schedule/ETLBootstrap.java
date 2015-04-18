package cc.changic.platform.etl.schedule;

import cc.changic.platform.etl.base.schedule.ETLScheduler;
import cc.changic.platform.etl.protocol.dispatcher.MessageDispatcher;
import cc.changic.platform.etl.schedule.cache.ConfigCache;
import cc.changic.platform.etl.schedule.listener.ETLJobListener;
import cc.changic.platform.etl.schedule.scheduler.ETLSchedulerImpl;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Panda.Z on 2015/1/29.
 */
@Configuration
@ComponentScan(basePackages = "cc.changic.platform.etl")
public class ETLBootstrap {

    public final static String JOB_SCHEDULER = "job_scheduler";
    public final static String VERSION_SCHEDULER = "version_scheduler";


    static Logger logger = LoggerFactory.getLogger(ETLBootstrap.class);

    @Autowired
    private StdSchedulerFactory schedulerFactory;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ConfigCache configCache() {
        return new ConfigCache();
    }

    @Bean(destroyMethod = "clear")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Scheduler Scheduler() throws SchedulerException {
        return schedulerFactory.getScheduler();
    }

    @Bean(initMethod = "init", destroyMethod = "clear")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ETLScheduler ETLScheduler() throws SchedulerException {
        return new ETLSchedulerImpl();
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
            context.getBean(MessageDispatcher.class);
            Scheduler scheduler = context.getBean(Scheduler.class);
            scheduler.getListenerManager().addJobListener(new ETLJobListener());
            scheduler.start();
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
            if (null != context)
                context.close();
            System.exit(-1);
        }
    }
}
