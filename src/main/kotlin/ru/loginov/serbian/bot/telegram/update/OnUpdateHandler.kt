package ru.loginov.serbian.bot.telegram.update

import ru.loginov.telegram.api.entity.Update

interface OnUpdateHandler {

    suspend fun onUpdate(update: Update)

}