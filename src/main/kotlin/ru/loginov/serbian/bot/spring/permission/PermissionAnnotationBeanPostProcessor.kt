package ru.loginov.serbian.bot.spring.permission

import com.fasterxml.jackson.module.kotlin.isKotlinClass
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.spring.permission.annotation.PermissionCheck
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.spring.permission.annotation.scanner.FullyPermissionConditionalScanner
import ru.loginov.serbian.bot.spring.permission.annotation.scanner.PermissionConditionalScanner
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaMethod

@Component
class PermissionAnnotationBeanPostProcessor : BeanPostProcessor {
    private val shouldBeHandled = HashMap<String, PermissionConditionalScanner>()
    private val beanClasses = HashMap<String, Class<*>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val scanner = FullyPermissionConditionalScanner(bean.javaClass)
        if (!scanner.isIgnored) {
            shouldBeHandled[beanName] = scanner
            beanClasses[beanName] = bean.javaClass
        }

        if (bean.javaClass.annotations.find { it is PermissionCheck || it is RequiredPermission } == null) {
            return bean
        }

//        val map = HashMap<Method, PermissionConditionals>()
//
//        val default = transformAnnotationsToConditional(
//                if (bean.javaClass.isKotlinClass())
//                    bean.javaClass.kotlin.annotations
//                else
//                    bean.javaClass.annotations.toList()
//        )
//
//        if (default?.group != null && default.permissions != null) {
//            error(
//                    "You can not require permissions group and permission in one time. " +
//                            "Found @RequiredGroup and @RequiredPermission like default in bean with name '$beanName'"
//            )
//        }
//
//        if (bean.javaClass.isKotlinClass()) {
//            prepareDataForKotlinClass(bean.javaClass.kotlin, default, map, beanName)
//        } else {
//            prepareDataForJavaClass(bean.javaClass, default, map, beanName)
//        }
//        if (map.isNotEmpty()) {
//            shouldBeHandled[beanName] = map
//            beanClasses[beanName] = bean.javaClass
//        }

        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val beanMethodsConditionals = shouldBeHandled[beanName] ?: return bean
        val clazz = beanClasses[beanName] ?: return bean

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

    private fun prepareDataForJavaClass(
            clazz: Class<*>,
            default: PermissionConditionals?,
            map: MutableMap<Method, PermissionConditionals>,
            beanName: String
    ) {
        prepareDataForMethods(clazz.methods.toList(), beanName, default, map)
    }

    private fun prepareDataForKotlinClass(
            clazz: KClass<*>,
            default: PermissionConditionals?,
            map: MutableMap<Method, PermissionConditionals>,
            beanName: String
    ) {
        prepareDataForMethods(clazz.memberFunctions.mapNotNull { it.javaMethod }, beanName, default, map)
    }

    fun prepareDataForMethods(
            methods: List<Method>,
            beanName: String,
            default: PermissionConditionals?,
            map: MutableMap<Method, PermissionConditionals>
    ) {

        for (it in methods.minus(IGNORED_METHOD)) {
            val conditionals = transformAnnotationsToConditional(it.annotations.toList()) ?: default ?: continue

            if (conditionals.group != null && conditionals.permissions != null) {
                error(
                        "Can not required permissions group and permission in one time. " +
                                "Found @RequiredGroup and @RequiredPermission in method with name '${it.name}' in bean with name '$beanName'"
                )
            }

            if (it.parameterTypes.any { PermissionContext::class.java.isAssignableFrom(it) }) {
                map[it] = conditionals
            } else {
                LOGGER.info(
                        "Can not enable permission check for method '${it.name}' in bean with name '$beanName'. " +
                                "One of argument should implements 'PermissionContext'."
                )
            }
        }
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



    companion object {
        private val LOGGER = LoggerFactory.getLogger(PermissionAnnotationBeanPostProcessor::class.java)
        private val IGNORED_METHOD = listOf(Any::equals, Any::hashCode, Any::toString).mapNotNull { it.javaMethod }
    }
}