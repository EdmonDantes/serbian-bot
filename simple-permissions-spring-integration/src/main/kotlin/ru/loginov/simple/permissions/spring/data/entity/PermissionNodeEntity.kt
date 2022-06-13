package ru.loginov.simple.permissions.spring.data.entity

import ru.loginov.simple.permissions.entity.PermissionNode
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
            NamedAttributeNode(value = "excluded"),
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
            children: List<PermissionNodeEntity> = emptyList(),
    ) : super() {
        this.id = id
        this.value = value
        this.excluded = excluded
        this.parentId = parentId
        this.children = children
    }

    constructor(node: PermissionNode) : this(
            node.id,
            node.value,
            node.excluded,
            node.parentId,
            node.childrenIds?.map { PermissionNodeEntity(it) } ?: emptyList()
    )

    @Id
    @GeneratedValue
    override var id: Int? = null

    override var value: String? = null
    override var excluded: Boolean? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId", insertable = false, updatable = false)
    var parent: PermissionNodeEntity? = null

    @Column(name = "parentId")
    override var parentId: Int? = null

    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY)
    var children: List<PermissionNodeEntity> = emptyList()
        set(value) {
            field = value; childrenIds = value.mapNotNull { it.id }
        }

    @Transient
    override var childrenIds: List<Int>? = emptyList()
}