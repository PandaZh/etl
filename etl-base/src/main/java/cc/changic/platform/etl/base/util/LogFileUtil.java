package cc.changic.platform.etl.base.util;

import cc.changic.platform.etl.base.model.db.Job;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志文件工具类
 */
public class LogFileUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(LogFileUtil.class);
    private static Logger DELETE_LOGGER = LoggerFactory.getLogger("delete");

    public final static String IDR_REGEX = "\\{(.+?)\\}";
    public final static Pattern DIR_PATTERN = Pattern.compile(IDR_REGEX);

    public final static String TIMESTAMP_REGEX = "\\[(.+?)\\]";
    public final static Pattern TIMESTAMP_PATTERN = Pattern.compile(TIMESTAMP_REGEX);

    /**
     * 获取当前文件夹下最老的日志文件,用于第一次拉取文件
     *
     * @param dir 指定的文件夹
     * @return 满足yyyy-MM-dd.HH-mm时间格式后缀的最老的日志文件
     */
    public static File getOldestLogFile(String dir) throws FileNotFoundException {
        File parentFile = new File(dir);
        if (!parentFile.exists())
            throw new FileNotFoundException("Not exist dir [ " + dir + " ]");
        if (!parentFile.isDirectory())
            throw new IllegalArgumentException("File [ " + dir + " ] is a file not a dir");
        File[] childFiles = parentFile.listFiles();
        if (null == childFiles)
            throw new FileNotFoundException("No file found in dir [" + dir + " ]");
        File oldestFile = null;
        Calendar old = null;
        for (File file : childFiles) {
            //如果子文件是文件夹，放弃计算
            if (file.isDirectory())
                continue;
            // 如果文件后缀为空，放弃计算
            String suffix = getLogFileTimestampSuffix(file.getPath());
            if (Strings.isNullOrEmpty(suffix))
                continue;
            // 如果没有最老文件,认为当前文件最老
            if (null == oldestFile) {
                try {
                    Date olderTime = TimeUtil.getLogSuffix(suffix);
                    old = Calendar.getInstance();
                    old.setTime(olderTime);
                    oldestFile = file;
                } catch (ParseException e) {
                    // do nothing
                }
            } else {
                // 如果已存在最老文件,计算当前文件跟已缓存的最老文件的时间戳,得出最老文件
                Calendar older;
                try {
                    Date olderTime = TimeUtil.getLogSuffix(suffix);
                    older = Calendar.getInstance();
                    older.setTime(olderTime);
                } catch (ParseException e) {
                    continue;
                }
                if (old.compareTo(older) > 0) {
                    oldestFile = file;
                    old = older;
                }
            }
        }
        return oldestFile;
    }

    /**
     * 获取日志文件的文件夹：源文件夹/存贮文件夹
     *
     * @param srcPath 需要替换的变量用"{}"包裹,例如/data/logs/1000/{gameZoneId}
     * @param srcObj  变量来源,例如上述表达式中包含gameZoneId,则srcObj中必须有gameZoneId字段,并且不能为null
     * @return 替换过后的字符串
     */
    public static String getLogFileDir(String srcPath, Object srcObj) throws NoSuchFieldException, IllegalAccessException {
        Matcher matcher = DIR_PATTERN.matcher(srcPath);
        Class<?> clazz = srcObj.getClass();
        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String regex = Pattern.quote("{" + fieldName + "}");
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(srcObj);
                if (null == value)
                    throw new NullPointerException("No value in field [" + fieldName + "] in class [" + srcObj.getClass() + "]");
                srcPath = srcPath.replaceAll(regex, value.toString());
            } catch (NoSuchFieldException e) {
                throw new NoSuchFieldException("Not found field [" + fieldName + "] in class [" + srcObj.getClass() + "]");
            }
        }
        return srcPath;
    }

    /**
     * 替换源路径的时间格式后缀
     *
     * @param srcName        时间格式后缀用[]包裹,例如:log_login_1000.[timestamp]/log_login_{gameZoneId}.[timestamp]
     * @param interval       时间间隔分钟数
     * @param lastRecordTime 最后一次记录的时间
     * @return
     */
    public static String getNextLogFileName(String srcName, Job job, short interval, Date lastRecordTime) {
        try {
            Matcher matcher = DIR_PATTERN.matcher(srcName);
            Class<?> clazz = job.getClass();
            while (matcher.find()) {
                String fieldName = matcher.group(1);
                String regex = Pattern.quote("{" + fieldName + "}");
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(job);
                    if (null == value)
                        throw new NullPointerException("No value in field [" + fieldName + "] in class [" + job.getClass() + "]");
                    srcName = srcName.replaceAll(regex, value.toString());
                } catch (NoSuchFieldException e) {
                    throw new NoSuchFieldException("Not found field [" + fieldName + "] in class [" + job.getClass() + "]");
                }
            }
            matcher = TIMESTAMP_PATTERN.matcher(srcName);
            if (matcher.find()) {
                String fieldName = matcher.group(1);
                String regex = Pattern.quote("[" + fieldName + "]");
                Calendar next = Calendar.getInstance();
                next.setTime(lastRecordTime);
                next.add(Calendar.MINUTE, interval);
                srcName = srcName.replaceAll(regex, TimeUtil.getLogSuffix(next.getTime()));
            }
            return srcName;
        } catch (Exception e) {
            throw new RuntimeException("Pattern fileName error!" + e.getClass());
        }
    }


    /**
     * 获取日志文件时间戳后缀
     *
     * @param fileName 文件的绝对路径或者简单文件名
     * @return yyyy-MM-dd.HHmm格式后缀
     */
    public static String getLogFileTimestampSuffix(String fileName) {
        if (fileName.length() <= TimeUtil.LOG_FILE_SUFFIX_FORMAT.length())
            return null;
        return fileName.substring(fileName.length() - TimeUtil.LOG_FILE_SUFFIX_FORMAT.length(), fileName.length());
    }

    public static Date getLogFileTimestamp(String fileName) throws ParseException {
        String suffix = getLogFileTimestampSuffix(fileName);
        return TimeUtil.getLogSuffix(suffix);
    }

    /**
     * 获取日志文件除时间戳之外的文件名，也就是正在写入的日志文件的文件名
     *
     * @param fileName 文件的绝对路径或者简单文件名
     * @return log_login_{gameZoneId}.[timestamp] ===> log_login_{gameZoneId}
     */
    public static String getLogFileBaseName(String fileName) {
        if (fileName.length() <= ("." + TimeUtil.LOG_FILE_SUFFIX_FORMAT).length())
            return null;
        return fileName.substring(0, fileName.length() - ("." + TimeUtil.LOG_FILE_SUFFIX_FORMAT).length());
    }

    /**
     * 删除源文件所在文件夹下的比较老的文件
     *
     * @param sourceFile     源文件
     * @param deleteInterval 时间间隔(单位:分钟)
     */
    public static void deleteTooOldFile(File sourceFile, int deleteInterval) {
        try {
            // 计算需要删除的最小时间
            Date timestamp = getLogFileTimestamp(sourceFile.getAbsolutePath());
            Calendar limitTime = Calendar.getInstance();
            limitTime.setTime(timestamp);
            limitTime.add(Calendar.HOUR, -deleteInterval);
            String logSuffix = TimeUtil.getLogSuffix(limitTime.getTime());

            // 获取简单文件名
            String baseName = getLogFileBaseName(sourceFile.getName());
            if (null == baseName)
                throw new NullPointerException("删除老文件时,获取文件名为空:file=" + sourceFile.getAbsolutePath());
            // 遍历文件
            File parentFile = sourceFile.getParentFile();
            File[] files = parentFile.listFiles();
            for (File file : files) {
                Calendar tmpLimitTime;
                try {
                    tmpLimitTime = Calendar.getInstance();
                    tmpLimitTime.setTime(getLogFileTimestamp(file.getAbsolutePath()));
                } catch (Exception e) {
                    continue;
                }
                if (tmpLimitTime.compareTo(limitTime) > 0)
                    continue;
                String tmpName = getLogFileBaseName(file.getName());
                if (null == tmpName)
                    continue;
                if (!tmpName.equals(baseName))
                    continue;
                file.delete();
                DELETE_LOGGER.info("Delete old file:[file_name={}, delete_interval={}, current_log_time={}]", file.getAbsolutePath(), deleteInterval, logSuffix);
                LOGGER.info("删除老文件:file_name={}, deleteInterval={}", file.getAbsolutePath(), deleteInterval);
            }
        } catch (Exception e) {
            LOGGER.error("删除老文件时异常:{}", e.getMessage(), e);
        }
    }
}

