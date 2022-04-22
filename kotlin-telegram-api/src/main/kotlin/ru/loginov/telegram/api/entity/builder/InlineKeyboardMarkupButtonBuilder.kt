package ru.loginov.telegram.api.entity.builder

import ru.loginov.telegram.api.entity.InlineKeyboardButton

class InlineKeyboardMarkupButtonBuilder : AbstractKeyboardButtonBuilder<InlineKeyboardButton>() {

    private var text: String? = null
    private var url: String? = null
    private var callbackData: String? = null
    var switchInlineQuery: String? = null // FIXME: Make private and write methods for build
    var switchInlineQueryCurrentChat: String? = null // FIXME: Make private and write methods for build
    private var pay: Boolean? = null

    fun text(text: CharSequence) {
        this.text = text.toString()
    }

    fun text(block: () -> CharSequence) = text(block())

    fun url(url: String) {
        this.url = url
    }

    fun callbackData(data: String) {
        this.callbackData = data
    }

    fun requiredPay() {
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