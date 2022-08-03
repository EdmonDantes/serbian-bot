package ru.loginov.simple.permissions.spring.data.entity

import io.github.edmondantes.simple.permissions.data.entity.PermissionNode
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.OneToMany
import javax.persistence.Transient

@Entity
@NamedEntityGraphs(
        NamedEntityGraph(
                name = "with_children", attributeNodes = [
            NamedAttributeNode(value = "id"),
            NamedAttributeNode(value = "value"),
            NamedAttributeNode(value = "_excluded"),
            NamedAttributeNode(value = "parentId"),
            NamedAttributeNode(value = "children")
        ]
        )
)
class PermissionNodeEntity : PermissionNode {

    constructor() : super()

    constructor(
            id: Int? = null,
            value: String? = null,
            excluded: Boolean? = null,
            parentId: Int? = null,
            children: List<PermissionNodeEntity>? = null,
    ) : super() {
        this.id = id ?: -1
        this.value = value
        this._excluded = excluded
        this.parentId = parentId
        this.children = children
    }

    constructor(node: PermissionNode) : this(
            node.id,
            node.value,
            node.excluded,
            node.parentId,
            node.childrenIds.map { PermissionNodeEntity(it) }
    )

    @Id
    @GeneratedValue
    override var id: Int = -1

    override var value: String? = null

    @Column(name = "excluded", nullable = false)
    var _excluded: Boolean? = null

    @get:Transient
    override val excluded: Boolean
        get() = _excluded ?: error("This permission node is not yet saved or loaded in repo")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId", insertable = false, updatable = false)
    var parent: PermissionNodeEntity? = null

    @Column(name = "parentId")
    override var parentId: Int? = null

    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY)
    var children: List<PermissionNodeEntity>? = null
        set(value) {
            field = value;
            if (value != null) {
                childrenIds = value.map { it.id }
            }
        }

    @Transient
    override var childrenIds: List<Int> = emptyList()
}