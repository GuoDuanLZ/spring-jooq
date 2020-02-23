package org.jooq.example.spring.cacheable


import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.springframework.cache.Cache
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.expression.EvaluationContext
import org.springframework.expression.Expression
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import java.io.Serializable
import java.lang.reflect.Method
import java.time.Duration


class MultiCacheInterceptor(private val redisCacheManager: MultiRedisCacheManager) : MethodInterceptor, Serializable {


    override fun invoke(invocation: MethodInvocation): Any? {

        val method = invocation.method
        val multiCacheable = method.getAnnotation(MultiCacheable::class.java)
        val cacheName = multiCacheable.cacheName
        val ttl = multiCacheable.ttl
        val a = redisCacheManager.cacheConfigurations[cacheName]?.entryTtl(Duration.ofSeconds(ttl))
        val multiRedisCache = redisCacheManager.getCacheWithTtl(cacheName,ttl)
        return when (multiCacheable.cacheType) {
            CacheType.NORMAL -> normalCache(invocation, method, multiCacheable, multiRedisCache)
            CacheType.BATCH -> batchCache(invocation, method, multiCacheable, multiRedisCache)
            CacheType.GLOBAL -> globalCache(invocation, method, multiCacheable, multiRedisCache)
        }
    }

    private fun normalCache(invocation: MethodInvocation, method: Method, multiCacheable: MultiCacheable, multiRedisCache: Cache?): Any? {
        val key = generateNormalKey(invocation, multiCacheable, method).toString()
        return cache(key, invocation, method, multiRedisCache)
    }

    private fun globalCache(invocation: MethodInvocation, method: Method, multiCacheable: MultiCacheable, multiRedisCache: Cache?): Any? {
        val remCount = multiCacheable.remCount
        val key = generateNormalKey(invocation, multiCacheable, method).toString()
        val rem = key.toCRC32().rem(remCount).toString()
        return cache("$key:$rem", invocation, method, multiRedisCache)
    }

    private fun cache(key: Any, invocation: MethodInvocation, method: Method, multiRedisCache: Cache?): Any {
        val value = multiRedisCache?.get(key)?.get()
        return if (value == null) {
            val invokeValue = method.invoke(invocation.`this`)
            multiRedisCache?.put(key, invokeValue)
            invokeValue
        } else {
            value
        }
    }

    private fun generateNormalKey(invocation: MethodInvocation, multiCacheable: MultiCacheable, method: Method): Any {
        val key = multiCacheable.key
        val arguments = invocation.arguments
        return if (key.isNotEmpty()) {
            if (key.contains("#")) {
                spELParser(arguments, key, method) ?: key
            } else {
                key
            }
        } else {
            normalParamsHandle(arguments)
        }
    }

    private fun normalParamsHandle(arguments: Array<Any>): Any {
        if (arguments.isEmpty()) {
            return ""
        }
        if (arguments.size == 1) {
            val param: Any = arguments[0]
            if (!param.javaClass.isArray) {
                return param
            }
        }
        return arguments.contentDeepHashCode()
    }

    private fun batchCache(invocation: MethodInvocation, method: Method, multiCacheable: MultiCacheable, multiRedisCache: Cache?): Any? {
        //如果key获取失败（在batch中有使用key赋值没有使用spEL表达式，多个）
        val keys = generateBatchKey(invocation, multiCacheable, method) ?: return invocation.proceed()
        val notHitKeys = mutableListOf<Any>()
        val returnValue = mutableListOf<Any>()
        for (key in keys) {
            val value = multiRedisCache?.get(key.toString())?.get()
            if (value == null) {
                notHitKeys.add(key.toString())
            } else {
                returnValue.add(value)
            }
        }
        if(notHitKeys.isEmpty()){
            return returnValue
        }
        val notHitValue = method.invoke(invocation.`this`, notHitKeys)
        returnValue.addAll(notHitValue as Collection<Any>)
        val cacheAdapter = multiCacheable.cacheAdapter.java.newInstance()
        val notHitValueMap = cacheAdapter.cover(notHitValue)
        notHitValueMap.forEach {
            multiRedisCache?.put(it.key, it.value)
        }
        return returnValue
    }


    private fun generateBatchKey(invocation: MethodInvocation, multiCacheable: MultiCacheable, method: Method): List<*>? {
        val key = multiCacheable.key
        val arguments = invocation.arguments
        return when {
            //方法参数也空的
            arguments.isEmpty() -> null
            //key存在且是spEL表达式
            (key.isNotEmpty() && key.contains("#")) -> {
                try {
                    spELParser(arguments, key, method) as List<*>
                } catch (e: Exception) {
                    null
                }
            }
            //有key，但不是spEL表达式
            else -> batchParamsHandle(arguments)
        }
    }

    private fun batchParamsHandle(arguments: Array<Any>): List<*>? {
        var arrayCount = 0
        var result: Any? = null
        arguments.forEach {
            if (it::class.java.isArray) {
                result = it
                arrayCount++
            }
        }
        return if (arrayCount > 1) {
            null
        } else {
            result as List<*>
        }
    }

    private fun spELParser(arguments: Array<Any>, spELString: String, method: Method): Any? {
        //SpEL解析器
        val parser = SpelExpressionParser()
        //用于获取方法参数定义名字
        val nameDiscoverer = DefaultParameterNameDiscoverer()
        val paramNames = nameDiscoverer.getParameterNames(method)
        val expression: Expression = parser.parseExpression(spELString)
        val context: EvaluationContext = StandardEvaluationContext()
        for (i in arguments.indices) {
            context.setVariable(paramNames[i], arguments[i])
        }
        return expression.getValue(context)
    }
}