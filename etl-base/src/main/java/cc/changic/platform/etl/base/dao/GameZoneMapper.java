package cc.changic.platform.etl.base.dao;

import cc.changic.platform.etl.base.model.db.GameZone;
import cc.changic.platform.etl.base.model.db.GameZoneKey;

import java.util.List;

public interface GameZoneMapper {


    List<GameZone> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_game_zone
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(GameZoneKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_game_zone
     *
     * @mbggenerated
     */
    int insert(GameZone record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_game_zone
     *
     * @mbggenerated
     */
    int insertSelective(GameZone record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_game_zone
     *
     * @mbggenerated
     */
    GameZone selectByPrimaryKey(GameZoneKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_game_zone
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(GameZone record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_game_zone
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(GameZone record);
}