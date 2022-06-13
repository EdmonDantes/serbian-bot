package ru.loginov.simple.permissions.scanner

import com.fasterxml.jackson.module.kotlin.isKotlinClass
import ru.loginov.simple.permissions.annotation.ForcePermissionCheck
import ru.loginov.simple.permissions.annotation.IgnorePermissionCheck
import ru.loginov.simple.permissions.annotation.RequiredPermission
import ru.loginov.simple.permissions.util.tryToGetJavaMethods
import ru.loginov.spring.simple.permissions.scanner.PermissionConditionalScanner
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KCallable
import kotlin.reflect.full.declaredFunctions

class FullyPermissionConditionalScanner(clazz: Class<*>) : PermissionConditionalScanner {

    private val root: PermissionConditionalScanner = constructPart(clazz)

    override val defaultPermissions: List<String>? = root.defaultPermissions
    override val isIgnored: Boolean = root.isIgnored

    override fun checkMethodIsIgnored(method: Method): Boolean = root.checkMethodIsIgnored(method)


    override fun getMethodPermissions(method: Method): List<String>? = root.getMethodPermissions(method)

    private fun constructPart(clazz: Class<*>): PermissionConditionalScanner {
        val scanner = ALL_SCANNERS[clazz]
        if (scanner != null) {
            return scanner
        }

        val newScanner = InnerPermissionConditionalScannerPart(
                clazz.interfaces.plus(clazz.superclass).filterNotNull().map { constructPart(it) },
                clazz
        )

        ALL_SCANNERS[clazz] = newScanner
        return newScanner
    }

    private class InnerPermissionConditionalScannerPart(
            parents: List<PermissionConditionalScanner>,
            clazz: Class<*>
    ) : AbstractPermissionConditionalScanner(clazz, parents) {
        override val defaultPermissions: List<String>?
        override val isIgnored: Boolean
        override val ignoredMethods: List<Method>

        private val kotlinMembers: Map<Method, KCallable<*>>

        init {
            val members = HashMap<Method, Pair<Boolean, KCallable<*>?>>()
            val isKotlin = clazz.isKotlinClass()

            val classAnnotations: List<Annotation> =
                    if (isKotlin) {
                        clazz.kotlin.annotations
                    } else {
                        clazz.annotations.toList()
                    }

            // Class will process only if it has PermissionCheck or RequiredPermission annotation
            val requiredPermissionAnnotations = classAnnotations.filterIsInstance<RequiredPermission>()
            val permissionCheckAnnotation = classAnnotations.find { it is ForcePermissionCheck } as? ForcePermissionCheck

            isIgnored = requiredPermissionAnnotations.isEmpty()
                    && (permissionCheckAnnotation == null || !permissionCheckAnnotation.enabled)

            if (isIgnored) { // Skip process if ignored
                defaultPermissions = null
                ignoredMethods = clazz.declaredMethods.toList()
                kotlinMembers = emptyMap()
            } else {
                if (isKotlin) {
                    clazz.kotlin.declaredFunctions.forEach { callable ->
                        val methodIsIgnored = (callable.annotations.find { it is IgnorePermissionCheck } != null)
                        val methodIsRequiredCheck = (callable.annotations.find { it is ForcePermissionCheck || it is RequiredPermission } != null)
                        callable.tryToGetJavaMethods().forEach { method ->
                            val methodIsIgnoredInParent = parents.any { parent -> parent.checkMethodIsIgnored(method) }
                            // Method will be ignored if it has IgnorePermissionCheck annotations or
                            // if it hasn't PermissionCheck or RequiredPermission annotation, and it was ignored in parent
                            members[method] = (methodIsIgnored || !methodIsRequiredCheck && methodIsIgnoredInParent) to callable
                        }
                    }
                } else {
                    clazz.declaredMethods.forEach { method ->
                        val methodIsIgnored = (method.annotations.find { it is IgnorePermissionCheck } != null)
                        val methodIsRequiredCheck = (method.annotations.find { it is ForcePermissionCheck || it is RequiredPermission } != null)
                        val methodIsIgnoredInParent = parents.any { parent -> parent.checkMethodIsIgnored(method) }
                        members[method] = (methodIsIgnored || !methodIsRequiredCheck && methodIsIgnoredInParent) to null
                    }
                }

                defaultPermissions = getPermissionsFromAnnotations(requiredPermissionAnnotations)

                ignoredMethods = members.filter { it.value.first }.map { it.key }
                kotlinMembers = members.filter { it.value.second != null }.mapValues { it.value.second!! }
            }
        }

        override fun getMethodAnnotations(method: Method): List<Annotation> = kotlinMembers[method]?.annotations
                ?: method.annotations.toList()
    }

    companion object {
        private val PREDEFINED_SCANNER_PART = listOf(Any::class.java)
        private val ALL_SCANNERS = ConcurrentHashMap<Class<*>, PermissionConditionalScanner>()

        init {
            PREDEFINED_SCANNER_PART.forEach { clazz ->
                ALL_SCANNERS[clazz] = IgnoreScannerPart(clazz)
            }
        }
    }
}