package ru.loginov.serbian.bot.data.manager.permission

interface PermissionRegister {

    fun getAllRegisteredPermissions(): List<String>
    fun registerPermission(permission: String)

}