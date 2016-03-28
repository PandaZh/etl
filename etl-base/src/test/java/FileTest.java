import cc.changic.platform.etl.base.model.db.ODSConfig;
import cc.changic.platform.etl.base.util.LogFileUtil;
import cc.changic.platform.etl.base.util.TimeUtil;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

    @Test
    public void test2(){
        try {
            List<String> datas = Lists.newArrayList();
            RandomAccessFile accessFile = new RandomAccessFile("G:\\log_online_regs_3", "r");
            String data = accessFile.readLine();
            while (data != null) {
                datas.add(data);
                data = accessFile.readLine();
            }
            char split = 0x01;
            for (String data2 : datas) {
                Iterable<String> strings = Splitter.on(split).split(data2);
                Iterator<String> iterator = strings.iterator();
                int i = 1;
                while (iterator.hasNext()) {
                    System.out.printf(iterator.next());
                    System.out.printf("\t");
                    i++;
//                    System.out.printf("" + i++);
                }

                System.out.println(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test3(){
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://" + "192.168.33.64" + ":" + "5432" + "/" + "db_game_data";
            Connection connection = DriverManager.getConnection(url, "g_user", "123456");
            String sql = "into sc_game_jj_data.t_l_log_player_charge(logdate,gamezoneid,account,playerid,level,billno,diamond,needmoney,moneytype,payway,payamtcoins,pubacctpayamtcoins,amt) values(CAST('2016-01-18T10:35:21+0800' AS timestamp without time zone),CAST('14' AS integer),CAST('32921517064B957B6F83EB1C46CD30D1' AS varchar),CAST('140000000000007' AS int8)),CAST('1' AS integer)),CAST('-APPDJ51670-20160118-1035213769' AS varchar)),CAST('10' AS integer)),CAST('1' AS decimal)),CAST('CNY' AS varchar)),CAST('TX' AS varchar)),CAST('0' AS integer)),CAST('0' AS integer)),CAST('100' AS integer))";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test4(){
        System.out.println(Boolean.parseBoolean("true") ? "1" : "2");
    }
}
