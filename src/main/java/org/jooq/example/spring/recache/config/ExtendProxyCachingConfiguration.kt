//package org.jooq.example.spring.recache.config
//
//import org.jooq.example.spring.recache.intercept.ExtendCacheInterceptor
//import org.springframework.beans.factory.config.BeanDefinition
//import org.springframework.cache.annotation.AbstractCachingConfiguration
//import org.springframework.cache.annotation.AnnotationCacheOperationSource
//import org.springframework.cache.annotation.SpringCacheAnnotationParser
//import org.springframework.cache.config.CacheManagementConfigUtils
//import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor
//import org.springframework.cache.interceptor.CacheOperationSource
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.Role
//
//@Configuration
//open class ExtendProxyCachingConfiguration : AbstractCachingConfiguration(){
//
//    @Bean(name = [CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME])
//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    open fun cacheAdvisor(): BeanFactoryCacheOperationSourceAdvisor? {
//        val advisor = BeanFactoryCacheOperationSourceAdvisor()
//        advisor.setCacheOperationSource(extendCacheOperationSource())
//        advisor.advice = extendCacheInterceptor()
//        if (this.enableCaching != null) {
//            advisor.order = this.enableCaching.getNumber("order")
//        }
//        return advisor
//    }
//
//    @Bean
//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    open fun extendCacheOperationSource(): CacheOperationSource? {
//        return AnnotationCacheOperationSource(SpringCacheAnnotationParser())
//    }
//
//    @Bean
//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    open fun extendCacheInterceptor(): ExtendCacheInterceptor? {
//        val interceptor = ExtendCacheInterceptor()
//        interceptor.configure(this.errorHandler, this.keyGenerator, this.cacheResolver, this.cacheManager)
//        interceptor.cacheOperationSource = extendCacheOperationSource()
//        return interceptor
//    }
//}