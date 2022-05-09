package ru.loginov.serbian.bot.spring.permission

import org.slf4j.LoggerFactory
import ru.loginov.serbian.bot.spring.permission.exception.HaveNotPermissionException
import ru.loginov.serbian.bot.spring.permission.exception.NotFoundPermissionException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class ProxyPermissionChecker(
        private val bean: Any?,
        private val clazz: Class<*>,
        private val beanName: String,
        private val methodsConditionals: Map<Method, List<String>>
) : InvocationHandler {

    private val methodNamesForCheck = HashSet(methodsConditionals.map { it.key.name })
    private val logger = LoggerFactory.getLogger(clazz)

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        val methodName = method?.name ?: return null
        if (!methodNamesForCheck.contains(methodName)) {
            return if (args == null) {
                method.invoke(bean)
            } else {
                method.invoke(bean, *args)
            }
        }

        val params = args ?: emptyArray()

        val currentMethod = try {
            clazz.getMethod(method.name, *method.parameterTypes)
        } catch (e: Exception) {
            return null
        }

        val methodPermissions = methodsConditionals[currentMethod] ?: return method.invoke(bean, *params)
        val context = args?.find { it is PermissionContext } as PermissionContext?

        if (context == null) {
            logger.warn(
                    "Can not find PermissionContext in arguments for method with name '${method.name}'" +
                            "in bean with name '$beanName'"
            )
            return method.invoke(bean, *params)
        }

        try {
            if (methodPermissions.all { context.havePermission(it) }) {
                return method.invoke(bean, *params)
            } else {
                throw HaveNotPermissionException()
            }
        } catch (e: NotFoundPermissionException) {
            logger.warn(e.message)
            throw HaveNotPermissionException()
        }
    }
}