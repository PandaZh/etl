package cc.changic.platform.etl.file.service;

import cc.changic.platform.etl.base.common.ExecutableJobType;
import cc.changic.platform.etl.base.dao.JobLogMapper;
import cc.changic.platform.etl.base.dao.JobMapper;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.Job;
import cc.changic.platform.etl.base.model.db.JobLog;
import cc.changic.platform.etl.base.schedule.ETLScheduler;
import cc.changic.platform.etl.base.service.JobService;
import cc.changic.platform.etl.base.util.LogFileUtil;
import cc.changic.platform.etl.base.util.TimeUtil;
import cc.changic.platform.etl.file.execute.ExecutableFileJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * 全量拉取作业的服务类实现
 *
 * @author Panda.Z
 */
@Service(value = ExecutableJobType.FILE_FULLY)
public class FullFileJobServiceImpl extends JobService {

    private Logger logger = LoggerFactory.getLogger(FullFileJobServiceImpl.class);

    @Autowired(required = false)
    private JobMapper jobMapper;
    @Autowired(required = false)
    private JobLogMapper logMapper;
    @Autowired(required = false)
    private ETLScheduler etlScheduler;

    @Override
    public boolean onJobSuccess(ExecutableJob executableJob, String desc) {
        Assert.isInstanceOf(ExecutableFileJob.class, executableJob, "FullFileJobError:");
        Job job = null;
        boolean success = false;
        try {
            ExecutableFileJob fileJob = (ExecutableFileJob) executableJob;
            job = fileJob.getJob();
            job.setStatus(ExecutableJob.SUCCESS);
            String fileName = new File(fileJob.getSourceDir(), fileJob.getFileName()).getAbsolutePath();
            job.setModifyTime(TimeUtil.dateTime(new Date()));
            // 计算最后记录时间
            Date logTime;
            try {
                logTime = LogFileUtil.getLogFileTimestamp(fileName);
            } catch (Exception e) {
                onJobFailed(executableJob, "日志文件时间格式错误");
                logger.error("时间格式转换错误:job_id={},{}", job.getId(), e.getMessage(), e);
                // return 确保不会影响到已存在的数据
                return false;
            }
            job.setLastRecordTime(TimeUtil.dateTime(logTime));
            // 计算下一次执行时间，如果本身NextTime不为空，依据NextTime计算，否则根据最后拉取时间计算
            Calendar calendar = Calendar.getInstance();
            if (null != job.getNextTime()) {
                calendar.setTime(TimeUtil.dateTime(job.getNextTime()));
            } else {
                calendar.setTime(logTime);
            }
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.MINUTE, executableJob.getNextInterval() + random());
            job.setNextTime(TimeUtil.dateTime(calendar.getTime()));
            String tmpDesc = "File=[" + fileName + "], desc=[" + desc + "], next_time=[" + job.getNextTime() + "], last_record_time=[" + job.getLastRecordTime() + "]";
            job.setOptionDesc(tmpDesc);
            jobMapper.updateByPrimaryKey(job);
            JobLog jobLog = buildLog(job, executableJob.getJobType());
            logMapper.insert(jobLog);
            success = true;
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
        }
        if (null != job)
            etlScheduler.addAndScheduleJob(job);
        else
            logger.error("调度任务时Job=null");
        return success;
    }

    @Override
    public boolean onJobFailed(ExecutableJob executableJob, String desc) {
        Assert.isInstanceOf(ExecutableFileJob.class, executableJob, "FullFileJobError:");
        Job job = null;
        boolean success = false;
        try {
            job = executableJob.getJob();
            job.setStatus(ExecutableJob.FAILED);
            job.setOptionDesc(desc);
            job.setModifyTime(TimeUtil.dateTime(new Date()));
            if (null == job.getNextTime()) {
                job.setNextTime(TimeUtil.dateTime(TimeUtil.getLogSuffix(TimeUtil.getLogSuffix(new Date()))));
            }
            Calendar next = Calendar.getInstance();
            next.setTime(TimeUtil.dateTime(job.getNextTime()));
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, executableJob.getNextInterval());
            // 如果NextTime大于当前时间+时间间隔，那NextTime不需要再做运算
            if (next.compareTo(now) < 0) {
                next.set(Calendar.MINUTE, 0);
                next.add(Calendar.MINUTE, executableJob.getNextInterval() + random());
            }
            job.setNextTime(TimeUtil.dateTime(next.getTime()));
            jobMapper.updateByPrimaryKey(job);
            JobLog jobLog = buildLog(job, executableJob.getJobType());
            logMapper.insert(jobLog);
            success = true;
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
        }
        if (null != job)
            etlScheduler.addAndScheduleJob(job);
        else
            logger.error("调度任务时Job=null");
        return success;
    }


}
