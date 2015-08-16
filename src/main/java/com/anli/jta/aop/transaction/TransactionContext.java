package com.anli.jta.aop.transaction;

import javax.transaction.Transaction;

public class TransactionContext {

    protected final Transaction suspendedTransaction;
    protected final boolean created;

    public TransactionContext(Transaction suspendedTransaction, boolean created) {
        this.suspendedTransaction = suspendedTransaction;
        this.created = created;
    }

    public Transaction getSuspendedTransaction() {
        return suspendedTransaction;
    }

    public boolean isCreated() {
        return created;
    }
}
