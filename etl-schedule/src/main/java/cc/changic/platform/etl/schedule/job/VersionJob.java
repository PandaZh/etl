package cc.changic.platform.etl.schedule.job;

import cc.changic.platform.etl.base.dao.ConfigVersionMapper;
import cc.changic.platform.etl.base.model.db.ConfigVersion;
import cc.changic.platform.etl.base.schedule.ETLScheduler;
import cc.changic.platform.etl.schedule.scheduler.ReloadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.TimerTask;

/**
 * 配置版本的轮训任务
 *
 * @author Panda.Z
 */
public class VersionJob extends TimerTask {

    private Logger logger = LoggerFactory.getLogger(VersionJob.class);

    private ConfigVersionMapper mapper;
    private ETLScheduler scheduler;
    private ReloadConfig config;

    public VersionJob(ConfigVersionMapper mapper, ETLScheduler scheduler, ReloadConfig config) {
        this.mapper = mapper;
        this.scheduler = scheduler;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            ConfigVersion version = mapper.selectLatest();
            logger.info("当前版本号:{},查询出版本号:{}", scheduler.getCurrentVersion(), version);
            if (scheduler.getCurrentVersion().equals(version)) {
                return;
            }
            Calendar instance = Calendar.getInstance();
            int minute = instance.get(Calendar.MINUTE);
            int singleDigit = minute - (minute / 10) * 10;
            if (singleDigit >= config.getStartMinute() && singleDigit <= config.getEndMinute()) {
                scheduler.setCurrentVersion(version);
                scheduler.reload();
            }
        } catch (Exception e) {
            logger.error("执行配置版本监控任务异常:{}", e.getMessage(), e);
        }
    }
}
