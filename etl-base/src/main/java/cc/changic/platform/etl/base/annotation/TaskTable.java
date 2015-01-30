package cc.changic.platform.etl.base.annotation;

import java.lang.annotation.*;

/**
 * Created by Panda.Z on 2015/1/30.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TaskTable {
    String tableName();
}
