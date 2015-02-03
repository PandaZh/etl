package cc.changic.platform.etl.file.execute;

import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.*;
import cc.changic.platform.etl.base.model.util.GameZoneKey;
import com.google.common.base.Strings;

import java.io.Serializable;
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

    // 根据构造函数数据计算出的数据
    private String fileName;
    private String sourceDir;
    private String storageDir;
    private String md5;

    public ExecutableFileJob(App app, GameZone gameZone, FileTask fileTask, Job job, ODSConfig odsConfig) {
        this.app = app;
        this.gameZone = gameZone;
        this.fileTask = fileTask;
        this.job = job;
        this.odsConfig = odsConfig;
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

    public Job getJob() {
        return job;
    }

    public String getClientIP() {
        return getGameZone().getEtlClientIp();
    }

    public String getFileName() {
        if (Strings.isNullOrEmpty(fileName)) {
            fileName = getNextLogFileName(getFileTask().getFileName(), getJob(), getFileTask().getNextInterval(), getJob().getLastRecordTime());
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
}
