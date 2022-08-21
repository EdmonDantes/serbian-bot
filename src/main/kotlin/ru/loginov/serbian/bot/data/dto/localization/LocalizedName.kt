package ru.loginov.serbian.bot.data.dto.localization

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField
import ru.loginov.serbian.bot.data.dto.WithId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MappedSuperclass
import javax.persistence.MapsId
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.isAccessible

@MappedSuperclass
open class LocalizedName<T : WithId> {

    constructor()

    constructor(entity: T?, locale: String?, name: String?) {
        this.localizedId.locale = locale
        this.localizedId.id = entity?.id
        this.entity = entity
        this.name = name
    }

    @EmbeddedId
    @DocumentId(identifierBridge = IdentifierBridgeRef(type = LocalizedIdFieldBridge::class))
    var localizedId: LocalizedId = LocalizedId()

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id")
    @JoinColumn(name = "id", insertable = false, updatable = false)
    var entity: T? = null

    @Column(nullable = false)
    @KeywordField
    var name: String? = null
}

inline fun <E : WithId, reified T : LocalizedName<E>> E.prepareLocalization(map: Map<String, T>): Map<String, T> =
        map.onEach { (locale, localizedName) ->
            localizedName.localizedId.id = id
            localizedName.localizedId.locale = locale
            localizedName.entity = this
        }

inline fun <E, reified T : LocalizedName<E>> constructLocalization(map: Map<String, String?>): Map<String, T> {
    val constructor = T::class.constructors.find { it.parameters.isEmpty() && it.visibility == KVisibility.PUBLIC }
            ?: error("Class ${T::class} haven't public empty construct for creating instance")

    val prevAccessible = constructor.isAccessible
    constructor.isAccessible = true

    val results = map.mapValues { (locale, localization) ->
        constructor.call().apply {
            localizedId.locale = locale
            name = localization
        }
    }

    constructor.isAccessible = prevAccessible

    return results
}

inline fun <E, reified T : LocalizedName<E>> constructLocalization(vararg pairs: Pair<String, String>): Map<String, T> =
        constructLocalization(mapOf(*pairs))
