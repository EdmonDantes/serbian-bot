package ru.loginov.serbian.bot.data.manager.permission

import ru.loginov.serbian.bot.data.dto.permission.GroupPermissionDto
import ru.loginov.serbian.bot.data.dto.user.UserDto

interface PermissionManager {

    fun getPermissionsForGroup(name: String): PermissionOwner?
    fun getPermissionsForUser(user: UserDto): PermissionOwner?

    fun addPermissionForGroup(name: String, permission: String): Boolean
    fun deletePermissionForGroup(name: String, permission: String): Boolean

    fun createGroup(name: String): Boolean
    fun deleteGroup(name: String): Boolean

    fun getAllGroups(): List<GroupPermissionDto>
    fun getAllPermissions(): List<String>
}