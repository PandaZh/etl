package cc.changic.platform.etl.schedule;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Panda.Z on 2015/1/29.
 */
@Configuration
@ComponentScan(basePackages = "cc.changic.platform.etl")
public class ETLScheduleBootstrap {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
//            context.getBean(HttpServer.class).bind();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
