package cc.changic.platform.etl.schedule;

import cc.changic.platform.etl.base.dao.AppMapper;
import cc.changic.platform.etl.schedule.cache.ConfigCache;
import cc.changic.platform.etl.schedule.scheduler.ETLScheduler;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
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
        return new ETLScheduler();
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
            Scheduler scheduler = context.getBean(Scheduler.class);
            scheduler.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
