package com.anli.jta.aop.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import static com.anli.jta.aop.transaction.TransactionStrategy.Restriction.MANDATORY;
import static com.anli.jta.aop.transaction.TransactionStrategy.Restriction.NEVER;
import static javax.transaction.Status.STATUS_ACTIVE;

public class TransactionHandler {

    public TransactionContext begin(TransactionManager manager, TransactionStrategy strategy) {
        try {
            Transaction currentTransaction = manager.getTransaction();
            boolean exists = doesTransactionExist(currentTransaction);
            if (exists && strategy.getRestriction() == NEVER) {
                throw new IllegalStateException("Method can not be called in transaction");
            }
            if (!exists && strategy.getRestriction() == MANDATORY) {
                throw new IllegalStateException("Method should be called in transaction");
            }
            Transaction suspended = null;
            boolean created = false;
            if (exists && strategy.isSuspensionRequired()) {
                suspended = manager.suspend();
                exists = false;
            }
            if (!exists && strategy.isCreationRequired()) {
                manager.begin();
                created = true;
            }
            return new TransactionContext(suspended, created);
        } catch (SystemException | NotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected boolean doesTransactionExist(Transaction transaction) throws SystemException {
        return transaction != null && transaction.getStatus() == STATUS_ACTIVE;
    }

    public void close(TransactionManager manager, TransactionContext context, boolean commit) {
        try {
            if (context.isCreated()) {
                if (commit) {
                    manager.commit();
                } else {
                    manager.rollback();
                }
            }
            Transaction suspended = context.getSuspendedTransaction();
            if (suspended != null) {
                manager.resume(suspended);
            }
        } catch (RollbackException | HeuristicMixedException |
                HeuristicRollbackException | SecurityException | SystemException |
                InvalidTransactionException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
