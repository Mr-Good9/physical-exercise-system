// 日志注解
package com.good.physicalexercisesystem.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    // 日志内容
    String value() default "";

    // 日志级别: info, warning, error
    String level() default "info";
}
