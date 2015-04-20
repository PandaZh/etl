package cc.changic.platform.etl.schedule.cache;


import cc.changic.platform.etl.base.dao.*;
import cc.changic.platform.etl.base.model.ETLTask;
import cc.changic.platform.etl.base.model.db.*;
import cc.changic.platform.etl.base.model.util.ETLTaskKey;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * ETL配置缓存
 */
@SuppressWarnings("ALL")
public class ConfigCache {

    private final Map<Integer, App> appMap = Maps.newHashMap();
    private final Map<GameZoneKey, GameZone> gameZoneMap = Maps.newHashMap();
    private final Map<Integer, ODSConfig> odsConfigMap = Maps.newHashMap();
    private final Map<ETLTaskKey, ETLTask> etlTaskMap = Maps.newHashMap();
    private final Map<Integer, Job> jobMap = Maps.newHashMap();


    private Logger logger = LoggerFactory.getLogger(ConfigCache.class);

    @Autowired
    private AppMapper appMapper;
    @Autowired
    private GameZoneMapper zoneMapper;
    @Autowired
    private ODSConfigMapper odsConfigMapper;
    @Autowired
    private FileTaskMapper fileTaskMapper;
    @Autowired
    private JobMapper jobMapper;

    public void init() {
//        caching();
    }

    public void destroy() {
        appMap.clear();
        gameZoneMap.clear();
        odsConfigMap.clear();
        etlTaskMap.clear();
        jobMap.clear();
    }

    public boolean caching() {
        logger.info("Caching ETL schedule configurations...");

        List<App> apps = appMapper.selectAll();
        for (App app : apps) {
            appMap.put(app.getAppId(), app);
            logger.info("Cached app:{}", app.toString());
        }

        List<GameZone> gameZones = zoneMapper.selectAll();
        for (GameZone gameZone : gameZones) {
            gameZoneMap.put(new GameZoneKey(gameZone.getAppId(), gameZone.getGameZoneId()), gameZone);
            logger.info("Cached gameZone:{}", gameZone.toString());
        }

        List<ODSConfig> odsConfigs = odsConfigMapper.selectAll();
        for (ODSConfig ods : odsConfigs) {
            odsConfigMap.put(ods.getId(), ods);
            logger.info("Cached ODSConfig:{}", ods.toString());
        }

        // 文件任务类型
        List<FileTask> taskFiles = fileTaskMapper.selectAll();
        for (FileTask fileTask : taskFiles) {
            etlTaskMap.put(new ETLTaskKey(fileTask.getId(), fileTask.getTaskTable()), fileTask);
            logger.info("Cached file_task:{}", fileTask.toString());
        }

        List<Job> jobs = jobMapper.selectAll();
        for (Job job : jobs) {
            jobMap.put(job.getId(), job);
            logger.info("Cached job:{}", job.toString());
        }
        Integer[] sizeDesc = new Integer[]{apps.size(), gameZones.size(), odsConfigs.size(), taskFiles.size(), jobs.size()};
        logger.info("Cached ETL schedule configurations! APPSize={}, GameZoneSize={}, ODSSize={}, FileTaskSize={}, JobSize={}", sizeDesc);
        return false;
    }

    public Map<Integer, App> getAppMap() {
        return Collections.unmodifiableMap(appMap);
    }

    public Map<GameZoneKey, GameZone> getGameZoneMap() {
        return Collections.unmodifiableMap(gameZoneMap);
    }

    public Map<Integer, ODSConfig> getOdsConfigMap() {
        return Collections.unmodifiableMap(odsConfigMap);
    }

    public Map<ETLTaskKey, ETLTask> getEtlTaskMap() {
        return Collections.unmodifiableMap(etlTaskMap);
    }

    public Map<Integer, Job> getJobMap() {
        return Collections.unmodifiableMap(jobMap);
    }


}
