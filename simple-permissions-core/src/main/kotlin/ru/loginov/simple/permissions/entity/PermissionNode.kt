package ru.loginov.simple.permissions.entity

open class PermissionNode(
        open var id: Int? = null,
        open var value: String? = null,
        open var excluded: Boolean? = null,
        open var parentId: Int? = null,
        open var childrenIds: List<Int>? = null
) {
    override fun toString(): String {
        return "PermissionNode(id=$id, value=$value, excluded=$excluded, parentId=$parentId, childrenIds=$childrenIds)"
    }
}