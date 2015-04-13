package cc.changic.platform.etl.base.dao;

import cc.changic.platform.etl.base.model.db.ConfigVersion;

public interface ConfigVersionMapper {

    /**
     * 获取最新的配置版本
     *
     * @return 最新的配置版本
     */
    ConfigVersion selectLatest();

}