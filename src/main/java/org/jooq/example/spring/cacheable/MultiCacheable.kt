package org.jooq.example.spring.cacheable

import java.lang.annotation.Documented
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass
import org.jooq.example.spring.cacheable.CacheAdapter as CacheAdapter

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
annotation class MultiCacheable(val cacheName: String = "", val key: String = "", val keyGenerator: String = "", val condition: String = "", val unless: String = "", val sync: Boolean = false, val ttl: Long = 6000, val target: String = "", val cacheType: CacheType = CacheType.NORMAL, val cacheAdapter: KClass<out CacheAdapter> = CacheAdapter()::class,val remCount:Int = 8)