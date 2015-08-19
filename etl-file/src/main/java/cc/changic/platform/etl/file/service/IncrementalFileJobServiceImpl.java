package cc.changic.platform.etl.file.service;

import cc.changic.platform.etl.base.common.ExecutableJobType;
import cc.changic.platform.etl.base.dao.JobLogMapper;
import cc.changic.platform.etl.base.dao.JobMapper;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.Job;
import cc.changic.platform.etl.base.model.db.JobLog;
import cc.changic.platform.etl.base.schedule.ETLScheduler;
import cc.changic.platform.etl.base.service.JobService;
import cc.changic.platform.etl.base.util.TimeUtil;
import cc.changic.platform.etl.file.execute.ExecutableFileJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;

/**
 * 增量拉取作业的服务类实现
 *
 * @author Panda.Z
 */
@Service(value = ExecutableJobType.FILE_INCREMENTALLY)
public class IncrementalFileJobServiceImpl extends JobService {

    private Logger logger = LoggerFactory.getLogger(IncrementalFileJobServiceImpl.class);

    @Autowired(required = false)
    private JobMapper jobMapper;
    @Autowired(required = false)
    private JobLogMapper logMapper;
    @Autowired(required = false)
    private ETLScheduler etlScheduler;

    @Override
    protected boolean onJobSuccess(ExecutableJob executableJob, String desc) {
        Assert.isInstanceOf(ExecutableFileJob.class, executableJob, "FullFileJobError:");
        Job job = null;
        boolean success = false;
        try {
            ExecutableFileJob fileJob = (ExecutableFileJob) executableJob;
            job = executableJob.getJob();
            job.setOptionDesc("SUCCESS: " + desc);
            job.setModifyTime(TimeUtil.dateTime(new Date()));
            job.setLastRecordOffset(job.getLastRecordOffset() + fileJob.getIncrementalOffset());
            if (null == job.getNextTime()) {
                job.setNextTime(TimeUtil.dateTime(new Date()));
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(TimeUtil.dateTime(job.getNextTime()));
            calendar.add(Calendar.MINUTE, executableJob.getNextInterval());
            job.setNextTime(TimeUtil.dateTime(calendar.getTime()));
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
    protected boolean onJobFailed(ExecutableJob executableJob, String desc) {
        Assert.isInstanceOf(ExecutableFileJob.class, executableJob, "FullFileJobError:");
        Job job = null;
        boolean success = false;
        try {
            job = executableJob.getJob();
            job.setStatus(ExecutableJob.FAILED);
            job.setOptionDesc(desc);
            job.setModifyTime(TimeUtil.dateTime(new Date()));
            if (null == job.getNextTime()) {
                job.setNextTime(TimeUtil.getLogSuffix(new Date()));
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
