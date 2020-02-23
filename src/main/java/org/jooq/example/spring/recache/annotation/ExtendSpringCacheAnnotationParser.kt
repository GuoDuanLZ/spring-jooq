package org.jooq.example.spring.recache

import org.jooq.example.spring.recache.annotation.ExtendCacheable
import org.jooq.example.spring.recache.intercept.ExtendCacheableOperation
import org.springframework.cache.annotation.*
import org.springframework.cache.interceptor.CacheEvictOperation
import org.springframework.cache.interceptor.CacheOperation
import org.springframework.cache.interceptor.CachePutOperation
import org.springframework.cache.interceptor.CacheableOperation
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.lang.Nullable
import org.springframework.util.StringUtils
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.util.*

class ExtendSpringCacheAnnotationParser()  : SpringCacheAnnotationParser(){

    companion object{
        private val CACHE_OPERATION_ANNOTATIONS: MutableSet<Class<out Annotation>> = LinkedHashSet(8)

        init {
            CACHE_OPERATION_ANNOTATIONS.add(ExtendCacheable::class.java)
        }
    }


    override fun parseCacheAnnotations(type: Class<*>): Collection<CacheOperation>? {
        val ops = super.parseCacheAnnotations(type)
        val defaultConfig = DefaultCacheConfig(type)
        val anns: Collection<Annotation?> = AnnotatedElementUtils.getAllMergedAnnotations(type, CACHE_OPERATION_ANNOTATIONS)
        anns.stream().filter { ann: Annotation? -> ann is ExtendCacheable }.forEach { ann: Annotation? -> ops.add(parseExtendCacheableAnnotation(type, defaultConfig, ann as ExtendCacheable)) }
        return ops
    }

    override fun parseCacheAnnotations(method: Method): MutableCollection<CacheOperation>? {
        val ops = super.parseCacheAnnotations(method)
        val defaultConfig = DefaultCacheConfig(method.declaringClass)
        val anns: Collection<Annotation?> = AnnotatedElementUtils.getAllMergedAnnotations(method, CACHE_OPERATION_ANNOTATIONS)
        anns.stream().filter { ann: Annotation? -> ann is ExtendCacheable }.forEach { ann: Annotation? -> ops.add(parseExtendCacheableAnnotation(method, defaultConfig, ann as ExtendCacheable)) }
        return ops
    }

    override fun isCandidateClass(targetClass: Class<*>): Boolean {
        return AnnotationUtils.isCandidateClass(targetClass, CACHE_OPERATION_ANNOTATIONS)
    }

    private fun parseExtendCacheableAnnotation(ae: AnnotatedElement, defaultConfig: DefaultCacheConfig, extendCacheable: ExtendCacheable): CacheOperation {
        val builder = ExtendCacheableOperation.Builder()
        builder.setName(ae.toString())
        builder.setCacheNames(*extendCacheable.cacheNames)
        builder.setCondition(extendCacheable.condition)
        builder.setUnless(extendCacheable.unless)
        builder.key = extendCacheable.key
        builder.keyGenerator = extendCacheable.keyGenerator
        builder.cacheManager = extendCacheable.cacheManager
        builder.cacheResolver = extendCacheable.cacheResolver
        builder.setSync(extendCacheable.sync)
        builder.setTTL(extendCacheable.ttl)
        builder.setCacheType(extendCacheable.cacheType)
        defaultConfig.applyDefault(builder)
        val op = builder.build()
        validateCacheOperation(ae, op)
        return op
    }
    private fun validateCacheOperation(ae: AnnotatedElement, operation: CacheOperation) {
        check(!(StringUtils.hasText(operation.key) && StringUtils.hasText(operation.keyGenerator))) {
            "Invalid cache annotation configuration on '" +
                    ae.toString() + "'. Both 'key' and 'keyGenerator' attributes have been set. " +
                    "These attributes are mutually exclusive: either set the SpEL expression used to" +
                    "compute the key at runtime or set the name of the KeyGenerator bean to use."
        }
        check(!(StringUtils.hasText(operation.cacheManager) && StringUtils.hasText(operation.cacheResolver))) {
            "Invalid cache annotation configuration on '" +
                    ae.toString() + "'. Both 'cacheManager' and 'cacheResolver' attributes have been set. " +
                    "These attributes are mutually exclusive: the cache manager is used to configure a" +
                    "default cache resolver if none is set. If a cache resolver is set, the cache manager" +
                    "won't be used."
        }
    }

    internal class DefaultCacheConfig(private val target: Class<*>) {
        @Nullable
        private var cacheNames: Array<String>? = null
        @Nullable
        private var keyGenerator: String? = null
        @Nullable
        private var cacheManager: String? = null
        @Nullable
        private var cacheResolver: String? = null
        private var initialized = false
        /**
         * Apply the defaults to the specified [CacheOperation.Builder].
         * @param builder the operation builder to update
         */
        fun applyDefault(builder: CacheOperation.Builder) {
            if (!initialized) {
                val annotation = AnnotatedElementUtils.findMergedAnnotation(target, CacheConfig::class.java)
                if (annotation != null) {
                    cacheNames = annotation.cacheNames
                    keyGenerator = annotation.keyGenerator
                    cacheManager = annotation.cacheManager
                    cacheResolver = annotation.cacheResolver
                }
                initialized = true
            }
            if (builder.cacheNames.isEmpty() && cacheNames != null) {
                builder.setCacheNames(*cacheNames!!)
            }
            if (!StringUtils.hasText(builder.key) && !StringUtils.hasText(builder.keyGenerator) &&
                    StringUtils.hasText(keyGenerator)) {
                builder.keyGenerator = keyGenerator
            }
            if (StringUtils.hasText(builder.cacheManager) || StringUtils.hasText(builder.cacheResolver)) { // One of these is set so we should not inherit anything
            } else if (StringUtils.hasText(cacheResolver)) {
                builder.cacheResolver = cacheResolver
            } else if (StringUtils.hasText(cacheManager)) {
                builder.cacheManager = cacheManager
            }
        }

    }
}