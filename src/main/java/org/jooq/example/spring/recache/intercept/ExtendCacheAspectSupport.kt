package org.jooq.example.spring.recache.intercept

import org.springframework.beans.factory.BeanFactory
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.interceptor.*
import java.lang.reflect.Method
import java.util.function.Supplier

open class ExtendCacheAspectSupport : CacheAspectSupport() {

    override fun setBeanFactory(beanFactory: BeanFactory) {
        super.setBeanFactory(beanFactory)
    }

    override fun clearMetadataCache() {
        super.clearMetadataCache()
    }

    override fun configure(errorHandler: Supplier<CacheErrorHandler>?, keyGenerator: Supplier<KeyGenerator>?, cacheResolver: Supplier<CacheResolver>?, cacheManager: Supplier<CacheManager>?) {
        super.configure(errorHandler, keyGenerator, cacheResolver, cacheManager)
    }

    override fun afterPropertiesSet() {
        super.afterPropertiesSet()
    }

    override fun getKeyGenerator(): KeyGenerator {
        return super.getKeyGenerator()
    }

    override fun setCacheOperationSource(cacheOperationSource: CacheOperationSource?) {
        super.setCacheOperationSource(cacheOperationSource)
    }

    override fun getCacheOperationSource(): CacheOperationSource? {
        return super.getCacheOperationSource()
    }

    override fun setCacheResolver(cacheResolver: CacheResolver?) {
        super.setCacheResolver(cacheResolver)
    }

    override fun getCacheResolver(): CacheResolver? {
        return super.getCacheResolver()
    }

    override fun methodIdentification(method: Method, targetClass: Class<*>): String {
        return super.methodIdentification(method, targetClass)
    }

    override fun setCacheOperationSources(vararg cacheOperationSources: CacheOperationSource?) {
        super.setCacheOperationSources(*cacheOperationSources)
    }

    override fun afterSingletonsInstantiated() {
        super.afterSingletonsInstantiated()
    }

    override fun invokeOperation(invoker: CacheOperationInvoker): Any {
        return super.invokeOperation(invoker)
    }

    override fun <T : Any?> getBean(beanName: String, expectedType: Class<T>): T {
        return super.getBean(beanName, expectedType)
    }

    override fun execute(invoker: CacheOperationInvoker, target: Any, method: Method, args: Array<Any>): Any? {

        return super.execute(invoker, target, method, args)
    }

    override fun getCacheOperationMetadata(operation: CacheOperation, method: Method, targetClass: Class<*>): CacheOperationMetadata {
        return super.getCacheOperationMetadata(operation, method, targetClass)
    }

    override fun setCacheManager(cacheManager: CacheManager) {
        super.setCacheManager(cacheManager)
    }

    override fun setKeyGenerator(keyGenerator: KeyGenerator) {
        super.setKeyGenerator(keyGenerator)
    }

    override fun getCaches(context: CacheOperationInvocationContext<CacheOperation>, cacheResolver: CacheResolver): MutableCollection<out Cache> {
        return super.getCaches(context, cacheResolver)
    }

    override fun getOperationContext(operation: CacheOperation, method: Method, args: Array<Any>, target: Any, targetClass: Class<*>): CacheOperationContext {
        return super.getOperationContext(operation, method, args, target, targetClass)
    }
}