package org.jooq.example.spring.cacheable

import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor
import org.springframework.aop.support.StaticMethodMatcherPointcut
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.io.Serializable
import java.lang.reflect.Method

@Configuration
@ConditionalOnClass(RedisConnectionFactory::class)
open class MultiCacheConfig {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    open fun multiCacheAdvisor(redisCacheManager: MultiRedisCacheManager): MultiCacheBeanFactorySourceAdvisor {
        val advisor = MultiCacheBeanFactorySourceAdvisor()
        advisor.advice = MultiCacheInterceptor(redisCacheManager)
        return advisor
    }
    @Bean("defaultRedisCacheConfiguration")
    @ConditionalOnMissingBean
    open fun defaultRedisCacheConfiguration(): RedisCacheConfiguration? {
        return RedisCacheConfiguration.defaultCacheConfig()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun redisCacheWriter(redisConnectionFactory: RedisConnectionFactory?): RedisCacheWriter? {
        return RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory)
    }


    @Bean("multiRedisCacheManager")
    @ConditionalOnMissingBean
    open fun multiRedisCacheManager(redisCacheWriter: RedisCacheWriter,
                                    defaultRedisCacheConfiguration: RedisCacheConfiguration): MultiRedisCacheManager {
        return MultiRedisCacheManager(
                redisCacheWriter,
                defaultRedisCacheConfiguration)
    }
}


class MultiCacheBeanFactorySourceAdvisor : AbstractBeanFactoryPointcutAdvisor(){
    override fun getPointcut(): Pointcut {
        return MultiSourcePointcut()
    }
}
class MultiSourcePointcut : StaticMethodMatcherPointcut(), Serializable {
    override fun matches(method: Method, targetClass: Class<*>): Boolean {
        return method.getAnnotation(MultiCacheable::class.java) != null
    }
}

