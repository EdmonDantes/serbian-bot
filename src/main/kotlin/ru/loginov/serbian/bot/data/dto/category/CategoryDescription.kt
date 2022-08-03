package ru.loginov.serbian.bot.data.dto.category

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.util.Locale
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
class CategoryDescription {

    constructor() {}

    constructor(id: Int?) {
        this.id = id
    }

    constructor(parentId: Int?, id: Int?) {
        this.parentId = parentId
        this.id = id
    }

    @Id
    @GeneratedValue
    var id: Int? = null

    @JsonIgnore
    @OneToMany(
            mappedBy = "parent",
            cascade = [CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH],
            orphanRemoval = true
    )
    var subCategories: MutableList<CategoryDescription> = ArrayList()

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
    var localization: MutableMap<String, CategoryLocalizedName> = HashMap()

    fun putLocalization(locale: Locale, value: String) {
        val localeTag = locale.toLanguageTag()
        localization[localeTag] = CategoryLocalizedName(this, localeTag, value)
    }

    fun putLocalization(locale: String, value: String) {
        localization[locale] = CategoryLocalizedName(this, locale, value)
    }
}