package ru.loginov.serbian.bot.data.manager.permission

/**
 * Class for checking permission for object
 */
interface PermissionOwner {

    /**
     * Check [permission] for owner
     * @return True if owner has the [permission], else false
     */
    fun havePermission(permission: String): Boolean

    companion object {
        /**
         * [PermissionOwner] which has all permission
         */
        val ALL_PERMISSION = object : PermissionOwner {
            override fun havePermission(permission: String): Boolean = true
        }

        /**
         * [PermissionOwner] which hasn't any permission
         */
        val NO_PERMISSION = object : PermissionOwner {
            override fun havePermission(permission: String): Boolean = false
        }
    }
}