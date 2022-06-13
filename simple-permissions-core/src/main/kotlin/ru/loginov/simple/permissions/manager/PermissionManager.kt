package ru.loginov.simple.permissions.manager

import ru.loginov.simple.permissions.PermissionOwner
import ru.loginov.simple.permissions.storage.PermissionStorage
import java.util.concurrent.TimeUnit

/**
 * Class for manage permission and groups
 */
interface PermissionManager : PermissionStorage {

    /**
     * All groups
     */
    val groups: List<String>

    /**
     * Default group
     */
    val defaultGroup: String?

    /**
     * Group with max count of permissions
     */
    val adminGroup: String?

    /**
     * Check manager has a group with [name]
     * @return If found - true, else false
     */
    fun hasGroup(name: String): Boolean

    /**
     * Try to get [PermissionOwner] for group with [groupName]
     * @param groupName name of permission group
     * @return If found it return [PermissionOwner] for group with [groupName], else null
     */
    fun getOwnerForGroup(groupName: String): PermissionOwner?

    /**
     * Try to get [PermissionOwner] for group with [groupName], or try to return [PermissionOwner] for default group
     * @param groupName name of permission group
     * @return If found it return [PermissionOwner] for group with [groupName],
     * else if default group is exists return [PermissionOwner] for default group, else null
     */
    fun getOwnerForGroupOrDefault(groupName: String?): PermissionOwner?

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
     * Create new group with [name]
     * @return If created - true, else false
     */
    fun createGroup(name: String): Boolean

    /**
     * Delete group with [name]
     * @return If deleted - true, else false
     */
    fun deleteGroup(name: String, forReplace: String?): Boolean


    fun flush(force: Boolean = true, timeout: Long = 5, unit: TimeUnit = TimeUnit.SECONDS)
}