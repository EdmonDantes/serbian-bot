package ru.loginov.simple.localization.impl

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest

class DefaultLocalizationRequest(override val keys: List<LocalizationKey>) : LocalizationRequest {

    constructor(vararg keys: LocalizationKey) : this(keys.toList())

    operator fun plus(request: LocalizationRequest): LocalizationRequest =
            DefaultLocalizationRequest(keys + request.keys)

    override fun equals(other: Any?): Boolean =
            other === this
                    || other != null
                    && other.javaClass == this.javaClass
                    && keys == (other as LocalizationRequest).keys


    override fun hashCode(): Int =
            keys.hashCode()

    override fun toString(): String =
            keys.joinToString(";")

    class Builder {
        private val keys = ArrayList<LocalizationKey>()

        fun add(key: String, arguments: List<String>): Builder = this.apply {
            keys.add(DefaultLocalizationKey(key, arguments))
        }

        fun add(key: String, vararg arguments: String): Builder = this.apply {
            keys.add(DefaultLocalizationKey(key, *arguments))
        }

        fun withoutLocalization(str: String): Builder = this.apply {
            keys.add(DefaultLocalizationKey("", listOf(str)))
        }

        fun build(): LocalizationRequest =
                DefaultLocalizationRequest(keys.toList())
    }
}

inline fun localizationRequest(block: DefaultLocalizationRequest.Builder.() -> Unit): LocalizationRequest =
        DefaultLocalizationRequest.Builder().apply(block).build()

fun singleRequest(key: String, vararg arguments: Any): LocalizationRequest =
        DefaultLocalizationRequest(listOf(DefaultLocalizationKey(key, arguments.map { it.toString() })))

fun singleRequest(key: String, arguments: List<Any>): LocalizationRequest =
        DefaultLocalizationRequest(listOf(DefaultLocalizationKey(key, arguments.map { it.toString() })))

fun stringRequest(str: String): LocalizationRequest =
        DefaultLocalizationRequest(listOf(DefaultLocalizationKey("", listOf(str))))