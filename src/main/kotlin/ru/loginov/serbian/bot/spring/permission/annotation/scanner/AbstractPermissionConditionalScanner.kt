package ru.loginov.serbian.bot.spring.permission.annotation.scanner

import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.jvm.javaMethod

abstract class AbstractPermissionConditionalScanner(
        protected val clazz: Class<*>,
        protected val parents: List<PermissionConditionalScanner>
) : PermissionConditionalScanner {

    protected abstract val ignoredMethods: List<Method>

    /**
     * Should ignore method if class is ignored, have ignoring annotations, or it is not public method
     * Else check method in parents
     */
    override fun checkMethodIsIgnored(method: Method): Boolean =
            if (clazz.methods.contains(method)) {
                isIgnored || ignoredMethods.contains(method) || !Modifier.isPublic(method.modifiers)
            } else {
                parents.any { it.checkMethodIsIgnored(method) }
            }

    override fun getMethodPermissions(method: Method): List<String>? {
        if (checkMethodIsIgnored(method)) {
            return null
        }

        if (!clazz.methods.contains(method)) {
            return parents.firstNotNullOfOrNull { it.getMethodPermissions(method) }
        }

        return getPermissionsFromAnnotations(getMethodAnnotations(method))
    }

    protected fun getPermissionsFromAnnotations(annotations: List<Annotation>): List<String>? {
        return annotations
                .filter { it is RequiredPermission }
                .ifEmpty { null }
                ?.map { (it as RequiredPermission).permission }
    }

    protected fun KCallable<*>.tryToGetJavaMethods(): List<Method> = when (this) {
        is KFunction<*> -> listOf(javaMethod)
        is KMutableProperty<*> -> listOf(getter.javaMethod, setter.javaMethod)
        is KProperty<*> -> listOf(getter.javaMethod)
        else -> emptyList()
    }.mapNotNull { it }

    protected fun KClass<*>.getAllJavaMethod() : List<Method> = this.declaredMembers.flatMap { it.tryToGetJavaMethods() }

    protected abstract fun getMethodAnnotations(method: Method): List<Annotation>
}