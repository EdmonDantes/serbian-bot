package ru.loginov.simple.permissions.spring.data.entity

import io.github.edmondantes.simple.permissions.data.entity.PermissionGroup
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PermissionGroupEntity : PermissionGroup {

    constructor() : super()
    constructor(name: String, rootNodeId: Int) {
        this.name = name
        this.rootNodeId = rootNodeId
    }

    @Id
    override var name: String = ""

    @Column(nullable = false, unique = true)
    override var rootNodeId: Int = 0
}