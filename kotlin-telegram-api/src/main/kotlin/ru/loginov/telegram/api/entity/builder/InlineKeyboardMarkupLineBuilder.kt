package ru.loginov.telegram.api.entity.builder

import ru.loginov.telegram.api.entity.InlineKeyboardButton

class InlineKeyboardMarkupLineBuilder : AbstractKeyboardLineBuilder<InlineKeyboardButton, InlineKeyboardMarkupButtonBuilder>() {
    override fun createKeyboardBuilder(): InlineKeyboardMarkupButtonBuilder = InlineKeyboardMarkupButtonBuilder()
}