package cc.changic.platform.etl.protocol.anotation;


import java.lang.annotation.*;

/**
 * 消息处理类型注解
 * @author Panda.Z
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageToken {

    short id();

    String desc() default "";

}
