import cc.changic.platform.etl.base.util.LogFileUtil;
import cc.changic.platform.etl.base.util.TimeUtil;
import com.google.common.base.Strings;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Panda.Z on 2015/2/14.
 */
public class FileTest {

    @Test
    public void modifyTest(){
        try {
            File file = new File("E:\\logs", "log_login_1.2015-02-03.1030");
            System.out.println(TimeUtil.getISOTime(new Date(file.lastModified())));
            long lastModified = file.lastModified();
            long millis = System.currentTimeMillis();
            System.out.println(millis - lastModified);
            System.out.println((millis - lastModified) / 1000);
            System.out.println((millis - lastModified) / (1000 * 60));
            System.out.println((millis - lastModified) / (1000 * 60 * 60));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test(){
        try {
            short nextInterval = 5;
            Date lastRecordTime = TimeUtil.getLogSuffix("2015-02-03.1055");
            File sourceFile = new File("E:\\logs", "log_login_1.2015-02-03.1100");
            if (!sourceFile.exists()) {
                String baseName = LogFileUtil.getLogFileBaseName(sourceFile.getAbsolutePath());
                if (!Strings.isNullOrEmpty(baseName)) {
                    File baseFile = new File(baseName);
                    if (baseFile.exists()) {
                        long lastModified = baseFile.lastModified();
                        // 计算出分钟间隔
                        int interval = (int) ((lastModified - lastRecordTime.getTime()) / (1000 * 60));
                        // 计算出间隔倍数
                        int multiple = interval / nextInterval;
                        if (multiple > 0) {
                            // 使用时间倍数换算日志后缀
                            for (int i = 1; i <= multiple; i++) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(lastRecordTime);
                                calendar.add(Calendar.MINUTE, i * nextInterval);
                                String suffix = TimeUtil.getLogSuffix(calendar.getTime());
                                File intervalLogFile = new File(baseName + "." + suffix);
                                if (intervalLogFile.exists()) {
                                    sourceFile = intervalLogFile;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            System.out.println(sourceFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void getOldestFile(){
        try {
            File file = LogFileUtil.getOldestLogFile("E:\\logs\\pay");
            System.out.println(file.getAbsolutePath());
            System.out.println(TimeUtil.getLogSuffix("E.4152015E4"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteOldFile(){
        File sourceFile = new File("E:\\data\\gamelog\\1\\login", "log_login_1.2015-02-15.1500");
        LogFileUtil.deleteTooOldFile(sourceFile, 48);
    }

    @Test
    public void timeTest(){
        Calendar instance = Calendar.getInstance();
        System.out.println(instance.getTime().getTime());
        instance.add(Calendar.HOUR, -1);
        System.out.println(instance.getTime().getTime());
        instance.add(Calendar.HOUR, 1);
        System.out.println(instance.getTime().getTime());

        Calendar instance1 = Calendar.getInstance();
        instance1.add(Calendar.HOUR, -1);

        System.out.println(instance.compareTo(instance1));

    }
}
