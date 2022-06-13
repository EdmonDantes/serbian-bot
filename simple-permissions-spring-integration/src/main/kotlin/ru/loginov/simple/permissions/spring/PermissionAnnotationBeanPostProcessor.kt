package ru.loginov.simple.permissions.spring

import com.fasterxml.jackson.module.kotlin.isKotlinClass
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck
import ru.loginov.simple.permissions.annotation.RequiredPermission
import ru.loginov.simple.permissions.scanner.FullyPermissionConditionalScanner
import ru.loginov.simple.permissions.util.getAllInterfaces
import ru.loginov.simple.permissions.util.getAnyAnnotations
import ru.loginov.simple.permissions.util.tryToGetJavaMethods
import ru.loginov.spring.simple.permissions.scanner.PermissionConditionalScanner
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.full.functions

@Component
class PermissionAnnotationBeanPostProcessor : BeanPostProcessor {

    @Autowired
    private lateinit var permissionRegister: PermissionRegister

    private val beanScanners = HashMap<String, PermissionConditionalScanner>()
    private val beanClasses = HashMap<String, Class<*>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {

        val clazz = bean.javaClass
        val classAnnotations: List<Annotation> = clazz.getAnyAnnotations()

        if (classAnnotations.find { it is ForcePermissionCheck || it is RequiredPermission } != null) {
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

        beanMethodsConditionals.values.forEach {
            it.forEach {
                if (permissionRegister.register(it)) {
                    LOGGER.debug("Registered permission '$it'")
                } else {
                    LOGGER.warn("Can not register permission '$it'")
                }
            }
        }

        val proxy = Proxy.newProxyInstance(
                Thread.currentThread().contextClassLoader,
                clazz.getAllInterfaces().toTypedArray(),
                PermissionCheckerProxyHandler(
                        bean,
                        clazz,
                        beanName,
                        beanMethodsConditionals
                )
        )

        return proxy ?: bean
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