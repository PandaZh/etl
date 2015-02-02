package cc.changic.platform.etl.file.commom;

/**
 * 文件任务类型
 */
public class FileJobType {
    /**
     * 全量拉取
     */
    public final static Short FILE_JOB_TYPE_FULL = 1;

    /**
     * 增量拉取
     */
    public final static Short FILE_JOB_TYPE_INCREMENTAL = 2;
}
