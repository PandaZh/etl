package cc.changic.platform.etl.base.util;

import org.apache.ibatis.logging.stdout.StdOutImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 */
public class TimeUtil {

    public final static String LOG_FILE_SUFFIX_FORMAT = "yyyy-MM-dd.HHmm";
    public final static String ISO_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * 时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static String dateTime(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * 时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static Date dateTime(String time) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
    }

    /**
     * 时间格式 yyyy-MM-dd'T'HH:mm:ssZ
     */
    public static String getISOTime(Date date) {
        return new SimpleDateFormat(ISO_TIME_FORMAT).format(date);
    }

    /**
     * 时间格式 yyyy-MM-dd'T'HH:mm:ssZ
     */
    public static Date getISOTime(String time) throws ParseException {
        return new SimpleDateFormat(ISO_TIME_FORMAT).parse(time);
    }

    /**
     * 时间格式 yyyy-MM-dd.HHmm
     */
    public static String getLogSuffix(Date date) {
        return new SimpleDateFormat(LOG_FILE_SUFFIX_FORMAT).format(date);
    }

    /**
     * 时间格式 yyyy-MM-dd.HHmm
     */
    public static Date getLogSuffix(String dateTime) throws ParseException {
        return new SimpleDateFormat(LOG_FILE_SUFFIX_FORMAT).parse(dateTime);
    }

    public static void main(String[] args) {
        try {
            System.out.println(dateTime(new Date()));
            System.out.println(dateTime("2015-08-05 21:20:39"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
