package ru.loginov.serbian.bot.data.dto.category

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField
import ru.loginov.serbian.bot.data.dto.localization.LocalizedId
import ru.loginov.serbian.bot.data.dto.localization.LocalizedIdFieldBridge
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId

@Entity
@Indexed
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
class CategoryLocalizedName {

    constructor() {}

    constructor(categoryId: Int, locale: String, name: String?)
            : this(CategoryDescription(categoryId), locale, name)

    constructor(entity: CategoryDescription?, locale: String, name: String?) {
        this.localizedId = LocalizedId()
        this.localizedId!!.locale = locale
        this.entity = entity
        this.name = name
    }


    @EmbeddedId
    @DocumentId(identifierBridge = IdentifierBridgeRef(type = LocalizedIdFieldBridge::class))
    var localizedId: LocalizedId? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id")
    @JoinColumn(name = "id")
    @JsonIgnore
    var entity: CategoryDescription? = null

    @Column(nullable = false)
    @KeywordField
    var name: String? = null

}