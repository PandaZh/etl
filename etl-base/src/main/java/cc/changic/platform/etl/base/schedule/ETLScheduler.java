package cc.changic.platform.etl.base.schedule;

import cc.changic.platform.etl.base.model.db.Job;

/**
 * ETL任务调度器接口
 */
public interface ETLScheduler {

    void init();

    void clear();

    /**
     * 添加一个任务到调度器,该操作会在添加的同时从任务队列中选出优先级最高的任务进行调度,并且会刷新缓存中的Job属性
     *
     * @param job 任务
     * @return 添加失败|调度失败返回false,其他返回true
     */
    boolean addAndScheduleJob(Job job);
}
