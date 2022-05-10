package ru.loginov.serbian.bot.data.manager.permission

import ru.loginov.serbian.bot.data.dto.permission.GroupPermissionDto
import ru.loginov.serbian.bot.data.dto.user.UserDto

interface PermissionManager {

    fun getPermissionsForGroup(name: String): PermissionOwner?
    fun getPermissionsForUser(user: UserDto): PermissionOwner?

    fun addPermissionForGroup(groupName: String, permission: String): Boolean
    fun deletePermissionForGroup(groupName: String, permission: String): Boolean

    fun createGroup(name: String): Boolean
    fun deleteGroup(name: String, forReplace: String?): Boolean

    fun getAllGroups(): List<GroupPermissionDto>
    fun getAllPermissions(): List<String>
}