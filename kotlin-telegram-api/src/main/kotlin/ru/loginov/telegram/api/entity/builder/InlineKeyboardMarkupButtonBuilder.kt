package ru.loginov.telegram.api.entity.builder

import ru.loginov.telegram.api.entity.InlineKeyboardButton

class InlineKeyboardMarkupButtonBuilder : AbstractKeyboardButtonBuilder<InlineKeyboardButton>() {

    var text: String? = null
    var url: String? = null
    var callbackData: String? = null
    var switchInlineQuery: String? = null // FIXME: Make private and write methods for build
    var switchInlineQueryCurrentChat: String? = null // FIXME: Make private and write methods for build
    var pay: Boolean? = null

    fun requiredPay(): InlineKeyboardMarkupButtonBuilder = apply {
        pay = true
    }

    override fun build(): InlineKeyboardButton = InlineKeyboardButton(
            text ?: error("Builder hasn't text. Can not create keyboard button without text"),
            url,
            callbackData,
            switchInlineQuery,
            switchInlineQueryCurrentChat,
            pay

    )
}