package ru.loginov.serbian.bot.data.dto.place

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MapKey
import javax.persistence.OneToMany

@Entity
class CountryDescription {

    constructor() {}

    constructor(id: Int?) {
        this.id = id
    }

    @Id
    @GeneratedValue
    var id: Int? = null

    @MapKey(name = "localizedId.locale")
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var localization: MutableMap<String, CountryLocalizedName> = HashMap()

}