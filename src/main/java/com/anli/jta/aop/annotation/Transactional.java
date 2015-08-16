package com.anli.jta.aop.annotation;

import com.anli.jta.aop.propagation.TransactionPropagation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.anli.jta.aop.propagation.TransactionPropagation.REQUIRED;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Transactional {

    TransactionPropagation value() default REQUIRED;
}
