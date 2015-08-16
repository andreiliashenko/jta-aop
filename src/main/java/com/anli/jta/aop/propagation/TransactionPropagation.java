package com.anli.jta.aop.propagation;

public enum TransactionPropagation {

    SUPPORTS,
    NOT_SUPPORTED,
    MANDATORY,
    NEVER,
    REQUIRED,
    REQUIRES_NEW
}
