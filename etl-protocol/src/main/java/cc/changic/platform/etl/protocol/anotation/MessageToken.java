package cc.changic.platform.etl.protocol.anotation;


import java.lang.annotation.*;

/**
 * Created by Panda.Z on 2015/1/19.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageToken {

    short id();

    String desc() default "";

}
