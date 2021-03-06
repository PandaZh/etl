package cc.changic.platform.etl.base.dao;

import cc.changic.platform.etl.base.model.db.FileTask;

import java.util.List;

public interface FileTaskMapper {

   List<FileTask> selectAll();
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_task_file_task
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_task_file_task
     *
     * @mbggenerated
     */
    int insert(FileTask record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_task_file_task
     *
     * @mbggenerated
     */
    int insertSelective(FileTask record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_task_file_task
     *
     * @mbggenerated
     */
    FileTask selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_task_file_task
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(FileTask record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_task_file_task
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(FileTask record);
}