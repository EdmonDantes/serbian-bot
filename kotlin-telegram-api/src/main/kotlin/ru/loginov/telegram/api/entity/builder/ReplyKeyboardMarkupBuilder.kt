package ru.loginov.telegram.api.entity.builder

import ru.loginov.telegram.api.entity.KeyboardButton
import ru.loginov.telegram.api.entity.ReplyKeyboardMarkup

class ReplyKeyboardMarkupBuilder : AbstractKeyboardBuilder<ReplyKeyboardMarkup, KeyboardButton, ReplyKeyboardMarkupLineBuilder, KeyboardButtonBuilder>() {

    private var resizeOnClient: Boolean? = null
    private var oneTime: Boolean? = null
    private var placeholder: String? = null
    private var selective: Boolean? = null

    fun shouldResizeOnClient(): ReplyKeyboardMarkupBuilder = apply {
        resizeOnClient = true
    }

    fun once(): ReplyKeyboardMarkupBuilder = apply {
        oneTime = true
    }

    fun placeholder(placeholder: String): ReplyKeyboardMarkupBuilder = apply {
        this.placeholder = placeholder
    }

    fun selective(): ReplyKeyboardMarkupBuilder = apply {
        this.selective = selective
    }

    override fun createLineBuilder(): ReplyKeyboardMarkupLineBuilder = ReplyKeyboardMarkupLineBuilder()

    override fun internalBuild(keyboard: List<List<KeyboardButton>>): ReplyKeyboardMarkup =
            ReplyKeyboardMarkup(keyboard, resizeOnClient, oneTime, placeholder, selective)
}