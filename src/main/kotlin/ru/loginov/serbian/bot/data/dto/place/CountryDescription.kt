package ru.loginov.serbian.bot.data.dto.place

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
import javax.persistence.MapKey
import javax.persistence.OneToMany

@Entity
class CountryDescription : WithId {

    constructor() : this(id = null)
    constructor(
            id: Int? = null,
            name: String? = null,
            localization: Map<String, CountryLocalizedName> = Collections.emptyMap()
    ) {
        this.localization = prepareLocalization(localization)
        this.name = name
        this.id = id
    }

    @Id
    @GeneratedValue
    override var id: Int? = null

    @Column(nullable = false, unique = true)
    var name: String? = null

    @JsonIgnore
    @MapKey(name = "localizedId.locale")
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    @OneToMany(mappedBy = "entity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var localization: Map<String, CountryLocalizedName>
}