package cc.changic.platform.etl.schedule.job;

import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.service.JobService;
import cc.changic.platform.etl.file.commom.FileJobType;
import cc.changic.platform.etl.file.execute.ExecutableFileJob;
import cc.changic.platform.etl.file.message.FullFileTaskMessageHandler;
import cc.changic.platform.etl.file.message.IncrementalFileTaskMessageHandler;
import cc.changic.platform.etl.protocol.exception.ETLException;
import cc.changic.platform.etl.protocol.message.DuplexMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessageHeader;
import cc.changic.platform.etl.schedule.net.Client;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import java.util.PriorityQueue;

/**
 * 数据拉取任务
 * @author Panda.Z
 */
public class DataJob implements Job {

    public final static String ETL_JOB_KEY = "data";
    public final static String SPRING_CONTEXT_KEY = "spring_context";
    public final static String JOB_SERVICE = "job_service";

    private Logger logger = LoggerFactory.getLogger(DataJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();

        Object tmpSpringContext = dataMap.get(SPRING_CONTEXT_KEY);
        Assert.notNull(tmpSpringContext, "Spring context is null");
        Assert.isInstanceOf(ConfigurableApplicationContext.class, tmpSpringContext);


        Object tmpExecutableJob = dataMap.get(ETL_JOB_KEY);
        Assert.notNull(tmpExecutableJob, "Executable Job is null");
        Assert.isInstanceOf(ExecutableJob.class, tmpExecutableJob);


        Object tmpJobService = dataMap.get(JOB_SERVICE);
        Assert.notNull(tmpJobService, "Job Service is null");
        Assert.isInstanceOf(JobService.class, tmpJobService);

        ConfigurableApplicationContext springContext = (ConfigurableApplicationContext) tmpSpringContext;
        ExecutableJob executableJob = (ExecutableJob) tmpExecutableJob;
        JobService jobService = (JobService) tmpJobService;

        boolean notSupport = false;
        try {
            // 暂时只处理文件任务
            if (tmpExecutableJob instanceof ExecutableFileJob) {
                ExecutableFileJob fileJob = (ExecutableFileJob) tmpExecutableJob;
                Short jobType = fileJob.getJobType();
                ETLMessage etlMessage = new ETLMessage();
                etlMessage.setBody(fileJob);
                etlMessage.setHeader(new ETLMessageHeader());
                DuplexMessage handler = null;
                if (jobType == FileJobType.FILE_JOB_TYPE_FULL) {
                    handler = springContext.getBean(FullFileTaskMessageHandler.class);
                } else if (jobType == FileJobType.FILE_JOB_TYPE_INCREMENTAL) {
                    handler = springContext.getBean(IncrementalFileTaskMessageHandler.class);
                } else {
                    notSupport = true;
                    throw new ETLException("Not support file job type [" + jobType + "]");
                }
                handler.setMessage(etlMessage);
                Client client = springContext.getBean(Client.class);
                client.write(fileJob.getClientIP(), handler);
            }
        } catch (Exception e) {
            if (notSupport){
                try {
                    jobService.onFailed(executableJob, e.getMessage());
                } catch (Exception e1) {
                    logger.error("Do error {}", e.getMessage(), e);
                }
            }
            logger.error("Execute job error: {}", e.getMessage(), e);
        }
    }

}
