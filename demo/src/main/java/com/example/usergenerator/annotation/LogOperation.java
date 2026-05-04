package com.example.usergenerator.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {

    String operationType() default "";

    String targetType() default "";

    String description() default "";
}
