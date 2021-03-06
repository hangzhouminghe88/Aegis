package com.mingheinfo.common.annotation;

import java.lang.annotation.*;

import com.mingheinfo.common.enums.LogType;

/**
 * 系统日志自定义注解
 * 
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})//作用于参数或方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {

        /**
         * 日志名称
         * @return
         */
        String description() default "";

        /**
         * 日志类型
         * @return
         */
        LogType type() default LogType.OPERATION;
}
