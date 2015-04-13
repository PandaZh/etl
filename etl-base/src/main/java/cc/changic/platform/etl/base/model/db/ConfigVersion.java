package cc.changic.platform.etl.base.model.db;

import java.io.Serializable;
import java.util.Date;

public class ConfigVersion implements Serializable {

    private Integer id;
    private Date modifyTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigVersion that = (ConfigVersion) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (modifyTime != null ? !modifyTime.equals(that.modifyTime) : that.modifyTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (modifyTime != null ? modifyTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ConfigVersion{" +
                "id=" + id +
                ", modifyTime=" + modifyTime +
                '}';
    }
}