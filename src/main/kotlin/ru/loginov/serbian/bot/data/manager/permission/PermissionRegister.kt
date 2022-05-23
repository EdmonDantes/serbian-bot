package ru.loginov.serbian.bot.data.manager.permission

/**
 * Class for registering all permissions
 */
interface PermissionRegister {

    /**
     * All registered permissions
     */
    val permissions: List<String>

    /**
     * Check if [permission] registered
     * @return True if [permission] is registered, else false
     */
    fun hasPermission(permission: String): Boolean

    /**
     * Register new [permission]
     */
    fun registerPermission(permission: String)

}