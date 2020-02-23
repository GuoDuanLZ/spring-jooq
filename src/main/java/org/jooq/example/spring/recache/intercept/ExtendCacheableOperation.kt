package org.jooq.example.spring.recache.intercept

import org.jooq.example.spring.cacheable.CacheType
import org.springframework.cache.interceptor.CacheableOperation

class ExtendCacheableOperation(private val ttl:Long,private val cacheType: CacheType) : CacheableOperation(Builder()) {

    class Builder : CacheableOperation.Builder() {

        private var ttl: Long = 6000

        private var cacheType: CacheType = CacheType.NORMAL

        fun setTTL(ttl:Long){
            this.ttl = ttl
        }
        fun setCacheType(cacheType: CacheType){
            this.cacheType = cacheType
        }

        override fun getOperationDescription(): StringBuilder? {
            val sb: StringBuilder = super.getOperationDescription()
            sb.append(" | ttl='")
            sb.append(this.ttl)
            sb.append("'")
            sb.append(" | cacheType='")
            sb.append(this.cacheType)
            sb.append("'")
            return sb
        }

        override fun build(): ExtendCacheableOperation {
            return ExtendCacheableOperation(ttl,cacheType)
        }
    }
}