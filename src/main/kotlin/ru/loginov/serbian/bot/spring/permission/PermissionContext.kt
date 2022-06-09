package ru.loginov.serbian.bot.spring.permission

interface PermissionContext {
    fun havePermission(permission: String): Boolean

    fun haveAllPermissions(permissions: List<String>): Boolean
}