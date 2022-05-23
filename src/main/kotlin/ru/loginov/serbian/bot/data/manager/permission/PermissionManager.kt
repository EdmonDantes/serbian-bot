package ru.loginov.serbian.bot.data.manager.permission

import ru.loginov.serbian.bot.data.dto.permission.GroupPermissionDto
import ru.loginov.serbian.bot.data.dto.user.UserDto

/**
 * Class for manage permission and groups
 */
interface PermissionManager {

    /**
     * All groups
     */
    val groups: List<GroupPermissionDto>

    /**
     * All permissions
     */
    val permissions: List<String>

    /**
     * Try to get [PermissionOwner] for group with [name]
     * @param name name of permission group
     * @return If found it return [PermissionOwner] for group with [name], else null
     */
    fun getPermissionsForGroup(name: String): PermissionOwner?

    /**
     * Try to get [PermissionOwner] for [user]
     * @return If found it return [PermissionOwner] for [user], else null
     */
    fun getPermissionsForUser(user: UserDto): PermissionOwner?

    /**
     * Check if manager has permission
     * @return True if found, else false
     */
    fun hasPermission(permission: String): Boolean

    /**
     * Add new [permission] for group with [groupName]
     * @return If added - true, else false
     */
    fun addPermissionForGroup(groupName: String, permission: String): Boolean

    /**
     * Delete [permission] from group with [groupName]
     * @return If deleted - true, else false
     */
    fun deletePermissionForGroup(groupName: String, permission: String): Boolean


    /**
     * Check manager has a group with [name]
     * @return If found - true, else false
     */
    fun hasGroup(name: String): Boolean

    /**
     * Create new group with [name]
     * @return If created - true, else false
     */
    fun createGroup(name: String): Boolean

    /**
     * Delete group with [name]
     * @return If deleted - true, else false
     */
    fun deleteGroup(name: String, forReplace: String?): Boolean
}