package ru.loginov.simple.permissions.scanner

import java.lang.reflect.Method

class IgnoreScannerPart(clazz: Class<*>) : AbstractPermissionConditionalScanner(clazz, emptyList()) {
    override val defaultPermissions: List<String>? = null
    override val isIgnored: Boolean = true
    override val ignoredMethods: List<Method> = emptyList()

    override fun checkMethodIsIgnored(method: Method): Boolean = hasMethod(method) != null

    override fun getMethodPermissions(method: Method): List<String>? = null

    override fun getMethodAnnotations(method: Method): List<Annotation> = emptyList()
}