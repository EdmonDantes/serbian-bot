package ru.loginov.simple.permissions

interface PermissionContext {
    fun hasPermission(permission: String): Boolean
    fun hasAllPermissions(permissions: List<String>): Boolean
}