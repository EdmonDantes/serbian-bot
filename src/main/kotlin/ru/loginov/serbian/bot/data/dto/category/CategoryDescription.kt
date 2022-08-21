package ru.loginov.serbian.bot.data.dto.category

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import ru.loginov.serbian.bot.data.dto.WithId
import ru.loginov.serbian.bot.data.dto.localization.prepareLocalization
import java.util.Collections
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapKey
import javax.persistence.OneToMany

@Entity
class CategoryDescription : WithId {

    constructor() : this(id = null)

    constructor(
            id: Int? = null,
            parent: CategoryDescription? = null,
            children: List<CategoryDescription> = Collections.emptyList(),
            localization: Map<String, CategoryLocalizedName> = Collections.emptyMap()
    ) {
        this.localization = prepareLocalization(localization)
        this.children = children
        this.parentId = parent?.id
        this.parent = parent
        this.id = id

        this.children.forEach {
            it.parent = this
            it.parentId = id
        }
    }

    @Id
    @GeneratedValue
    override var id: Int? = null

    @JsonIgnore
    @OneToMany(
            mappedBy = "parent",
            cascade = [CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH],
            orphanRemoval = true
    )
    var children: List<CategoryDescription>

    @JsonIgnore
    @ManyToOne(cascade = [CascadeType.REFRESH], optional = true)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    var parent: CategoryDescription? = null

    @Column(name = "parent_id")
    var parentId: Int? = null

    @JsonIgnore
    @MapKey(name = "localizedId.locale")
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var localization: Map<String, CategoryLocalizedName>
}