package cc.changic.platform.etl.protocol;

import cc.changic.platform.etl.protocol.dispatcher.MessageDispatcher;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by Panda.Z on 2015/1/19.
 */
@Configuration
@ComponentScan(basePackages = "cc.changic.platform.etl")
public class ProtocolBootstrap {

    @Bean(initMethod = "init", destroyMethod = "cleanup")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public MessageDispatcher messageDispatcher() {
        return new MessageDispatcher();
    }

}
