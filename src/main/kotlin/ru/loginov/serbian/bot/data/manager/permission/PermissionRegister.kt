package ru.loginov.serbian.bot.data.manager.permission

interface PermissionRegister {

    fun getAllRegisteredPermissions(): List<String>
    fun hasPermission(permission: String): Boolean
    fun registerPermission(permission: String)

}