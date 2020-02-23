package org.jooq.example.spring.recache.config

import org.jooq.example.spring.cacheable.CacheType
import org.jooq.example.spring.recache.intercept.ExtendCacheInterceptor
import org.jooq.example.spring.recache.intercept.ExtendCacheableOperation
import org.springframework.beans.factory.config.TypedStringValue
import org.springframework.beans.factory.parsing.ReaderContext
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.ManagedList
import org.springframework.beans.factory.support.ManagedMap
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
import org.springframework.beans.factory.xml.ParserContext
import org.springframework.cache.interceptor.CacheOperation
import org.springframework.cache.interceptor.NameMatchCacheOperationSource
import org.springframework.lang.Nullable
import org.springframework.util.StringUtils
import org.springframework.util.xml.DomUtils
import org.w3c.dom.Element
import java.util.*


class ExtendCacheAdviceParser : AbstractSingleBeanDefinitionParser() {
    companion object {
        private val EXTENDCACHEABLE_ELEMENT= "extend-cacheable"
        private val DEFS_ELEMENT = "caching"
        private val METHOD_ATTRIBUTE = "method"
    }

    override fun getBeanClass(element: Element): Class<*> {
        return ExtendCacheInterceptor::class.java
    }

    override fun doParse(element: Element, parserContext: ParserContext, builder: BeanDefinitionBuilder) {
        builder.addPropertyReference("cacheManager", ExtendCacheNamespaceHandler.extractCacheManager(element))
        ExtendCacheNamespaceHandler.parseKeyGenerator(element, builder.beanDefinition)
        val cacheDefs = DomUtils.getChildElementsByTagName(element, DEFS_ELEMENT)
        if (cacheDefs.isNotEmpty()) { // Using attributes source.
            val attributeSourceDefinitions: List<RootBeanDefinition> = parseDefinitionsSources(cacheDefs, parserContext)
            builder.addPropertyValue("cacheOperationSources", attributeSourceDefinitions)
        } else { // Assume annotations source.
            builder.addPropertyValue("cacheOperationSources",
                    RootBeanDefinition("org.springframework.cache.annotation.AnnotationCacheOperationSource"))
        }
    }

    private fun parseDefinitionsSources(definitions: List<Element>, parserContext: ParserContext): List<RootBeanDefinition> {
        val defs = ManagedList<RootBeanDefinition>(definitions.size)

        // extract default param for the definition
        // extract default param for the definition
        for (element in definitions) {
            defs.add(parseDefinitionSource(element, parserContext))
        }

        return defs
    }

    private fun parseDefinitionSource(definition: Element, parserContext: ParserContext): RootBeanDefinition {
        val prop = Props(definition)
        // add cacheable first
        val cacheOpMap = ManagedMap<TypedStringValue, MutableCollection<CacheOperation>>()
        cacheOpMap.source = parserContext.extractSource(definition)

        val cacheableCacheMethods = DomUtils.getChildElementsByTagName(definition, EXTENDCACHEABLE_ELEMENT)

        for (opElement in cacheableCacheMethods) {
            val name: String? = prop.merge(opElement, parserContext.readerContext)
            val nameHolder = TypedStringValue(name)
            nameHolder.source = parserContext.extractSource(opElement)
            val builder: ExtendCacheableOperation.Builder = prop.merge(opElement,
                    parserContext.readerContext, ExtendCacheableOperation.Builder())
            builder.setTTL(getAttributeValue(opElement, "ttl", "6000").toLong())
            builder.setCacheType(CacheType.valueOf(getAttributeValue(opElement, "cacheType", "NORMAL")))
            var col = cacheOpMap[nameHolder]
            if (col == null) {
                col = ArrayList(2)
                cacheOpMap[nameHolder] = col
            }
            col.add(builder.build())
        }
        val attributeSourceDefinition = RootBeanDefinition(NameMatchCacheOperationSource::class.java)
        attributeSourceDefinition.source = parserContext.extractSource(definition)
        attributeSourceDefinition.propertyValues.add("nameMap", cacheOpMap)
        return attributeSourceDefinition
    }
    private fun getAttributeValue(element: Element, attributeName: String, defaultValue: String): String {
        val attribute = element.getAttribute(attributeName)
        return if (StringUtils.hasText(attribute)) {
            attribute.trim { it <= ' ' }
        } else defaultValue
    }

    /**
     * Simple, reusable class used for overriding defaults.
     */
    inner class Props internal constructor(root: Element) {
        private val key: String
        private val keyGenerator: String
        private val cacheManager: String
        private val condition: String
        private val method: String
        private var caches: Array<String>? = null

        fun <T : CacheOperation.Builder?> merge(element: Element, readerCtx: ReaderContext, builder: T): T {
            val cache = element.getAttribute("cache")
            // sanity check
            var localCaches: Array<String>? = caches
            if (StringUtils.hasText(cache)) {
                localCaches = StringUtils.commaDelimitedListToStringArray(cache.trim { it <= ' ' })
            }
            if (localCaches != null) {
                builder!!.setCacheNames(*localCaches)
            } else {
                readerCtx.error("No cache specified for " + element.nodeName, element)
            }
            builder!!.key = getAttributeValue(element, "key", key)
            builder.keyGenerator = getAttributeValue(element, "key-generator", keyGenerator)
            builder.cacheManager = getAttributeValue(element, "cache-manager", cacheManager)
            builder.setCondition(getAttributeValue(element, "condition", condition))
            check(!(StringUtils.hasText(builder.key) && StringUtils.hasText(builder.keyGenerator))) {
                "Invalid cache advice configuration on '" +
                        element.toString() + "'. Both 'key' and 'keyGenerator' attributes have been set. " +
                        "These attributes are mutually exclusive: either set the SpEL expression used to" +
                        "compute the key at runtime or set the name of the KeyGenerator bean to use."
            }
            return builder
        }

        @Nullable
        fun merge(element: Element, readerCtx: ReaderContext): String? {
            val method = element.getAttribute(METHOD_ATTRIBUTE)
            if (StringUtils.hasText(method)) {
                return method.trim { it <= ' ' }
            }
            if (StringUtils.hasText(this.method)) {
                return this.method
            }
            readerCtx.error("No method specified for " + element.nodeName, element)
            return null
        }

        init {
            val defaultCache = root.getAttribute("cache")
            key = root.getAttribute("key")
            keyGenerator = root.getAttribute("key-generator")
            cacheManager = root.getAttribute("cache-manager")
            condition = root.getAttribute("condition")
            method = root.getAttribute(METHOD_ATTRIBUTE)
            if (StringUtils.hasText(defaultCache)) {
                caches = StringUtils.commaDelimitedListToStringArray(defaultCache.trim { it <= ' ' })
            }
        }
    }
}