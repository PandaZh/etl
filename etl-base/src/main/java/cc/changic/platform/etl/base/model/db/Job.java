package cc.changic.platform.etl.base.model.db;

import java.io.Serializable;
import java.util.Date;

public class Job  implements Serializable {

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", appId=" + appId +
                ", gameZoneId=" + gameZoneId +
                ", taskTable='" + taskTable + '\'' +
                ", taskId=" + taskId +
                '}';
    }

    private Integer id;
    private Integer appId;
    private Integer gameZoneId;
    private String taskTable;
    private Integer taskId;
    private Short status;
    private String modifyTime;
    private String nextTime;
    private String lastRecordTime;
    private Long lastRecordId;
    private Long lastRecordOffset;
    private String optionDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getGameZoneId() {
        return gameZoneId;
    }

    public void setGameZoneId(Integer gameZoneId) {
        this.gameZoneId = gameZoneId;
    }

    public String getTaskTable() {
        return taskTable;
    }

    public void setTaskTable(String taskTable) {
        this.taskTable = taskTable;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getNextTime() {
        return nextTime;
    }

    public void setNextTime(String nextTime) {
        this.nextTime = nextTime;
    }

    public String getLastRecordTime() {
        return lastRecordTime;
    }

    public void setLastRecordTime(String lastRecordTime) {
        this.lastRecordTime = lastRecordTime;
    }

    public Long getLastRecordId() {
        return lastRecordId;
    }

    public void setLastRecordId(Long lastRecordId) {
        this.lastRecordId = lastRecordId;
    }

    public Long getLastRecordOffset() {
        return lastRecordOffset;
    }

    public void setLastRecordOffset(Long lastRecordOffset) {
        this.lastRecordOffset = lastRecordOffset;
    }

    public String getOptionDesc() {
        return optionDesc;
    }

    public void setOptionDesc(String optionDesc) {
        this.optionDesc = optionDesc;
    }
}