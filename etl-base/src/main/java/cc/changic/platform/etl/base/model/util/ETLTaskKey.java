package cc.changic.platform.etl.base.model.util;

/**
 * 任务Key,多个任务类型分为多张表,使用表名和ID作为唯一Key,避免ID重复
 */
public class ETLTaskKey {

    private Integer taskID;
    private String tableName;

    /**
     *
     * @param taskID 任务ID
     * @param tableName 任务表名
     */
    public ETLTaskKey(Integer taskID, String tableName) {
        this.taskID = taskID;
        this.tableName = tableName;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ETLTaskKey that = (ETLTaskKey) o;

        if (taskID != null ? !taskID.equals(that.taskID) : that.taskID != null) return false;
        if (tableName != null ? !tableName.equals(that.tableName) : that.tableName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = taskID != null ? taskID.hashCode() : 0;
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }
}

