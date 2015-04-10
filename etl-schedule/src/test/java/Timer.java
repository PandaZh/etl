import cc.changic.platform.etl.base.model.db.ODSConfig;
import cc.changic.platform.etl.base.util.TimeUtil;
import com.google.common.base.Splitter;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.sound.midi.Soundbank;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by Panda.Z on 2015/2/10.
 */
public class Timer {


    @Test
    public void test(){
        Calendar instance = Calendar.getInstance();
        Integer minute = instance.get(Calendar.MINUTE);

        System.out.println(minute - (minute / 10) * 10);
    }
    @Test
    public void scheduler() {
        try {
            // Grab the Scheduler instance from the Factory
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            JobDetail job = newJob(HelloJob.class)
                    .withIdentity("job1", "group1")
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .build();
            scheduler.scheduleJob(job, trigger);

            Thread.sleep(100000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class HelloJob implements Job {

        public void execute(JobExecutionContext context) throws JobExecutionException {
            // Say Hello to the World and display the date/time
            System.out.println("Hello World! - ===================================================================" + new Date());
        }

    }
}
