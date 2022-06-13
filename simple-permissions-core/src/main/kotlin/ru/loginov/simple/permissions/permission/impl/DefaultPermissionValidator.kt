package ru.loginov.simple.permissions.permission.impl

import ru.loginov.simple.permissions.permission.PermissionValidator
import ru.loginov.simple.permissions.util.PermissionHelper

/**
 * Default implementation for [PermissionValidator]
 */
class DefaultPermissionValidator : PermissionValidator {
    override fun validate(permission: String): Boolean =
            PermissionHelper.isValidPermission(permission)
}