package cc.changic.platform.etl.schedule.job;

import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.file.commom.FileJobType;
import cc.changic.platform.etl.file.execute.ExecutableFileJob;
import cc.changic.platform.etl.file.message.FullFileTaskMessageHandler;
import cc.changic.platform.etl.file.message.IncrementalFileTaskMessageHandler;
import cc.changic.platform.etl.protocol.exception.ETLException;
import cc.changic.platform.etl.protocol.message.DuplexMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessageHeader;
import cc.changic.platform.etl.schedule.net.Client;
import cc.changic.platform.etl.schedule.scheduler.ETLSchedulerImpl;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

/**
 * Created by Panda.Z on 2015/2/2.
 */
public class ETLJob implements Job {

    private Logger logger = LoggerFactory.getLogger(ETLJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        Object tmpSpringContext = dataMap.get(ETLSchedulerImpl.SPRING_CONTEXT_KEY);
        Object tmpExecutableJob = dataMap.get(ETLSchedulerImpl.ETL_JOB_KEY);
        Assert.notNull(tmpSpringContext, "Spring context is null");
        Assert.notNull(tmpExecutableJob, "Executable Job is null");
        if (!(tmpSpringContext instanceof ConfigurableApplicationContext)) {
            throw new IllegalArgumentException("Spring context must instanceof" + ConfigurableApplicationContext.class);
        }
        if (!(tmpExecutableJob instanceof ExecutableJob))
            throw new IllegalArgumentException("Executable Job must instanceof " + ExecutableJob.class);
        ConfigurableApplicationContext springContext = (ConfigurableApplicationContext) tmpSpringContext;
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
                throw new ETLException("Not support file job type [" + jobType + "]");
            }
            handler.setMessage(etlMessage);
            Client client = springContext.getBean(Client.class);
            try {
                client.write(fileJob.getClientIP(), handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
