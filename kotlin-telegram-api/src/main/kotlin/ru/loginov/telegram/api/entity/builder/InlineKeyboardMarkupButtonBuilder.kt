package ru.loginov.telegram.api.entity.builder

import ru.loginov.telegram.api.entity.InlineKeyboardButton

class InlineKeyboardMarkupButtonBuilder : AbstractKeyboardButtonBuilder<InlineKeyboardButton>() {

    var text: String? = null
    var url: String? = null
    var callbackData: String? = null
    var switchInlineQuery: String? = null // FIXME: Make private and write methods for build
    var switchInlineQueryCurrentChat: String? = null // FIXME: Make private and write methods for build
    var pay: Boolean? = null

    /**
     * Add callback with format:
     *
     * **_[chatId]_:_[userId]_#_[data]_**
     */
    fun callbackData(chatId: Long, userId: Long?, data: Number?): InlineKeyboardMarkupButtonBuilder = apply {
        this.callbackData = "$chatId${userId?.let { ":$userId" } ?: ""}${data?.let { "#$data" } ?: ""}"
    }

    /**
     * Add callback with format:
     *
     * **_[chatId]_:_[userId]_#cancel**
     */
    fun cancelCallback(chatId: Long, userId: Long?): InlineKeyboardMarkupButtonBuilder = apply {
        this.callbackData = "$chatId${userId?.let { ":$userId" } ?: ""}#$CANCEL_CALLBACK"
    }

    /**
     * Add callback with format:
     *
     * **_[chatId]_:_[userId]_#continue**
     */
    fun continueCallback(chatId: Long, userId: Long?): InlineKeyboardMarkupButtonBuilder = apply {
        this.callbackData = "$chatId${userId?.let { ":$userId" } ?: ""}#$CONTINUE_CALLBACK"
    }

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

    companion object {
        const val CANCEL_CALLBACK = "cancel"
        const val CONTINUE_CALLBACK = "continue"
    }
}