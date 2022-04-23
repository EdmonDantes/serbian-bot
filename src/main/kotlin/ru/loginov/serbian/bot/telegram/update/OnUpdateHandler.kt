package ru.loginov.serbian.bot.telegram.update

import ru.loginov.telegram.api.entity.Update

interface OnUpdateHandler {

    fun onUpdate(update: Update)

}