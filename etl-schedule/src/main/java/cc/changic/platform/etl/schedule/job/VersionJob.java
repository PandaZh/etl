package cc.changic.platform.etl.schedule.job;

import cc.changic.platform.etl.base.dao.ConfigVersionMapper;
import cc.changic.platform.etl.base.model.db.ConfigVersion;
import cc.changic.platform.etl.schedule.scheduler.ETLSchedulerImpl;
import cc.changic.platform.etl.schedule.scheduler.ReloadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Map;
import java.util.TimerTask;

/**
 * 配置版本的轮询任务
 *
 * @author Panda.Z
 */
public class VersionJob extends TimerTask {

    public final static int PRE_LOADING = 0;
    public final static int LOADING_STANDBY = 1;
    public final static int LOADING = 2;
    public final static int AFTER_LOADED = 3;

    private Logger logger = LoggerFactory.getLogger(VersionJob.class);

    private ConfigVersionMapper mapper;
    private ETLSchedulerImpl scheduler;
    private ReloadConfig config;

    public VersionJob(ConfigVersionMapper mapper, ETLSchedulerImpl scheduler, ReloadConfig config) {
        this.mapper = mapper;
        this.scheduler = scheduler;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            ConfigVersion version = mapper.selectLatest();
            logger.info("当前:{},查询出:{}", scheduler.getCurrentVersion(), version);
            if (scheduler.getCurrentVersion().equals(version)) {
                return;
            }
            switch (version.getStatus()) {
                case PRE_LOADING:
                    scheduler.PRE_LOAD.set(true);
                    version.setStatus(LOADING_STANDBY);
                    mapper.updateByPrimaryKey(version);
                    break;

                case LOADING_STANDBY:
                    break;

                case LOADING:
                    Calendar instance = Calendar.getInstance();
                    int minute = instance.get(Calendar.MINUTE);
                    int singleDigit = minute - (minute / 10) * 10;
                    if (singleDigit >= config.getStartMinute() && singleDigit <= config.getEndMinute()) {
                        scheduler.setCurrentVersion(version);
                        scheduler.reload(true);
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("执行配置版本监控任务异常:{}", e.getMessage(), e);
        }
    }
}
