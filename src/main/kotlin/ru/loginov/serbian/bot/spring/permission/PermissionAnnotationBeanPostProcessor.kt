package ru.loginov.serbian.bot.spring.permission

import com.fasterxml.jackson.module.kotlin.isKotlinClass
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.annotation.PermissionCheck
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.permission.annotation.scanner.FullyPermissionConditionalScanner
import ru.loginov.serbian.bot.spring.permission.annotation.scanner.PermissionConditionalScanner
import ru.loginov.serbian.bot.util.tryToGetJavaMethods
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.functions

@Component
class PermissionAnnotationBeanPostProcessor : BeanPostProcessor {
    private val beanScanners = HashMap<String, PermissionConditionalScanner>()
    private val beanClasses = HashMap<String, Class<*>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {

        val clazz = bean.javaClass
        val classAnnotations: List<Annotation> =
                if (clazz.isKotlinClass()) {
                    clazz.kotlin.annotations
                } else {
                    clazz.annotations.toList()
                }

        if (classAnnotations.find { it is PermissionCheck || it is RequiredPermission } != null) {
            val scanner = FullyPermissionConditionalScanner(bean.javaClass)
            if (!scanner.isIgnored) {
                beanScanners[beanName] = scanner
                beanClasses[beanName] = bean.javaClass
            }
        }

        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val clazz = beanClasses[beanName] ?: return bean
        val scanner = beanScanners[beanName] ?: return bean

        val beanMethodsConditionals = createConditionals(clazz, scanner)
        if (beanMethodsConditionals.isEmpty()) {
            return bean
        }

        val proxy = Proxy.newProxyInstance(
                Thread.currentThread().contextClassLoader,
                getAllInterfaces(clazz).toTypedArray(),
                ProxyPermissionChecker(
                        bean,
                        clazz,
                        beanName,
                        beanMethodsConditionals
                )
        )

        return proxy ?: bean
    }

    private fun getAllInterfaces(clazz: Class<*>): List<Class<*>> {
        val result = ArrayList<Class<*>>()
        var currentClass = clazz
        while (currentClass != Any::class.java) {
            result.addAll(currentClass.interfaces)
            currentClass = currentClass.superclass
        }
        return result
    }

    private fun createConditionals(clazz: Class<*>, scanner: PermissionConditionalScanner): Map<Method, List<String>> {
        val result = HashMap<Method, List<String>>()
        if (clazz.isKotlinClass()) {
            clazz.kotlin.functions.forEach { member ->
                member.tryToGetJavaMethods().forEach { method ->
                    val permissions = scanner.getMethodPermissions(method)
                    if (permissions != null) {
                        result[method] = permissions
                    }
                }
            }
        } else {
            clazz.methods.forEach { method ->
                val permissions = scanner.getMethodPermissions(method)
                if (permissions != null) {
                    result[method] = permissions
                }
            }
        }

        return result
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PermissionAnnotationBeanPostProcessor::class.java)
    }
}