package ru.loginov.serbian.bot.data.dto.product

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField
import ru.loginov.serbian.bot.data.dto.localization.LocalizedId
import ru.loginov.serbian.bot.data.dto.localization.LocalizedIdFieldBridge
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId

@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
class ProductDtoLocalization {

    @EmbeddedId
    @DocumentId(identifierBridge = IdentifierBridgeRef(type = LocalizedIdFieldBridge::class))
    var localizedId: LocalizedId = LocalizedId()

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "id", insertable = false, updatable = false)
    var entity: ProductDto? = null

    @JoinColumn(name = "id", nullable = false)
    var entityId: Int? = null

    @Column(nullable = false)
    @KeywordField
    var name: String? = null

}