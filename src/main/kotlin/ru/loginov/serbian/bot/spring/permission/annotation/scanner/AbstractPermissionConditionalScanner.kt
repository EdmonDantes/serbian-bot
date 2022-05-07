package ru.loginov.serbian.bot.spring.permission.annotation.scanner

import org.springframework.util.ClassUtils.hasMethod
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.util.tryToGetJavaMethods
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMembers

abstract class AbstractPermissionConditionalScanner(
        protected val clazz: Class<*>,
        protected val parents: List<PermissionConditionalScanner>
) : PermissionConditionalScanner {

    protected abstract val ignoredMethods: List<Method>

    /**
     * Should ignore method if class is ignored, have ignoring annotations, or it is not public method
     * Else check method in parents
     */
    override fun checkMethodIsIgnored(method: Method): Boolean = hasMethod(method)
            ?.let { isIgnored || ignoredMethods.contains(it) || !Modifier.isPublic(it.modifiers) }
            ?: parents.any { it.checkMethodIsIgnored(method) }

    //FIXME: Mark equals, toString and another method is ignored
    //FIXME: Create architecture for ignored strategy
    override fun getMethodPermissions(method: Method): List<String>? {
        val currentMethod = hasMethod(method)
        if (currentMethod == null) {
            return parents.firstNotNullOfOrNull { it.getMethodPermissions(method) }
        } else if (isIgnored || ignoredMethods.contains(method) || !Modifier.isPublic(method.modifiers)) {
            return null
        }

        val result = if (defaultPermission == null) ArrayList<String>() else ArrayList(defaultPermission)

        getPermissionsFromAnnotations(getMethodAnnotations(currentMethod))?.also {
            result.addAll(it)
        }

        parents.forEach {
            it.getMethodPermissions(currentMethod)?.also {
                result.addAll(it)
            }
        }

        return result
    }

    protected fun hasMethod(method: Method): Method? {
        return try {
            clazz.getMethod(method.name, *method.parameterTypes)
        } catch (e: Exception) {
            null
        }
    }

    protected fun getPermissionsFromAnnotations(annotations: List<Annotation>): List<String>? {
        return annotations
                .filter { it is RequiredPermission }
                .ifEmpty { null }
                ?.map { (it as RequiredPermission).permission }
    }

    protected fun KClass<*>.getAllJavaMethod(): List<Method> = this.declaredMembers.flatMap { it.tryToGetJavaMethods() }

    protected abstract fun getMethodAnnotations(method: Method): List<Annotation>
}