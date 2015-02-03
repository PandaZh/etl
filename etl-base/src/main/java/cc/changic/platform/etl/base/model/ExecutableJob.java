package cc.changic.platform.etl.base.model;


import cc.changic.platform.etl.base.model.db.GameZoneKey;

import java.util.Date;

/**
 * 可执行的任务
 */
public interface ExecutableJob extends Comparable<ExecutableJob>{
    public final static Short SUCCESS = 1;
    public final static Short FAILED = 0;
    Integer getJobID();
    Short getJobType();
    Date getNextTime();
    Short getNextInterval();
    GameZoneKey getGameZoneKey();
}
