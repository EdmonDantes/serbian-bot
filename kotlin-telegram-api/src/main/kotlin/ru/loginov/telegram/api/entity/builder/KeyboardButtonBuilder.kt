package ru.loginov.telegram.api.entity.builder

import ru.loginov.telegram.api.entity.KeyboardButton

class KeyboardButtonBuilder : AbstractKeyboardButtonBuilder<KeyboardButton>() {
    private var text: String? = null
    private var requestContact: Boolean? = null
    private var requestLocation: Boolean? = null

    fun text(text: CharSequence) {
        this.text = text.toString()
    }

    fun text(block: () -> CharSequence) = text(block())

    fun requestContact() {
        if (requestLocation == true) {
            error("Only one request can be in button. Button already has location request")
        }
        requestContact = true
    }

    fun requestLocation() {
        if (requestContact == true) {
            error("Only one request can be in button. Button already has contact request")
        }
        requestLocation = true
    }

    override fun build(): KeyboardButton = KeyboardButton(
            text ?: error("Builder hasn't text. Can not create keyboard button without text"),
            requestContact,
            requestLocation
    )
}