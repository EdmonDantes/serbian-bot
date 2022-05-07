package ru.loginov.serbian.bot.spring.permission.annotation.scanner

import java.lang.reflect.Method

interface PermissionConditionalScanner {

    val defaultPermission: List<String>?
    val isIgnored: Boolean

    fun checkMethodIsIgnored(method: Method) : Boolean
    fun getMethodPermissions(method: Method) : List<String>?

}