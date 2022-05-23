package ru.loginov.serbian.bot.util

import ru.loginov.serbian.bot.spring.localization.context.LocalizationContext
import ru.loginov.telegram.api.request.SendMessageRequest
import ru.loginov.telegram.api.util.TelegramMessageTextBuilder

class TelegramMessageTextBuilderWithLocalization(
        private val context: LocalizationContext
) : TelegramMessageTextBuilder() {
    override fun appendString(str: String, clearFormatting: Boolean) {
        super.appendString(context.transformStringToLocalized(str), clearFormatting)
    }

    override fun returnInstance(): TelegramMessageTextBuilder = this
}

suspend fun markdown2Coroutine(
        context: LocalizationContext,
        block: suspend TelegramMessageTextBuilder.() -> Unit
): TelegramMessageTextBuilder =
        TelegramMessageTextBuilderWithLocalization(context).apply {
            block(this)
        }

suspend fun markdown2StringCoroutine(
        context: LocalizationContext,
        block: suspend TelegramMessageTextBuilder.() -> Unit
): String =
        TelegramMessageTextBuilderWithLocalization(context).apply {
            block(this)
        }.toMarkdownV2String()

fun markdown2(
        context: LocalizationContext,
        block: TelegramMessageTextBuilder.() -> Unit
): TelegramMessageTextBuilder =
        TelegramMessageTextBuilderWithLocalization(context).apply {
            block(this)
        }

fun SendMessageRequest.markdown2(
        context: LocalizationContext,
        block: TelegramMessageTextBuilder.() -> Unit
): SendMessageRequest =
        markdown2(TelegramMessageTextBuilderWithLocalization(context), block)

fun markdown2String(
        context: LocalizationContext,
        block: TelegramMessageTextBuilder.() -> Unit
): String =
        TelegramMessageTextBuilderWithLocalization(context).apply {
            block(this)
        }.toMarkdownV2String()

