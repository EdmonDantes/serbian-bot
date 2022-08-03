package ru.loginov.simple.permissions.scanner

import ru.loginov.simple.permissions.annotation.RequiredPermission
import java.lang.reflect.Method
import java.lang.reflect.Modifier

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
            ?.let { isIgnored || ignoredMethods.contains(it) || !Modifier.isPublic(method.modifiers) }
            ?: parents.any { it.checkMethodIsIgnored(method) }

    override fun getMethodPermissions(method: Method): List<String>? {
        val currentMethod = hasMethod(method)
        val parentPermission = parents.mapNotNull { it.getMethodPermissions(method) }.flatten().ifEmpty { null }
        val parentAndDefaultPermissions = parentPermission?.let {
            if (defaultPermissions == null) it else it.plus(
                    defaultPermissions!!
            )
        }
                ?: defaultPermissions

        if (currentMethod == null) {
            return parentAndDefaultPermissions
        } else if (isIgnored || ignoredMethods.contains(currentMethod) || !Modifier.isPublic(method.modifiers)) {
            return null
        }

        val result: MutableList<String> = parentAndDefaultPermissions?.let { ArrayList(it) } ?: ArrayList()

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
            clazz.getDeclaredMethod(method.name, *method.parameterTypes)
        } catch (e: Exception) {
            null
        }
    }

    protected fun getPermissionsFromAnnotations(annotations: List<Annotation>): List<String>? {
        return annotations
                .filterIsInstance<RequiredPermission>()
                .ifEmpty { null }
                ?.map { it.permission }
    }

    protected abstract fun getMethodAnnotations(method: Method): List<Annotation>
}