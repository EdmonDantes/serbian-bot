package ru.loginov.serbian.bot.spring.permission.annotation.scanner

import com.fasterxml.jackson.module.kotlin.isKotlinClass
import ru.loginov.serbian.bot.spring.permission.annotation.IgnoreAllPermissionCheck
import ru.loginov.serbian.bot.spring.permission.annotation.IgnorePermissionCheck
import ru.loginov.serbian.bot.spring.permission.annotation.PermissionCheck
import ru.loginov.serbian.bot.spring.permission.annotation.RequiredPermission
import ru.loginov.serbian.bot.util.tryToGetJavaMethods
import java.lang.reflect.Method
import kotlin.reflect.KCallable
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMembers

class FullyPermissionConditionalScanner(clazz: Class<*>) : PermissionConditionalScanner {

    private val root: PermissionConditionalScanner = constructPart(clazz)

    override val defaultPermission: List<String>? = root.defaultPermission
    override val isIgnored: Boolean = root.isIgnored

    override fun checkMethodIsIgnored(method: Method): Boolean = root.checkMethodIsIgnored(method)


    override fun getMethodPermissions(method: Method): List<String>? = root.getMethodPermissions(method)

    private fun constructPart(clazz: Class<*>): PermissionConditionalScanner = PREDEFINED_SCANNER_PART[clazz]
            ?: InnerPermissionConditionalScannerPart(
                    (clazz.superclass?.let { listOf(it) } ?: emptyList())
                            .plus(clazz.interfaces)
                            .map { constructPart(it) },
                    clazz
            )

    private class InnerPermissionConditionalScannerPart(
            parents: List<PermissionConditionalScanner>,
            clazz: Class<*>
    ) : AbstractPermissionConditionalScanner(clazz, parents) {
        override val defaultPermission: List<String>?
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

            val ignoreAnnotation: IgnoreAllPermissionCheck? = classAnnotations.find { it is IgnoreAllPermissionCheck }?.let { it as IgnoreAllPermissionCheck }

            // Class will be ignored if it has IgnoreAllPermissionCheck annotation or if it hasn't annotations PermissionCheck or RequiredPermission
            isIgnored = ignoreAnnotation?.all
                    ?: (classAnnotations.find { it is PermissionCheck || it is RequiredPermission } == null)

            if (isIgnored) { // Skip process if ignored
                defaultPermission = null
                ignoredMethods = clazz.declaredMethods.toList()
                kotlinMembers = emptyMap()
            } else {

                if (isKotlin) {
                    clazz.kotlin.declaredFunctions.forEach { callable ->
                        val methodIsIgnored = (callable.annotations.find { it is IgnorePermissionCheck } != null)
                        val methodIsRequiredCheck = (callable.annotations.find { it is PermissionCheck || it is RequiredPermission } != null)
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
                        val methodIsRequiredCheck = (method.annotations.find { it is PermissionCheck || it is RequiredPermission } != null)
                        val methodIsIgnoredInParent = parents.any { parent -> parent.checkMethodIsIgnored(method) }
                        members[method] = (methodIsIgnored || !methodIsRequiredCheck && methodIsIgnoredInParent) to null
                    }
                }

                defaultPermission = getPermissionsFromAnnotations(classAnnotations)


                //Process IgnorePermissionCheckFor annotation
                ignoreAnnotation?.memberNames?.forEach { memberName ->
                    if (isKotlin)
                        clazz.kotlin.declaredMembers
                                .filter { it.name == memberName }
                                .forEach { callable ->
                                    callable.tryToGetJavaMethods().forEach { method ->
                                        members[method] = false to callable
                                    }
                                }
                    else
                        clazz.declaredMethods.filter { it.name == memberName }.forEach { method ->
                            members.compute(method) { _, pair -> false to pair?.second }
                        }
                }

                ignoredMethods = members.filter { it.value.first }.map { it.key }
                kotlinMembers = members.filter { it.value.second != null }.mapValues { it.value.second!! }
            }
        }

        override fun getMethodAnnotations(method: Method): List<Annotation> = kotlinMembers[method]?.annotations
                ?: method.annotations.toList()
    }

    companion object {
        private val PREDEFINED_SCANNER_PART = listOf(
                Any::class.java
        ).associateWith { IgnoreScannerPart(it) }
    }
}