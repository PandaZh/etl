package cc.changic.platform.etl.base.model.db;


import cc.changic.platform.etl.base.annotation.TaskTable;
import cc.changic.platform.etl.base.model.ETLTask;

@TaskTable(tableName = "t_c_task_file")
public class TaskFile extends ETLTask {

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column db_etl_server_0001.t_c_task_file.task_id
     *
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    private Integer taskId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column db_etl_server_0001.t_c_task_file.app_id
     *
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    private Integer appId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column db_etl_server_0001.t_c_task_file.task_name
     *
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    private String taskName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column db_etl_server_0001.t_c_task_file.source_path
     *
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    private String sourcePath;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column db_etl_server_0001.t_c_task_file.target_path
     *
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    private String targetPath;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column db_etl_server_0001.t_c_task_file.task_type
     *
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    private Short taskType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column db_etl_server_0001.t_c_task_file.ods_id
     *
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    private Integer odsId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column db_etl_server_0001.t_c_task_file.insert_sql
     *
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    private String insertSql;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column db_etl_server_0001.t_c_task_file.delete_sql
     *
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    private String deleteSql;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_task_file.task_id
     *
     * @return the value of db_etl_server_0001.t_c_task_file.task_id
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public Integer getTaskId() {
        return taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_task_file.task_id
     *
     * @param taskId the value for db_etl_server_0001.t_c_task_file.task_id
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_task_file.app_id
     *
     * @return the value of db_etl_server_0001.t_c_task_file.app_id
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public Integer getAppId() {
        return appId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_task_file.app_id
     *
     * @param appId the value for db_etl_server_0001.t_c_task_file.app_id
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_task_file.task_name
     *
     * @return the value of db_etl_server_0001.t_c_task_file.task_name
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_task_file.task_name
     *
     * @param taskName the value for db_etl_server_0001.t_c_task_file.task_name
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_task_file.source_path
     *
     * @return the value of db_etl_server_0001.t_c_task_file.source_path
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_task_file.source_path
     *
     * @param sourcePath the value for db_etl_server_0001.t_c_task_file.source_path
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath == null ? null : sourcePath.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_task_file.target_path
     *
     * @return the value of db_etl_server_0001.t_c_task_file.target_path
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public String getTargetPath() {
        return targetPath;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_task_file.target_path
     *
     * @param targetPath the value for db_etl_server_0001.t_c_task_file.target_path
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath == null ? null : targetPath.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_task_file.task_type
     *
     * @return the value of db_etl_server_0001.t_c_task_file.task_type
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public Short getTaskType() {
        return taskType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_task_file.task_type
     *
     * @param taskType the value for db_etl_server_0001.t_c_task_file.task_type
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public void setTaskType(Short taskType) {
        this.taskType = taskType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_task_file.ods_id
     *
     * @return the value of db_etl_server_0001.t_c_task_file.ods_id
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public Integer getOdsId() {
        return odsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_task_file.ods_id
     *
     * @param odsId the value for db_etl_server_0001.t_c_task_file.ods_id
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public void setOdsId(Integer odsId) {
        this.odsId = odsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_task_file.insert_sql
     *
     * @return the value of db_etl_server_0001.t_c_task_file.insert_sql
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public String getInsertSql() {
        return insertSql;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_task_file.insert_sql
     *
     * @param insertSql the value for db_etl_server_0001.t_c_task_file.insert_sql
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public void setInsertSql(String insertSql) {
        this.insertSql = insertSql == null ? null : insertSql.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_task_file.delete_sql
     *
     * @return the value of db_etl_server_0001.t_c_task_file.delete_sql
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public String getDeleteSql() {
        return deleteSql;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_task_file.delete_sql
     *
     * @param deleteSql the value for db_etl_server_0001.t_c_task_file.delete_sql
     * @mbggenerated Fri Jan 30 12:07:19 CST 2015
     */
    public void setDeleteSql(String deleteSql) {
        this.deleteSql = deleteSql == null ? null : deleteSql.trim();
    }
}