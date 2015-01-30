package cc.changic.platform.etl.base;

import cc.changic.platform.etl.base.cache.ConfigCache;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 基础模块适配器
 */
@Configuration
@ComponentScan(basePackages = "cc.changic.platform.etl.base")
public class ETLBaseBootstrap {

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ConfigCache configCache() {
        return new ConfigCache();
    }

}
