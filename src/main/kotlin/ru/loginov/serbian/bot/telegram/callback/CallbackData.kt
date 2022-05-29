package ru.loginov.serbian.bot.telegram.callback

import ru.loginov.telegram.api.entity.Location

data class CallbackData(
        val dataFromMessage: String? = null,
        val dataFromCallback: String? = null,
        val location: Location? = null
)