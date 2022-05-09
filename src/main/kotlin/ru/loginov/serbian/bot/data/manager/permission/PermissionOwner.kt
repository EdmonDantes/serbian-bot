package ru.loginov.serbian.bot.data.manager.permission

interface PermissionOwner {
    fun havePermission(permission: String): Boolean

    companion object {
        val ALL_PERMISSION = object : PermissionOwner {
            override fun havePermission(permission: String): Boolean = true
        }

        val NO_PERMISSION = object : PermissionOwner {
            override fun havePermission(permission: String): Boolean = false
        }
    }
}