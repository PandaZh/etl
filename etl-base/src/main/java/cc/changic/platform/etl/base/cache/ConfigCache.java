package cc.changic.platform.etl.base.cache;


import cc.changic.platform.etl.base.dao.*;
import cc.changic.platform.etl.base.model.ETLTask;
import cc.changic.platform.etl.base.model.db.*;
import cc.changic.platform.etl.base.model.util.ETLTaskKey;
import cc.changic.platform.etl.base.model.util.GameZoneKey;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * ETL配置缓存
 */
@Component
public class ConfigCache {

    private final static Map<Integer, App> APP_MAP = Maps.newHashMap();
    private final static Map<GameZoneKey, GameZone> GAME_ZONE_MAP = Maps.newHashMap();
    private final static Map<Integer, ODSConfig> ODS_CONFIG_MAP = Maps.newHashMap();
    private final static Map<ETLTaskKey, ETLTask> ETL_TASK_MAP = Maps.newHashMap();
    private final static Map<Integer, Job> JOB_MAP = Maps.newHashMap();


    private Logger logger = LoggerFactory.getLogger(ConfigCache.class);

    @Autowired
    private AppMapper appMapper;
    @Autowired
    private GameZoneMapper zoneMapper;
    @Autowired
    private ODSConfigMapper odsConfigMapper;
    @Autowired
    private TaskFileMapper taskFileMapper;
    @Autowired
    private JobMapper jobMapper;

    public void init() {
        caching();
    }

    public void destroy() {
        APP_MAP.clear();
        GAME_ZONE_MAP.clear();
        ODS_CONFIG_MAP.clear();
        ETL_TASK_MAP.clear();
        JOB_MAP.clear();
    }

    public boolean caching() {
        logger.info("Caching ETL schedule configurations...");

        List<App> apps = appMapper.selectAll();
        for (App app : apps) {
            APP_MAP.put(app.getAppId(), app);
            logger.info("Cached app:{}", app.toString());
        }

        List<GameZone> gameZones = zoneMapper.selectAll();
        for (GameZone gameZone : gameZones) {
            GAME_ZONE_MAP.put(new GameZoneKey(gameZone.getAppId(), gameZone.getGameZoneId()), gameZone);
            logger.info("Cached gameZone:{}", gameZone.toString());
        }

        List<ODSConfig> odsConfigs = odsConfigMapper.selectAll();
        for (ODSConfig ods : odsConfigs) {
            ODS_CONFIG_MAP.put(ods.getId(), ods);
            logger.info("Cached ODSConfig:{}", ods.toString());
        }

        // 文件任务类型
        List<TaskFile> taskFiles = taskFileMapper.selectAll();
        for (TaskFile taskFile : taskFiles) {
            ETL_TASK_MAP.put(new ETLTaskKey(taskFile.getTaskId(), taskFile.getTaskTable()), taskFile);
            logger.info("Cached file_task:{}", taskFile.toString());
        }

        List<Job> jobs = jobMapper.selectAll();
        for (Job job : jobs) {
            JOB_MAP.put(job.getId(), job);
            logger.info("Cached job:{}", job.toString());
        }

        logger.info("Cached ETL schedule configurations!");
        return false;
    }

    public static Map<Integer, App> getAppMap() {
        return Collections.unmodifiableMap(APP_MAP);
    }

    public static Map<GameZoneKey, GameZone> getGameZoneMap() {
        return Collections.unmodifiableMap(GAME_ZONE_MAP);
    }

    public static Map<Integer, ODSConfig> getOdsConfigMap() {
        return Collections.unmodifiableMap(ODS_CONFIG_MAP);
    }

    public static Map<ETLTaskKey, ETLTask> getEtlTaskMap() {
        return Collections.unmodifiableMap(ETL_TASK_MAP);
    }

    public static Map<Integer, Job> getJobMap() {
        return Collections.unmodifiableMap(JOB_MAP);
    }


}
