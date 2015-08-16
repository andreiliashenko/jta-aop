package com.anli.jta.aop.transaction;

import com.anli.jta.aop.propagation.TransactionPropagation;
import com.anli.jta.aop.transaction.TransactionStrategy.Restriction;

import static com.anli.jta.aop.propagation.TransactionPropagation.MANDATORY;
import static com.anli.jta.aop.propagation.TransactionPropagation.NEVER;
import static com.anli.jta.aop.propagation.TransactionPropagation.NOT_SUPPORTED;
import static com.anli.jta.aop.propagation.TransactionPropagation.REQUIRED;
import static com.anli.jta.aop.propagation.TransactionPropagation.REQUIRES_NEW;

public class TransactionStrategyResolver {

    public TransactionStrategy resolve(TransactionPropagation propagation) {
        if (propagation == null) {
            propagation = REQUIRED;
        }
        Restriction restriction;
        if (propagation == MANDATORY) {
            restriction = Restriction.MANDATORY;
        } else if (propagation == NEVER) {
            restriction = Restriction.NEVER;
        } else {
            restriction = Restriction.SUPPORTS;
        }

        boolean isSuspensionRequired = propagation == NOT_SUPPORTED || propagation == REQUIRES_NEW;
        boolean isCreationRequired = propagation == REQUIRES_NEW || propagation == REQUIRED;
        return new TransactionStrategy(restriction, isSuspensionRequired, isCreationRequired);
    }
}
