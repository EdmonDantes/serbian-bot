package ru.loginov.simple.localization.impl

import ru.loginov.simple.localization.LocalizationKey

class DefaultLocalizationKey(override val key: String, override val arguments: List<String>) : LocalizationKey {
    constructor(key: String, vararg arguments: String) : this(key, arguments.toList())

    override fun equals(other: Any?): Boolean =
            other === this
                    || other != null
                    && other.javaClass == this.javaClass
                    && key == (other as LocalizationKey).key
                    && arguments == other.arguments

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + arguments.hashCode()
        return result
    }

    override fun toString(): String =
            "(key=$key,arguments=$arguments)"
}

fun localizationKey(key: String, arguments: List<String>): LocalizationKey =
        DefaultLocalizationKey(key, arguments)

fun localizationKey(key: String, vararg arguments: Any?): LocalizationKey =
        DefaultLocalizationKey(key, arguments.map { it.toString() })