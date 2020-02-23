package org.jooq.example.spring.service

import kotlinx.coroutines.*
import org.jooq.DSLContext
import org.jooq.example.db.generated.Tables
import org.jooq.example.db.generated.tables.pojos.User
import org.jooq.example.spring.cacheable.CacheType
import org.jooq.example.spring.cacheable.MultiCacheable
import org.jooq.example.spring.cacheable.UserCacheAdapter
import org.jooq.example.spring.dao.UserDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class UserService {

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var userDao:UserDao

//    @MultiCacheable(cacheName = "user",key = "2",cacheType = CacheType.BATCH)
    suspend fun createUserSuspend() = withContext(Dispatchers.IO) {
        return@withContext dsl.insertInto(Tables.USER, Tables.USER.UID, Tables.USER.MOBILE, Tables.USER.NAME, Tables.USER.PASSWORD).values(6, "6", "cesh1", "134").execute()
    }
//    @MultiCacheable(cacheName = "user",key = "2",cacheType = CacheType.BATCH)
    open suspend fun registerSuspend(){
        userDao.createUserSuspend()
    }
    open suspend fun listSuspend(){
        userDao.listUser()
    }

    open fun register(){
        listUser()
    }

    open fun createUser(){
        dsl.insertInto(Tables.USER, Tables.USER.UID, Tables.USER.MOBILE, Tables.USER.NAME, Tables.USER.PASSWORD).values(7, "7", "cesh1", "134").execute()
    }
    @MultiCacheable(cacheName = "user",key = "#ids",target = "uid", cacheType = CacheType.BATCH,cacheAdapter = UserCacheAdapter::class,ttl = 30)
    open fun batchUser(ids:Collection<Int>):List<User>{
       return dsl.selectFrom(Tables.USER).where(Tables.USER.UID.`in`(ids)).fetchInto(User::class.java)
    }

    @MultiCacheable(cacheName = "user345",cacheType = CacheType.NORMAL)
    open fun listUser():List<User>{
        return dsl.selectFrom(Tables.USER).fetchInto(User::class.java)
    }

}