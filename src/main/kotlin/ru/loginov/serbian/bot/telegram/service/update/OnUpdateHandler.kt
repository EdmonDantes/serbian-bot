package ru.loginov.serbian.bot.telegram.service.update

import ru.loginov.serbian.bot.telegram.entity.Update

interface OnUpdateHandler {

    fun onUpdate(update: Update)

}