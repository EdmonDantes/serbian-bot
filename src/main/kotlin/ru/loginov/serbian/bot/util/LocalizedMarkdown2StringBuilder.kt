package ru.loginov.serbian.bot.util

import ru.loginov.simple.localization.LocalizationKey
import ru.loginov.simple.localization.LocalizationRequest
import ru.loginov.simple.localization.context.LocalizationContext
import ru.loginov.telegram.api.request.SendMessageRequest
import ru.loginov.telegram.api.util.Markdown2StringBuilder
import ru.loginov.telegram.api.util.impl.DefaultMarkdown2StringBuilder

class LocalizedMarkdown2StringBuilder(private val localizationContext: LocalizationContext) : DefaultMarkdown2StringBuilder() {

    override fun append(char: Char, needToEscape: Boolean): Markdown2StringBuilder = this.apply {
        super.append(char, needToEscape)
    }

    override fun append(obj: Any, needToEscape: Boolean): Markdown2StringBuilder = this.apply {
        super.append(obj, needToEscape)
    }

    override fun append(str: String, needToEscape: Boolean): Markdown2StringBuilder = this.apply {
        super.append(str, needToEscape)
    }

    fun append(key: LocalizationKey, needToEscape: Boolean = true): Markdown2StringBuilder = this.apply {
        super.append(localizationContext.localize(key), needToEscape)
    }

    fun append(request: LocalizationRequest, needToEscape: Boolean = true): Markdown2StringBuilder = this.apply {
        super.append(localizationContext.localize(request), needToEscape)
    }
}

inline fun Markdown2StringBuilder.withLocalization(
        context: LocalizationContext,
        block: LocalizedMarkdown2StringBuilder.() -> Unit
): Markdown2StringBuilder =
        append(LocalizedMarkdown2StringBuilder(context).apply(block))

inline fun markdown2(
        context: LocalizationContext,
        block: LocalizedMarkdown2StringBuilder.() -> Unit
): Markdown2StringBuilder =
        LocalizedMarkdown2StringBuilder(context).apply(block)

inline fun SendMessageRequest.markdown2(
        context: LocalizationContext,
        block: LocalizedMarkdown2StringBuilder.() -> Unit
): SendMessageRequest =
        markdown2(LocalizedMarkdown2StringBuilder(context).apply(block))