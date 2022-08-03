package ru.loginov.simple.permissions.spring

import io.github.edmondantes.simple.permissions.PermissionOwner
import org.slf4j.LoggerFactory
import ru.loginov.simple.permissions.exception.AccessDeniedException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class PermissionCheckerProxyHandler(
        private val bean: Any?,
        private val clazz: Class<*>,
        private val beanName: String,
        private val methodsConditionals: Map<Method, List<String>>,
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

        val currentMethod = try {
            clazz.getMethod(method.name, *method.parameterTypes)
        } catch (e: Exception) {
            return null
        }

        val methodPermissions = methodsConditionals[currentMethod] ?: return method.invokeWithArgs(bean, args)

        val context = args?.filterIsInstance<PermissionOwner>()?.firstOrNull()

        if (context == null) {
            logger.warn(
                    "Can not find PermissionContext in arguments for method with name '${method.name}'" +
                            " in bean with name '$beanName'"
            )
            return method.invokeWithArgs(bean, args)
        }

        val isHavePermissions = try {
            context.checkAllPermission(methodPermissions)
        } catch (e: Exception) {
            throw AccessDeniedException(e)
        }

        if (isHavePermissions) {
            return method.invokeWithArgs(bean, args)
        } else {
            throw AccessDeniedException()
        }
    }

    private fun <T> Method.invokeWithArgs(obj: T, args: Array<out Any>?): Any? = if (args == null) {
        invoke(obj)
    } else {
        invoke(obj, *args)
    }
}