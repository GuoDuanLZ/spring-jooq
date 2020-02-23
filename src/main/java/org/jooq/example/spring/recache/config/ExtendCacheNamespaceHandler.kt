package org.jooq.example.spring.recache.config

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.xml.NamespaceHandlerSupport
import org.springframework.cache.config.CacheNamespaceHandler
import org.springframework.util.StringUtils
import org.w3c.dom.Element

class ExtendCacheNamespaceHandler : NamespaceHandlerSupport() {
    companion object{
        val CACHE_MANAGER_ATTRIBUTE = "cache-manager"
        val DEFAULT_CACHE_MANAGER_BEAN_NAME = "cacheManager"

        fun extractCacheManager(element: Element): String? {
            return if (element.hasAttribute(CACHE_MANAGER_ATTRIBUTE)) element.getAttribute(CACHE_MANAGER_ATTRIBUTE) else DEFAULT_CACHE_MANAGER_BEAN_NAME
        }
        fun parseKeyGenerator(element: Element, def: BeanDefinition): BeanDefinition? {
            val name = element.getAttribute("key-generator")
            if (StringUtils.hasText(name)) {
                def.propertyValues.add("keyGenerator", RuntimeBeanReference(name.trim { it <= ' ' }))
            }
            return def
        }
    }




    override fun init() {
        registerBeanDefinitionParser("extend-advice", ExtendCacheAdviceParser())
    }
}