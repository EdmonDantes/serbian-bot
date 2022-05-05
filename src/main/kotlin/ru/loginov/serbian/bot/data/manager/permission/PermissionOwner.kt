package ru.loginov.serbian.bot.data.manager.permission

interface PermissionOwner {
    fun havePermission(permission: String) : Boolean
}