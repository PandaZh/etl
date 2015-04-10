package cc.changic.platform.etl.schedule.scheduler;

/**
 * 重新加载资源的相关配置
 */
public class ReloadConfig {

    private int interval;
    private int startMinute;
    private int endMinute;

    public ReloadConfig(int interval, int startMinute, int endMinute) {
        this.interval = interval;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
    }

    /**
     * @return 轮询时间单位(秒)
     */
    public int getInterval() {
        return interval;
    }

    /**
     * @return 允许重新加载的开始分钟数的个位
     */
    public int getStartMinute() {
        return startMinute;
    }

    /**
     * @return 允许重新加载的结束分钟数的个位
     */
    public int getEndMinute() {
        return endMinute;
    }
}
