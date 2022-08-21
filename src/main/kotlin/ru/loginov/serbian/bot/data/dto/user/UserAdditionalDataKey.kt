package ru.loginov.serbian.bot.data.dto.user

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class UserAdditionalDataKey : Serializable {

    @Column(nullable = false, updatable = false)
    var userId: Int? = null

    @Column(nullable = false, updatable = false)
    var key: String? = null

    constructor()
    constructor(id: Int?, key: String?) {
        this.userId = id
        this.key = key
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserAdditionalDataKey

        if (userId != other.userId) return false
        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId ?: 0
        result = 31 * result + (key?.hashCode() ?: 0)
        return result
    }


}