package com.anli.jta.aop.transaction;

public class TransactionStrategy {

    protected final Restriction restriction;
    protected final boolean suspensionRequired;
    protected final boolean creationRequired;

    public TransactionStrategy(Restriction restriction, boolean suspensionRequired,
            boolean creationRequired) {
        this.restriction = restriction;
        this.suspensionRequired = suspensionRequired;
        this.creationRequired = creationRequired;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public boolean isSuspensionRequired() {
        return suspensionRequired;
    }

    public boolean isCreationRequired() {
        return creationRequired;
    }

    public static enum Restriction {

        SUPPORTS,
        NEVER,
        MANDATORY
    }
}
