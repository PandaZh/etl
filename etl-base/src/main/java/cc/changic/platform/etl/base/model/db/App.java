package cc.changic.platform.etl.base.model.db;

import java.io.Serializable;

public class App implements Serializable {

    @Override
    public String toString() {
        return "App{" +
                "appId=" + appId +
                ", appName='" + appName + '\'' +
                '}';
    }


    private Integer appId;

    private String appName;

    private String dbUser;

    private String dbPwd;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser == null ? null : dbUser.trim();
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd == null ? null : dbPwd.trim();
    }
}