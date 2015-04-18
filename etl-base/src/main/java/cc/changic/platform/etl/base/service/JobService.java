package cc.changic.platform.etl.base.service;

import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.Job;
import cc.changic.platform.etl.base.model.db.JobLog;
import cc.changic.platform.etl.base.schedule.ETLScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 可执行作业的相关接口
 *
 * @author Panda.Z
 */
public abstract class JobService {

    private Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired(required = false)
    private ETLScheduler scheduler;

    protected abstract boolean onJobSuccess(ExecutableJob executableJob, String desc);

    protected abstract boolean onJobFailed(ExecutableJob executableJob, String desc);

    /**
     * 当作业执行[成功]时调用,调用之后会向任务队列添加新任务
     *
     * @param job  可执行作业
     * @param desc 描述
     * @return true(成功)/false(失败)
     */
    public boolean onSuccess(ExecutableJob job, String desc) {
        if (canUpdate(job))
            return onJobSuccess(job, desc);
        else
            return false;
    }

    /**
     * 当作业执行[失败]时调用,调用之后会向任务队列添加新任务
     *
     * @param job  可执行作业
     * @param desc 描述
     * @return true(成功)/false(失败)
     */
    public boolean onFailed(ExecutableJob job, String desc) {
        if (canUpdate(job))
            return onJobFailed(job, desc);
        else
            return false;
    }

    /**
     * 判断指定的可执行任务是否能够对本地数据进行更改
     *
     * @return true(可以)/false(不可以)
     */
    public final boolean canUpdate(ExecutableJob job) {
        if (null == scheduler){
            logger.warn("不可更新本地数据:Scheduler为null");
            return false;
        }
        if (scheduler.isReloading()){
            logger.warn("不可更新本地数据:Scheduler正在重新加载配置");
            return false;
        }
        if (null == scheduler.getCurrentVersion()){
            logger.warn("不可更新本地数据:Scheduler当前版本配置为null");
            return false;
        }
        if (null == job){
            logger.warn("不可更新本地数据:Job为null");
            return false;
        }
        if (null == job.getConfigVersion()){
            logger.warn("不可更新本地数据:Job版本配置为null");
            return false;
        }
        if (job.getConfigVersion().equals(scheduler.getCurrentVersion()))
            return true;
        else
            logger.warn("不可更新本地数据:配置版本不匹配当前版本{},携带版本{}", scheduler.getCurrentVersion(), job.getConfigVersion());
        return false;
    }

    /**
     * 构建Job日志
     */
    protected final JobLog buildLog(Job job, Short type) {
        JobLog log = new JobLog();
        log.setJobId(job.getId());
        log.setStatus(job.getStatus());
        log.setOptionDesc(job.getOptionDesc());
        log.setType(type);
        log.setCreateTime(new Date());
        return log;
    }

    /**
     * 获取一个小时之内的可执行时间(每个小时分成6份,其中可执行时间为0~5分钟,其余时间视为可重新加载配置时间)<br>
     * PS:可加载配置的时间最好设为8~10分钟
     */
    protected final int random() {
        int tensDigit = (int) (Math.random() * 6);
        int singleDigit = (int) (Math.random() * 6);
        return tensDigit * 10 + singleDigit;
    }
}
