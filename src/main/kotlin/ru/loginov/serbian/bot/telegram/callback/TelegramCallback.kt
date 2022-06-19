package ru.loginov.serbian.bot.telegram.callback

import ru.loginov.serbian.bot.telegram.callback.TelegramCallback.Companion.CANCEL_CALLBACK
import ru.loginov.serbian.bot.telegram.callback.TelegramCallback.Companion.CONTINUE_CALLBACK
import ru.loginov.telegram.api.entity.builder.InlineKeyboardMarkupButtonBuilder

@FunctionalInterface
fun interface TelegramCallback {

    fun execute(data: CallbackData?): Boolean

    companion object {
        const val CANCEL_CALLBACK = "cancel"
        const val CONTINUE_CALLBACK = "continue"
    }

}


/**
 * Add callback with format:
 *
 * **_[chatId]_:_[userId]_#_[data]_**
 */
fun InlineKeyboardMarkupButtonBuilder.callbackData(
        chatId: Long,
        userId: Long?,
        data: Number?
): InlineKeyboardMarkupButtonBuilder = apply {
    this.callbackData = "$chatId${userId?.let { ":$userId" } ?: ""}${data?.let { "#$data" } ?: ""}"
}

/**
 * Add callback with format:
 *
 * **_[chatId]_:_[userId]_#cancel**
 */
fun InlineKeyboardMarkupButtonBuilder.cancelCallback(
        chatId: Long,
        userId: Long?
): InlineKeyboardMarkupButtonBuilder = apply {
    this.callbackData = "$chatId${userId?.let { ":$userId" } ?: ""}#$CANCEL_CALLBACK"
}

/**
 * Add callback with format:
 *
 * **_[chatId]_:_[userId]_#continue**
 */
fun InlineKeyboardMarkupButtonBuilder.continueCallback(
        chatId: Long,
        userId: Long?
): InlineKeyboardMarkupButtonBuilder = apply {
    this.callbackData = "$chatId${userId?.let { ":$userId" } ?: ""}#$CONTINUE_CALLBACK"
}