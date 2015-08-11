package cc.changic.platform.etl.base.model.db;

import java.io.Serializable;

public class GameZone extends GameZoneKey implements Serializable {

    @Override
    public String toString() {
        return "GameZone{" +
                "id=" + id +
                ", gameZoneName='" + gameZoneName + '\'' +
                ", timezone=" + timezone +
                ", gameZoneStatus=" + gameZoneStatus +
                '}';
    }


    private Integer id;
    private String gameZoneName;
    private String etlClientIp;
    private String dbIp;
    private Short dbPort;
    private Short timezone;
    private String site;
    private Short maxRunJob;
    private Short gameZoneStatus;
    private Short fileDeleteInterval;

    public Short getFileDeleteInterval() {
        return fileDeleteInterval;
    }

    public void setFileDeleteInterval(Short fileDeleteInterval) {
        this.fileDeleteInterval = fileDeleteInterval;
    }

    public Short getGameZoneStatus() {
        return gameZoneStatus;
    }

    public void setGameZoneStatus(Short gameZoneStatus) {
        this.gameZoneStatus = gameZoneStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_game_zone.id
     *
     * @return the value of db_etl_server_0001.t_c_game_zone.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_game_zone.id
     *
     * @param id the value for db_etl_server_0001.t_c_game_zone.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_game_zone.game_zone_name
     *
     * @return the value of db_etl_server_0001.t_c_game_zone.game_zone_name
     *
     * @mbggenerated
     */
    public String getGameZoneName() {
        return gameZoneName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_game_zone.game_zone_name
     *
     * @param gameZoneName the value for db_etl_server_0001.t_c_game_zone.game_zone_name
     *
     * @mbggenerated
     */
    public void setGameZoneName(String gameZoneName) {
        this.gameZoneName = gameZoneName == null ? null : gameZoneName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_game_zone.etl_client_ip
     *
     * @return the value of db_etl_server_0001.t_c_game_zone.etl_client_ip
     *
     * @mbggenerated
     */
    public String getEtlClientIp() {
        return etlClientIp;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_game_zone.etl_client_ip
     *
     * @param etlClientIp the value for db_etl_server_0001.t_c_game_zone.etl_client_ip
     *
     * @mbggenerated
     */
    public void setEtlClientIp(String etlClientIp) {
        this.etlClientIp = etlClientIp == null ? null : etlClientIp.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_game_zone.db_ip
     *
     * @return the value of db_etl_server_0001.t_c_game_zone.db_ip
     *
     * @mbggenerated
     */
    public String getDbIp() {
        return dbIp;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_game_zone.db_ip
     *
     * @param dbIp the value for db_etl_server_0001.t_c_game_zone.db_ip
     *
     * @mbggenerated
     */
    public void setDbIp(String dbIp) {
        this.dbIp = dbIp == null ? null : dbIp.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_game_zone.db_port
     *
     * @return the value of db_etl_server_0001.t_c_game_zone.db_port
     *
     * @mbggenerated
     */
    public Short getDbPort() {
        return dbPort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_game_zone.db_port
     *
     * @param dbPort the value for db_etl_server_0001.t_c_game_zone.db_port
     *
     * @mbggenerated
     */
    public void setDbPort(Short dbPort) {
        this.dbPort = dbPort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_game_zone.timezone
     *
     * @return the value of db_etl_server_0001.t_c_game_zone.timezone
     *
     * @mbggenerated
     */
    public Short getTimezone() {
        return timezone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_game_zone.timezone
     *
     * @param timezone the value for db_etl_server_0001.t_c_game_zone.timezone
     *
     * @mbggenerated
     */
    public void setTimezone(Short timezone) {
        this.timezone = timezone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_game_zone.site
     *
     * @return the value of db_etl_server_0001.t_c_game_zone.site
     *
     * @mbggenerated
     */
    public String getSite() {
        return site;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_game_zone.site
     *
     * @param site the value for db_etl_server_0001.t_c_game_zone.site
     *
     * @mbggenerated
     */
    public void setSite(String site) {
        this.site = site == null ? null : site.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column db_etl_server_0001.t_c_game_zone.max_run_job
     *
     * @return the value of db_etl_server_0001.t_c_game_zone.max_run_job
     *
     * @mbggenerated
     */
    public Short getMaxRunJob() {
        return maxRunJob;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column db_etl_server_0001.t_c_game_zone.max_run_job
     *
     * @param maxRunJob the value for db_etl_server_0001.t_c_game_zone.max_run_job
     *
     * @mbggenerated
     */
    public void setMaxRunJob(Short maxRunJob) {
        this.maxRunJob = maxRunJob;
    }
}