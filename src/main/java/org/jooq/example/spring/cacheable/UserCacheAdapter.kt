package org.jooq.example.spring.cacheable

import org.jooq.example.db.generated.tables.pojos.User

class UserCacheAdapter:CacheAdapter() {

    override fun cover(data: Any): Map<String, Any> {
        return (data as List<Any>).associateBy { (it as User).uid.toString() }
    }
}