package ru.loginov.telegram.api.entity.builder

import ru.loginov.telegram.api.entity.KeyboardButton

class ReplyKeyboardMarkupLineBuilder : AbstractKeyboardLineBuilder<KeyboardButton, KeyboardButtonBuilder>() {
    override fun createKeyboardBuilder(): KeyboardButtonBuilder = KeyboardButtonBuilder()
}