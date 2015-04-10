package cc.changic.platform.etl.base.schedule;

import cc.changic.platform.etl.base.model.db.ConfigVersion;
import cc.changic.platform.etl.base.model.db.Job;

/**
 * ETL任务调度器接口
 */
public interface ETLScheduler {

    /**
     * 初始化调度信息
     */
    void init();

    /**
     * 重新加载配置信息
     */
    void reload();

    /**
     * 判断是否正在加载配置信息
     *
     * @return true(正在加载)/false(加载完成)
     */
    boolean isReloading();

    /**
     * 清空配置信息
     */
    void clear();

    /**
     * 添加一个任务到调度器,该操作会在添加的同时从任务队列中选出优先级最高的任务进行调度,并且会刷新缓存中的Job属性
     *
     * @param job 任务
     * @return 添加失败|调度失败返回false,其他返回true
     */
    boolean addAndScheduleJob(Job job);

    /**
     * 获取当前内存中的配置版本
     */
    ConfigVersion getCurrentVersion();

    /**
     * 设置当前内存中的配置版本
     *
     * @param version 指定的版本号
     */
    void setCurrentVersion(ConfigVersion version);

}
