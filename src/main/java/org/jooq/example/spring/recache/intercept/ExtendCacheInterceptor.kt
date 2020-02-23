package org.jooq.example.spring.recache.intercept

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.jooq.example.spring.recache.intercept.ExtendCacheAspectSupport
import org.springframework.cache.interceptor.CacheOperationInvoker
import org.springframework.cache.interceptor.CacheOperationInvoker.ThrowableWrapper
import java.io.Serializable

class ExtendCacheInterceptor : ExtendCacheAspectSupport() , MethodInterceptor, Serializable {
    override fun invoke(invocation: MethodInvocation?): Any {
        val method = invocation!!.method

        val aopAllianceInvoker = CacheOperationInvoker {
            try {
                 invocation.proceed()
            } catch (ex: Throwable) {
                throw ThrowableWrapper(ex)
            }
        }

        return try {
            //todo 待重写，重写下面这个方法来实现定制功能
            execute(aopAllianceInvoker, invocation.getThis(), method, invocation.arguments)!!
        } catch (th: ThrowableWrapper) {
            throw th.original
        }
    }
}