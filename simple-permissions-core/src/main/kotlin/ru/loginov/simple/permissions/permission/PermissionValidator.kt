package ru.loginov.simple.permissions.permission

/**
 * This object helps to check if the permission has valid format
 */
interface PermissionValidator {

    /**
     * Checks if the [permission] has valid format
     *
     * Permission string is valid if contains only latin lowercase and uppercase alphabet
     * and '*' isn't used or is last part
     *
     * Examples:
     *
     * - Valid:
     *     - `a.b.c`
     *     - `a.b.c.*`
     * - Not valid:
     *     - `a.b.c.`
     *     - `a.*.b.c`
     *     - `12.b.c`
     *     - `a.b.c.*.`
     *     - `a.b.c*`
     */
    fun validate(permission: String): Boolean
}