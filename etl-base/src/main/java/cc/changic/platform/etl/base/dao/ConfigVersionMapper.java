package cc.changic.platform.etl.base.dao;

import cc.changic.platform.etl.base.model.db.ConfigVersion;

public interface ConfigVersionMapper {

    /**
     * 获取最新的配置版本
     *
     * @return 最新的配置版本
     */
    ConfigVersion selectLatest();

    /**
     * 修改配置版本状态
     *
     * @param record 配置版本
     * @return 受影响的记录数
     */
    int updateByPrimaryKey(ConfigVersion record);
}