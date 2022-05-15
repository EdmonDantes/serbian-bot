package ru.loginov.serbian.bot.spring.localization

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.localization.annotation.Localized
import ru.loginov.serbian.bot.util.getAllInterfaces
import ru.loginov.serbian.bot.util.getAnyAnnotations
import java.lang.reflect.Proxy

@Component
class LocalizedBeanPostProcessor : BeanPostProcessor {

    private val shouldBeProcessed = HashMap<String, Class<*>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val clazz = bean.javaClass
        val annotations = clazz.getAnyAnnotations()
        if (annotations.find { it is Localized } != null) {
            shouldBeProcessed[beanName] = clazz
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val clazz = shouldBeProcessed[beanName] ?: return bean

        val proxy = Proxy.newProxyInstance(
                Thread.currentThread().contextClassLoader,
                clazz.getAllInterfaces().toTypedArray(),
                LocalizedProxyHandler(bean, clazz)
        )

        return proxy ?: bean
    }
}