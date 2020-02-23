package org.jooq.example.spring.transaction;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.interceptor.TransactionInterceptor;

public class SuspendTransactionInterceptor extends TransactionInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return super.invoke(invocation);
    }
}
