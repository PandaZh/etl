package cc.changic.platform.etl.base.model;

import cc.changic.platform.etl.base.annotation.TaskTable;

/**
 * Created by Panda.Z on 2015/1/30.
 */
public abstract class ETLTask {

    public String getTaskTable() {
        TaskTable taskTable = this.getClass().getAnnotation(TaskTable.class);
        if (null == taskTable)
            throw new IllegalArgumentException(this.getClass() + " extends ETLTask must annotate @TaskTable");
        return taskTable.tableName();
    }
}
