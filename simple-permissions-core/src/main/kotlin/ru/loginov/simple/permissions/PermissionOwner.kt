package ru.loginov.simple.permissions

import ru.loginov.simple.permissions.exception.NotValidPermissionFormatException

/**
 * Class for checking permission for object
 */
interface PermissionOwner {

    /**
     * Check [permission] for owner
     * @return True if owner are granted the [permission], else false
     */
    @Throws(NotValidPermissionFormatException::class)
    fun checkPermission(permission: String): Boolean

    /**
     * Check [permissions] for owner
     * @return True if owner are granted all permissions in [permissions], else false
     */
    @Throws(NotValidPermissionFormatException::class)
    fun checkAllPermission(permissions: List<String>): Boolean

    companion object {
        /**
         * [PermissionOwner] which has all permission
         */
        val ALL_PERMISSION = object : PermissionOwner {
            override fun checkPermission(permission: String): Boolean = true
            override fun checkAllPermission(permissions: List<String>): Boolean = true
        }

        /**
         * [PermissionOwner] which hasn't any permission
         */
        val NO_PERMISSION = object : PermissionOwner {
            override fun checkPermission(permission: String): Boolean = false
            override fun checkAllPermission(permissions: List<String>): Boolean = false
        }
    }
}