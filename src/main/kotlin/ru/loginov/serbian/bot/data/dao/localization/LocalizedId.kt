package ru.loginov.serbian.bot.data.dao.localization

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
class LocalizedId : Serializable {

    var id: Int? = null
    var locale: String? = null

    constructor() {}
    constructor(id: Int, locale: String) : this() {
        this.id = id
        this.locale = locale
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocalizedId) return false

        if (id != other.id) return false
        if (locale != other.locale) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (locale?.hashCode() ?: 0)
        return result
    }


}