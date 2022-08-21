package ru.loginov.serbian.bot.data.dto.product

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import ru.loginov.serbian.bot.data.dto.WithId
import ru.loginov.serbian.bot.data.dto.category.CategoryDescription
import ru.loginov.serbian.bot.data.dto.localization.prepareLocalization
import java.util.Collections
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapKey
import javax.persistence.OneToMany

//TODO: Add photo to model
@Entity
class ProductDescription : WithId {

    constructor() : this(null, null, Collections.emptyMap())

    constructor(
            id: Int? = null,
            categoryId: Int? = null,
            localization: Map<String, ProductLocalizedName> = Collections.emptyMap()
    ) {
        this.id = id
        this.categoryId = categoryId
        this.localization = prepareLocalization(localization)
    }

    @Id
    @GeneratedValue
    override var id: Int? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    var category: CategoryDescription? = null

    @Column(name = "category_id", nullable = false)
    var categoryId: Int? = null

    @JsonIgnore
    @MapKey(name = "localizedId.locale")
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var localization: Map<String, ProductLocalizedName>

    companion object {
        fun createWith(
                id: Int? = null,
                categoryDescription: CategoryDescription? = null,
                localization: Map<String, ProductLocalizedName> = Collections.emptyMap()
        ): ProductDescription =
                ProductDescription(
                        id,
                        categoryDescription?.run { id ?: error("Categories id can not be null") },
                        localization
                )

    }
}