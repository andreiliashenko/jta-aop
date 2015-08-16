package com.anli.jta.aop;

import com.anli.jta.aop.annotation.Transactional;
import com.anli.jta.aop.transaction.TransactionContext;
import com.anli.jta.aop.transaction.TransactionHandler;
import com.anli.jta.aop.transaction.TransactionStrategy;
import com.anli.jta.aop.transaction.TransactionStrategyResolver;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import static java.util.Arrays.asList;

public class JtaTransactionInterceptor implements MethodInterceptor {

    private static final String DEFAULT_TRANSACTION_MANAGER_JNDI_NAME = "java:comp/TransactionManager";

    private final TransactionManager manager;
    private final Set<Class<? extends Throwable>> noRollbackFor;
    private final TransactionHandler transactionHandler;
    private final TransactionStrategyResolver strategyResolver;
    private final Map<Method, TransactionStrategy> strategies;

    public JtaTransactionInterceptor(String transactionManagerJndiName,
            Class<? extends Throwable>[] noRollbackFor) {
        try {
            this.manager = InitialContext.doLookup(transactionManagerJndiName);
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
        this.noRollbackFor = new HashSet<>(asList(noRollbackFor));
        this.transactionHandler = new TransactionHandler();
        this.strategyResolver = new TransactionStrategyResolver();
        this.strategies = new HashMap<>();
    }

    public JtaTransactionInterceptor(Class<? extends Throwable>[] noRollbackFor) {
        this(DEFAULT_TRANSACTION_MANAGER_JNDI_NAME, noRollbackFor);
    }

    public JtaTransactionInterceptor(String transactinManagerJndiName) {
        this(transactinManagerJndiName, new Class[0]);
    }

    public JtaTransactionInterceptor() {
        this(DEFAULT_TRANSACTION_MANAGER_JNDI_NAME);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TransactionStrategy strategy = getStrategy(invocation.getMethod());
        TransactionContext context = transactionHandler.begin(manager, strategy);
        boolean commit = false;
        try {
            Object result = invocation.proceed();
            commit = true;
            return result;
        } catch (Throwable exception) {
            if (noRollbackFor.contains(exception.getClass())) {
                commit = true;
            }
            throw exception;
        } finally {
            transactionHandler.close(manager, context, commit);
        }
    }

    protected TransactionStrategy getStrategy(Method method) {
        TransactionStrategy strategy = strategies.get(method);
        if (strategy == null) {
            Transactional annotation = method.getAnnotation(Transactional.class);
            if (annotation == null) {
                throw new IllegalStateException("Method " + method + " is not annotated with "
                        + Transactional.class.getCanonicalName());
            }
            strategy = strategyResolver.resolve(annotation.value());
            strategies.put(method, strategy);
        }
        return strategy;
    }
}
