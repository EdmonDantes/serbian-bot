package ru.loginov.simple.permissions.spring.data.entity

import ru.loginov.simple.permissions.entity.PermissionGroup
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
class PermissionGroupEntity : PermissionGroup {

    constructor() : super()
    constructor(group: PermissionGroup) : this(group.name, group.rootNodeId)
    constructor(name: String?, rootNodeId: Int?) {
        this.name = name
        this.rootNodeId = rootNodeId
    }

    @Id
    override var name: String? = null

    @OneToOne
    @JoinColumn(name = "rootNodeId", insertable = false, updatable = false)
    var rootNode: PermissionNodeEntity? = null

    @Column(name = "rootNodeId")
    override var rootNodeId: Int? = null
}