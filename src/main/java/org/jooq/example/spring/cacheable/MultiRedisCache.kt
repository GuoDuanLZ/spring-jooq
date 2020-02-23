package org.jooq.example.spring.cacheable

import org.springframework.cache.Cache
import org.springframework.data.redis.cache.RedisCache
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheWriter
import java.util.concurrent.Callable

class MultiRedisCache(name: String, cacheWriter: RedisCacheWriter, cacheConfig: RedisCacheConfiguration) : RedisCache(name, cacheWriter, cacheConfig) {

    override fun clear() {
        super.clear()
    }

    override fun getName(): String {
        return super.getName()
    }

    override fun convertKey(key: Any): String {
        return super.convertKey(key)
    }

    override fun putIfAbsent(key: Any, value: Any?): Cache.ValueWrapper {
        return super.putIfAbsent(key, value)
    }

    override fun getCacheConfiguration(): RedisCacheConfiguration {
        return super.getCacheConfiguration()
    }

    override fun preProcessCacheValue(value: Any?): Any? {
        return super.preProcessCacheValue(value)
    }

    override fun <T : Any?> get(key: Any, valueLoader: Callable<T>): T {
        return super.get(key, valueLoader)
    }

    override fun evict(key: Any) {
        super.evict(key)
    }

    override fun lookup(key: Any): Any {
        return super.lookup(key)
    }

    override fun put(key: Any, value: Any?) {
        super.put(key, value)
    }

    override fun getNativeCache(): RedisCacheWriter {
        return super.getNativeCache()
    }

    override fun deserializeCacheValue(value: ByteArray): Any? {
        return super.deserializeCacheValue(value)
    }

    override fun createCacheKey(key: Any): String {
        return super.createCacheKey(key)
    }

    override fun serializeCacheKey(cacheKey: String): ByteArray {
        return super.serializeCacheKey(cacheKey)
    }

    override fun serializeCacheValue(value: Any): ByteArray {
        return super.serializeCacheValue(value)
    }
}