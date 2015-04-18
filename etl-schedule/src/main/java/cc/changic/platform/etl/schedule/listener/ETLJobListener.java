package cc.changic.platform.etl.schedule.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Panda.Z on 2015/2/2.
 */
public class ETLJobListener implements JobListener {

    private Logger logger = LoggerFactory.getLogger(ETLJobListener.class);


    @Override
    public String getName() {
        return "ETL job listener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        logger.error("任务异常,任务key={}", context.getJobDetail().getKey());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
    }
}
