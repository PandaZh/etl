import cc.changic.platform.etl.base.util.TimeUtil;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * @author Panda.Z
 */
public class TimeTest {

    @Test
    public void test(){
        Date date = new Date(1391174450000L); // 2014-1-31 21:20:50
        System.out.println(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        // 或者可以 Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
        System.out.println(calendar2.getTime());
    }
    @Test
    public void timezone() {
        try {
            Date date = TimeUtil.dateTime("2015-08-18 12:00:00");
            Calendar calendar = Calendar.getInstance(new SimpleTimeZone(-4 * 60 * 60 * 1000, "test"));
            calendar.setTime(date);
            System.out.println(calendar.getTime());
            Calendar instance = Calendar.getInstance();
//            instance.setTime(calendar.getTime());
            instance.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND));
            System.out.println(instance.getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getDate(int timeZoneOffset) {
        if (timeZoneOffset > 13 || timeZoneOffset < -12) {
            timeZoneOffset = 0;
        }
        TimeZone timeZone;
        String[] ids = TimeZone.getAvailableIDs(timeZoneOffset * 60 * 60 * 1000);
        if (ids.length == 0) {
            // if no ids were returned, something is wrong. use default TimeZone
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = new SimpleTimeZone(timeZoneOffset * 60 * 60 * 1000, ids[0]);
        }
        System.out.println(timeZone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(timeZone);
        return sdf.format(new Date());
    }

    @Test
    public void main() {

        // 测试创建TimeZone对象的3种方法
        showUsageOfTimeZones();

        // 测试TimeZone的其它API
        testOtherAPIs();

        // 打印getTimeZone(String id)支持的所有id
        printAllTimeZones();
    }


    /**
     * 测试创建TimeZone对象的3种方法
     */
    public void showUsageOfTimeZones() {
        TimeZone tz;

        // (01) 默认时区
        tz = TimeZone.getDefault();
        printDateIn(tz);

        // (02) 设置时区为"GMT+08:00"
        tz = TimeZone.getTimeZone("GMT+08:00");
        printDateIn(tz);

        // (03) 设置时区为""
        tz = TimeZone.getTimeZone("Asia/Chongqing");
        printDateIn(tz);
    }

    /**
     * 打印 tz对应的日期/时间
     */
    private void printDateIn(TimeZone tz) {
        // date为2013-09-19 14:22:30
        Date date = new Date(113, 8, 19, 14, 22, 30);
        // 获取默认的DateFormat，用于格式化Date
        DateFormat df = DateFormat.getInstance();
        // 设置时区为tz
        df.setTimeZone(tz);
        // 获取格式化后的字符串
        String str = df.format(date);

        System.out.println(tz.getID() + " :" + str);
    }

    /**
     * 测试TimeZone的其它API
     */
    public void testOtherAPIs() {
        // 默认时区
        TimeZone tz = TimeZone.getDefault();

        // 获取“id”
        String id = tz.getID();

        // 获取“显示名称”
        String name = tz.getDisplayName();

        // 获取“时间偏移”。相对于“本初子午线”的偏移，单位是ms。
        int offset = tz.getRawOffset();
        // 获取“时间偏移” 对应的小时
        int gmt = offset / (3600 * 1000);

        System.out.printf("id=%s, name=%s, offset=%s(ms), gmt=%s\n",
                id, name, offset, gmt);
    }

    /**
     * 打印getTimeZone(String id)支持的所有id
     */
    public void printAllTimeZones() {

        String[] ids = TimeZone.getAvailableIDs();
        for (String id : ids) {
            //int offset = TimeZone.getTimeZone(avaIds[i]).getRawOffset();
            //System.out.println(i+"  "+avaIds[i]+" "+offset / (3600 * 1000) + "\t");
            System.out.printf(id + ", ");
        }
        System.out.println();
    }
}
