package ru.loginov.serbian.bot.data.dto.purchase

import javax.persistence.Embeddable

@Embeddable
class PurchaseTrustLevel {

    private var _level: Int? = null

    val level: Int
        get() = _level ?: error("Not yet init this object")

    constructor()
    constructor(level: Int) {
        this._level = level
    }

    companion object {
        val NOT_TRUSTED = PurchaseTrustLevel(0)
        val TRUSTED = PurchaseTrustLevel(500)
        val OFFICIAL = PurchaseTrustLevel(1000)
    }
}