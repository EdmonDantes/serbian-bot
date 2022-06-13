package ru.loginov.simple.permissions.storage

interface PermissionStorage {

    /**
     * All permissions
     */
    val permissions: Collection<String>

    /**
     * Check if manager has permission
     * @return True if found, else false
     */
    fun hasPermission(permission: String): Boolean


}