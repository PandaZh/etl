package cc.changic.platform.etl.base.service.impl;

import cc.changic.platform.etl.base.dao.JobLogMapper;
import cc.changic.platform.etl.base.dao.JobMapper;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.schedule.ETLScheduler;
import cc.changic.platform.etl.base.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 作业相关服务类
 * Created by Panda.Z on 2015/3/3.
 */
public class JobServiceImpl implements JobService{

    private Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired(required = false)
    private JobMapper jobMapper;
    @Autowired(required = false)
    private JobLogMapper logMapper;
    @Autowired(required = false)
    private ETLScheduler etlScheduler;


    @Override
    public void onJobSuccess(ExecutableJob executableJob) {

    }

    @Override
    public void onJobFailed(ExecutableJob executableJob) {

    }
}
