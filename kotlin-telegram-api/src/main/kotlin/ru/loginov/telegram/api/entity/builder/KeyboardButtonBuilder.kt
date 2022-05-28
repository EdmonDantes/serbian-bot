package ru.loginov.telegram.api.entity.builder

import ru.loginov.telegram.api.entity.KeyboardButton
import ru.loginov.telegram.api.entity.KeyboardButtonPollType
import ru.loginov.telegram.api.entity.PollType

class KeyboardButtonBuilder : AbstractKeyboardButtonBuilder<KeyboardButton>() {
    var text: String? = null
    var requestContact: Boolean? = null
    var requestLocation: Boolean? = null
    var requestPoll: PollType? = null

    fun text(text: CharSequence) {
        this.text = text.toString()
    }

    fun text(block: () -> CharSequence) = text(block())

    fun requestContact() {
        checkRequestOrThrow()
        requestContact = true
    }

    fun requestLocation() {
        checkRequestOrThrow()
        requestLocation = true
    }

    fun requestPoll(type: PollType?) {
        checkRequestOrThrow()
        this.requestPoll = type
    }


    private fun checkRequestOrThrow() {
        if (requestLocation == true) {
            error("Only one request can be in button. Button already has location request")
        }
        if (requestContact == true) {
            error("Only one request can be in button. Button already has contact request")
        }
        if (requestPoll != null) {
            error("Only one request can be in button. Button already has poll request")
        }
    }

    override fun build(): KeyboardButton = KeyboardButton(
            text ?: error("Builder hasn't text. Can not create keyboard button without text"),
            requestContact,
            requestLocation,
            requestPoll?.let { KeyboardButtonPollType(it) }
    )
}