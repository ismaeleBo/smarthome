package com.ismaelebonaventura.home_service.aop;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Audited {
    String value() default "";
}