package ru.loginov.telegram.api.entity.builder

import ru.loginov.telegram.api.entity.InlineKeyboardMarkup
import ru.loginov.telegram.api.entity.InlineKeyboardButton

class InlineKeyboardMarkupBuilder : AbstractKeyboardBuilder<InlineKeyboardMarkup, InlineKeyboardButton, InlineKeyboardMarkupLineBuilder, InlineKeyboardMarkupButtonBuilder>() {
    override fun createLineBuilder(): InlineKeyboardMarkupLineBuilder = InlineKeyboardMarkupLineBuilder()

    override fun internalBuild(keyboard: List<List<InlineKeyboardButton>>): InlineKeyboardMarkup =
            InlineKeyboardMarkup(keyboard)

}