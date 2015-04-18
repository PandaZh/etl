package cc.changic.test;

import com.google.common.collect.Sets;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Panda.Z on 2015/4/17.
 */
public class SchedulerTest implements Runnable {

    private int count;

    public SchedulerTest(int count) {
        this.count = count;
    }

    @Override
    public void run() {
        System.out.println(count);
    }

    public static void main(String[] args) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
        long initialDelay1 = 10;
        long period1 = 1;
        // 从现在开始1秒钟之后，每隔1秒钟执行一次job1
        service.schedule(new SchedulerTest(1), initialDelay1, TimeUnit.SECONDS);
        service.shutdown();
//        long initialDelay2 = 1;
//        long delay2 = 1;
//        // 从现在开始2秒钟之后，每隔2秒钟执行一次job2
//        service.scheduleWithFixedDelay(new SchedulerTest(2), initialDelay2, delay2, TimeUnit.SECONDS);
    }


}
