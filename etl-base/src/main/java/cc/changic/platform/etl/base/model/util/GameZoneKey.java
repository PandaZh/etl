package cc.changic.platform.etl.base.model.util;

/**
 * 游戏区Key,app_id和game_zone_id唯一约束
 */
public class GameZoneKey {

    private Integer appID;
    private Integer gameZoneID;

    /**
     * @param appID      应用ID
     * @param gameZoneID 游戏区ID
     */
    public GameZoneKey(Integer appID, Integer gameZoneID) {
        this.appID = appID;
        this.gameZoneID = gameZoneID;
    }

    public Integer getAppID() {
        return appID;
    }

    public Integer getGameZoneID() {
        return gameZoneID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameZoneKey that = (GameZoneKey) o;

        if (appID != null ? !appID.equals(that.appID) : that.appID != null) return false;
        if (gameZoneID != null ? !gameZoneID.equals(that.gameZoneID) : that.gameZoneID != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = appID != null ? appID.hashCode() : 0;
        result = 31 * result + (gameZoneID != null ? gameZoneID.hashCode() : 0);
        return result;
    }
}
