package ru.loginov.serbian.bot.spring.permission.annotation.scanner

import com.fasterxml.jackson.module.kotlin.isKotlinClass
import java.lang.reflect.Method

class IgnoreScannerPart(clazz: Class<*>) : AbstractPermissionConditionalScanner(clazz, emptyList()) {
    override val defaultPermission: List<String>? = null
    override val isIgnored: Boolean = true
    override val ignoredMethods: List<Method> = if (clazz.isKotlinClass()) clazz.kotlin.getAllJavaMethod() else clazz.methods.toList()

    override fun getMethodPermissions(method: Method): List<String>? = null

    override fun getMethodAnnotations(method: Method): List<Annotation> = emptyList()
}