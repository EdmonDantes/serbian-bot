package ru.loginov.simple.permissions.tree

import ru.loginov.simple.permissions.PermissionOwner
import ru.loginov.simple.permissions.exception.NotValidPermissionFormatException

/**
 * Help to mutate specify permission owner.
 *
 * Not mutable
 */
interface PermissionTree : PermissionOwner {
    @Throws(NotValidPermissionFormatException::class)
    fun addPermission(permission: String): NodeMutations?

    @Throws(NotValidPermissionFormatException::class)
    fun deletePermission(permission: String): NodeMutations?

}