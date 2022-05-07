package ru.loginov.serbian.bot.spring.permission.annotation.scanner

import com.fasterxml.jackson.module.kotlin.isKotlinClass
import ru.loginov.serbian.bot.spring.permission.annotation.IgnorePermissionCheck
import ru.loginov.serbian.bot.spring.permission.annotation.IgnorePermissionCheckFor
import ru.loginov.serbian.bot.spring.permission.annotation.PermissionCheck
import java.lang.reflect.Method
import kotlin.reflect.KCallable
import kotlin.reflect.full.declaredMembers

class FullyPermissionConditionalScanner(private val clazz: Class<*>) : PermissionConditionalScanner {

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
            val ignoreAnnotation: IgnorePermissionCheckFor?
            val classAnnotations: List<Annotation>
            val members = HashMap<Method, Pair<Boolean, KCallable<*>?>>()
            val isKotlin = clazz.isKotlinClass()

            if (isKotlin) {
                ignoreAnnotation = clazz.kotlin.annotations
                        .find { it is IgnorePermissionCheckFor } as IgnorePermissionCheckFor?

                classAnnotations = clazz.kotlin.annotations

                clazz.kotlin.declaredMembers.forEach { callable ->
                    val methodIsIgnored = (callable.annotations.find { it is IgnorePermissionCheck } as? IgnorePermissionCheck)?.enabled == true
                    callable.tryToGetJavaMethods().forEach {
                        members[it] = methodIsIgnored to callable
                    }
                }
            } else {
                ignoreAnnotation = clazz.annotations
                        .find { it is IgnorePermissionCheckFor } as IgnorePermissionCheckFor?

                classAnnotations = clazz.annotations.toList()
                clazz.declaredMethods.forEach { method ->
                    val methodIsIgnored = (method.annotations.find { it is IgnorePermissionCheck } as? IgnorePermissionCheck)?.enabled == true
                    members[method] = methodIsIgnored to null
                }
            }

            defaultPermission = getPermissionsFromAnnotations(classAnnotations)
            isIgnored = ignoreAnnotation?.all
                    ?: if (parents.isNotEmpty())
                        parents.all { it.isIgnored }
                                && classAnnotations.find { it is PermissionCheck } == null
                                && defaultPermission == null
                    else false


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
                    clazz.methods.filter { it.name == memberName }.forEach { method ->
                        members.compute(method) { _, pair -> false to pair?.second }
                    }
            }

            ignoredMethods = members.filter { it.value.first }.map { it.key }
            kotlinMembers = members.filter { it.value.second != null }.mapValues { it.value.second!! }
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