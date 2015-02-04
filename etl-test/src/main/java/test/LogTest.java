package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Panda.Z on 2015/2/3.
 */
public class LogTest {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(LogTest.class);
        int i = 0;
        char spile = 0x01;
        for (; ; ) {
            i++;
            logger.info("index" + i + spile + "test1" + spile + "test1" + spile + "test1" + spile + "test1" + spile + "test1");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
