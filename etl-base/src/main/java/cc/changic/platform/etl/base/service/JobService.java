package cc.changic.platform.etl.base.service;

import cc.changic.platform.etl.base.dao.JobMapper;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Panda.Z on 2015/1/31.
 */
@Component
public class JobService {

//    @Autowired
    private JobMapper jobMapper;

    public void doError(Job job, String message){
        job.setStatus(ExecutableJob.FAILED);
        job.setOptionDesc(message);
        job.setModifyTime(new Date());
        jobMapper.updateByPrimaryKey(job);
    }
}
