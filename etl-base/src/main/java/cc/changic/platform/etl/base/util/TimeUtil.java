package cc.changic.platform.etl.base.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 */
public class TimeUtil {

    public final static String LOG_FILE_SUFFIX_FORMAT = "yyyy-MM-dd.HH-mm";
    public final static SimpleDateFormat LOG_FILE_SUFFIX_FORMATTER = new SimpleDateFormat(LOG_FILE_SUFFIX_FORMAT);

    public final static String ISO_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public final static SimpleDateFormat ISO_TIME_FORMATTER = new SimpleDateFormat(ISO_TIME_FORMAT);

    /**
     * 时间格式 yyyy-MM-dd'T'HH:mm:ssZ
     */
    public static String getISOTime(Date date) {
        return ISO_TIME_FORMATTER.format(date);
    }

    /**
     * 时间格式 yyyy-MM-dd'T'HH:mm:ssZ
     */
    public static Date getISOTime(String time) throws ParseException {
        return ISO_TIME_FORMATTER.parse(time);
    }

    /**
     * 时间格式 yyyy-MM-dd.HH-mm
     */
    public static String getLogSuffix(Date date) {
        return LOG_FILE_SUFFIX_FORMATTER.format(date);
    }

    /**
     * 时间格式 yyyy-MM-dd.HH-mm
     */
    public static Date getLogSuffix(String dateTime) throws ParseException {
        return LOG_FILE_SUFFIX_FORMATTER.parse(dateTime);
    }








}
