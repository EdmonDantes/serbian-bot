package ru.loginov.simple.permissions.entity

open class PermissionGroup(open var name: String? = null, open var rootNodeId: Int? = null) {
    override fun toString(): String {
        return "PermissionGroup(name=$name, rootNodeId=$rootNodeId)"
    }
}