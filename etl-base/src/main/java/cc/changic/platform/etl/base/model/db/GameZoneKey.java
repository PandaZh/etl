package cc.changic.platform.etl.base.model.db;

public class GameZoneKey {

    private Integer appId;
    private Integer gameZoneId;
    public GameZoneKey(){}
    /**
     * @param appId      应用ID
     * @param gameZoneId 游戏区ID
     */
    public GameZoneKey(Integer appId, Integer gameZoneId) {
        this.appId = appId;
        this.gameZoneId = gameZoneId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameZoneKey that = (GameZoneKey) o;

        if (appId != null ? !appId.equals(that.appId) : that.appId != null) return false;
        if (gameZoneId != null ? !gameZoneId.equals(that.gameZoneId) : that.gameZoneId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = appId != null ? appId.hashCode() : 0;
        result = 31 * result + (gameZoneId != null ? gameZoneId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GameZoneKey[" +
                "appId=" + appId +
                ", gameZoneId=" + gameZoneId +
                ']';
    }
}