package cc.changic.platform.etl.base.service;

import cc.changic.platform.etl.base.dao.JobLogMapper;
import cc.changic.platform.etl.base.dao.JobMapper;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.Job;
import cc.changic.platform.etl.base.model.db.JobLog;
import cc.changic.platform.etl.base.schedule.ETLScheduler;
import cc.changic.platform.etl.base.util.LogFileUtil;
import cc.changic.platform.etl.base.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Panda.Z on 2015/1/31.
 */
@Component
public class JobServiceImpl {

    private Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired(required = false)
    private JobMapper jobMapper;
    @Autowired(required = false)
    private JobLogMapper logMapper;
    @Autowired(required = false)
    private ETLScheduler etlScheduler;

    public boolean doIncrementalFileSuccess(Job job, Short taskType, Short nextInterval, long offset, String fileName) {
        try {
            job.setOptionDesc("SUCCESS: " + fileName);
            job.setModifyTime(new Date());
            job.setLastRecordOffset(offset);
            if (null == job.getNextTime()) {
                job.setNextTime(new Date());
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(job.getNextTime());
            calendar.add(Calendar.MINUTE, nextInterval);
            job.setNextTime(calendar.getTime());
            jobMapper.updateByPrimaryKey(job);
            JobLog jobLog = buildLog(job, taskType);
            logMapper.insert(jobLog);
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
        }
        // TODO 下一次任务的调度应当无论代码是否异常都会进行
        return etlScheduler.addAndScheduleJob(job);
    }

    public boolean doFileSuccess(Job job, Short taskType, String fileName, String desc, Short nextInterval) {
        try {
            job.setStatus(ExecutableJob.SUCCESS);
            job.setOptionDesc("File=[" + fileName + "], desc=[" + desc + "]");
            job.setModifyTime(new Date());
            // 计算最后记录时间
            Date logTime;
            try {
                logTime = LogFileUtil.getLogFileTimestamp(fileName);
            } catch (Exception e) {
                doError(job, taskType, nextInterval, "日志文件时间格式错误");
                logger.error("时间格式转换错误:job_id={},{}", job.getId(), e.getMessage(), e);
                // return 确保不会影响到已存在的数据
                return false;
            }
            job.setLastRecordTime(logTime);
            // 计算下一次执行时间，如果本身NextTime不为空，依据NextTime计算，否则根据最后拉取时间计算
            Calendar calendar = Calendar.getInstance();
            if (null != job.getNextTime()){
                calendar.setTime(job.getNextTime());
            }else{
                calendar.setTime(logTime);
            }
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.MINUTE, nextInterval + random());
            job.setNextTime(calendar.getTime());
            jobMapper.updateByPrimaryKey(job);
            JobLog jobLog = buildLog(job, taskType);
            logMapper.insert(jobLog);
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
        }
        return etlScheduler.addAndScheduleJob(job);
    }

    public void doError(Job job, Short taskType, Short nextInterval, String message) {
        try {
            job.setStatus(ExecutableJob.FAILED);
            job.setOptionDesc(message);
            job.setModifyTime(new Date());
            if (null == job.getNextTime()) {
                job.setNextTime(TimeUtil.getLogSuffix(TimeUtil.getLogSuffix(new Date())));
            }
            Calendar next = Calendar.getInstance();
            next.setTime(job.getNextTime());
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, nextInterval);
            // 如果NextTime大于当前时间+时间间隔，那NextTime不需要再做运算
            if (next.compareTo(now) < 0){
                next.set(Calendar.MINUTE, 0);
                next.add(Calendar.MINUTE, nextInterval + random());
            }
            job.setNextTime(next.getTime());
            jobMapper.updateByPrimaryKey(job);
            JobLog jobLog = buildLog(job, taskType);
            logMapper.insert(jobLog);
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
        }
        etlScheduler.addAndScheduleJob(job);
    }

    private JobLog buildLog(Job job, Short type) {
        JobLog log = new JobLog();
        log.setJobId(job.getId());
        log.setStatus(job.getStatus());
        log.setOptionDesc(job.getOptionDesc());
        log.setType(type);
        log.setCreateTime(new Date());
        return log;
    }

    private int random() {
        return (int) Math.round(Math.random() * (55 - 5) + 5);
    }
}
