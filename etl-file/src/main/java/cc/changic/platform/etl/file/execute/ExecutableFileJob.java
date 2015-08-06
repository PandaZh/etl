package cc.changic.platform.etl.file.execute;

import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.*;
import cc.changic.platform.etl.base.util.TimeUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static cc.changic.platform.etl.base.util.LogFileUtil.getLogFileDir;
import static cc.changic.platform.etl.base.util.LogFileUtil.getNextLogFileName;

/**
 * 可执行的文件任务
 */
public class ExecutableFileJob implements ExecutableJob, Serializable {

    // 构造函数数据
    private App app;
    private GameZone gameZone;
    private ODSConfig odsConfig;
    private FileTask fileTask;
    private Job job;
    private ConfigVersion version;

    // 根据构造函数数据计算出的数据
    private String fileName;
    private String sourceDir;
    private String storageDir;
    // 全量拉取时文件的MD5值
    private String md5;
    // 增量拉取时每次增量的字节数
    private long incrementalOffset;

    public ExecutableFileJob(App app, GameZone gameZone, FileTask fileTask, Job job, ODSConfig odsConfig, ConfigVersion version) {
        this.app = app;
        this.gameZone = gameZone;
        this.fileTask = fileTask;
        this.job = job;
        this.odsConfig = odsConfig;
        this.version = version;
    }

    @Override
    public int compareTo(ExecutableJob other) {
        if (null == other)
            return 1;
        if (null == this.getNextTime())
            return -1;
        if (null == other.getNextTime())
            return 1;
        Calendar thisCalendar = Calendar.getInstance();
        thisCalendar.setTime(this.getNextTime());
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTime(other.getNextTime());
        return thisCalendar.compareTo(otherCalendar);
    }

    @Override
    public Integer getJobID() {
        return getJob().getId();
    }

    @Override
    public Short getJobType() {
        return getFileTask().getTaskType();
    }

    @Override
    public Date getNextTime() {
        return null == getJob() ? null : getJob().getNextTime();
    }

    @Override
    public Short getNextInterval() {
        return getFileTask().getNextInterval();
    }

    @Override
    public GameZoneKey getGameZoneKey() {
        return new GameZoneKey(job.getAppId(), job.getGameZoneId());
    }

    @Override
    public ConfigVersion getConfigVersion() {
        return version;
    }

    public App getApp() {
        return app;
    }

    public GameZone getGameZone() {
        return gameZone;
    }

    public ODSConfig getOdsConfig() {
        return odsConfig;
    }

    public FileTask getFileTask() {
        return fileTask;
    }

    @Override
    public Job getJob() {
        return job;
    }

    public String getClientIP() {
        return getGameZone().getEtlClientIp();
    }

    public String getFileName() throws ParseException {
        if (Strings.isNullOrEmpty(fileName)) {
            LoggerFactory.getLogger("test").info("测试;;;;{},{},{},{}",getFileTask().getFileName(), getJob(), getFileTask().getNextInterval(), getJob().getLastRecordTimeStr());
            fileName = getNextLogFileName(getFileTask().getFileName(), getJob(), getFileTask().getNextInterval(), TimeUtil.dateTime(getJob().getLastRecordTimeStr()));
        }
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSourceDir() throws NoSuchFieldException, IllegalAccessException {
        if (Strings.isNullOrEmpty(sourceDir)) {
            sourceDir = getLogFileDir(getFileTask().getSourceDir(), getJob());
        }
        return sourceDir;
    }

    public String getStorageDir() throws NoSuchFieldException, IllegalAccessException {
        if (Strings.isNullOrEmpty(storageDir)) {
            storageDir = getLogFileDir(getFileTask().getStorageDir(), getJob());
        }
        return storageDir;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getIncrementalOffset() {
        return incrementalOffset;
    }

    public void setIncrementalOffset(long incrementalOffset) {
        this.incrementalOffset = incrementalOffset;
    }

    @Override
    public String toString() {
        return "ExecutableFileJob{" +
                "id=" + getJobID() +
                ", name=" + getFileTask().getTaskName() +
                '}';
    }
}
