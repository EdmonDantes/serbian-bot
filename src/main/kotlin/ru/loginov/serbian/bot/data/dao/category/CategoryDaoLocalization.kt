package ru.loginov.serbian.bot.data.dao.category

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField
import ru.loginov.serbian.bot.data.dao.localization.LocalizedId
import ru.loginov.serbian.bot.data.dao.localization.LocalizedIdFieldBridge
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId

@Entity
@Indexed
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
class CategoryDaoLocalization {

    constructor() {}

    constructor(entity: CategoryDao?, locale: String, name: String?) {
        this.localizedId = LocalizedId()
        this.localizedId!!.locale = locale
        this.entity = entity
        this.name = name
    }


    @EmbeddedId
    @DocumentId(identifierBridge = IdentifierBridgeRef(type = LocalizedIdFieldBridge::class))
    var localizedId: LocalizedId? = null

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "id")
    @JsonIgnore
    var entity: CategoryDao? = null

    @Column(nullable = false)
    @KeywordField
    var name: String? = null

}