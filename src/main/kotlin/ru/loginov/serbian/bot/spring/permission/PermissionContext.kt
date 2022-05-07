package ru.loginov.serbian.bot.spring.permission

import ru.loginov.serbian.bot.spring.permission.exception.NotFoundPermissionException

interface PermissionContext {
    @Throws(NotFoundPermissionException::class)
    fun havePermission(permission: String): Boolean
    fun haveGroup(group: String): Boolean

}