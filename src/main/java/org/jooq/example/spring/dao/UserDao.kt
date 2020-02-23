package org.jooq.example.spring.dao

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.example.db.generated.Tables
import org.jooq.example.db.generated.tables.pojos.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
open class UserDao {
    @Autowired
    private lateinit var dsl: DSLContext


//    @MultiCacheable(cacheName = "user",key = "2",cacheType = CacheType.NORMAL)
    open suspend fun createUserSuspend() = withContext(Dispatchers.IO) {
        return@withContext dsl.insertInto(Tables.USER, Tables.USER.UID, Tables.USER.MOBILE, Tables.USER.NAME, Tables.USER.PASSWORD).values(6, "6", "cesh1", "134").execute()
    }
    @Cacheable(value = ["user1"],key = "789")
    open suspend fun listUser():List<User> = withContext(Dispatchers.IO){
        return@withContext dsl.selectFrom(Tables.USER).fetchInto(User::class.java)
    }
}