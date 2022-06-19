package ru.loginov.serbian.bot.util

import ru.loginov.serbian.bot.spring.localization.context.LocalizationContext
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
        if (str.length < 5) {
            super.append(str, needToEscape)
        } else {
            super.append(localizationContext.transformStringToLocalized(str), needToEscape)
        }
    }
}

fun markdown2(context: LocalizationContext, block: Markdown2StringBuilder.() -> Unit): Markdown2StringBuilder =
        LocalizedMarkdown2StringBuilder(context).apply(block)

fun SendMessageRequest.markdown2(
        context: LocalizationContext,
        block: Markdown2StringBuilder.() -> Unit
): SendMessageRequest =
        markdown2(LocalizedMarkdown2StringBuilder(context), block)