package ru.loginov.serbian.bot.data.dto.localization

import org.hibernate.search.mapper.pojo.bridge.IdentifierBridge
import org.hibernate.search.mapper.pojo.bridge.runtime.IdentifierBridgeFromDocumentIdentifierContext
import org.hibernate.search.mapper.pojo.bridge.runtime.IdentifierBridgeToDocumentIdentifierContext

class LocalizedIdFieldBridge : IdentifierBridge<LocalizedId> {
    override fun toDocumentIdentifier(propertyValue: LocalizedId?, context: IdentifierBridgeToDocumentIdentifierContext): String {
        return propertyValue?.let { "${it.locale}/${it.id}" } ?: "none/0"

    }

    override fun fromDocumentIdentifier(documentIdentifier: String, context: IdentifierBridgeFromDocumentIdentifierContext): LocalizedId {
        return documentIdentifier.split('/').let { array ->
            if (array.size > 1) {
                array[1].toIntOrNull()?.let {
                    LocalizedId(it, array[0])
                }
            } else null
        } ?: LocalizedId(-1, "none")
    }
}