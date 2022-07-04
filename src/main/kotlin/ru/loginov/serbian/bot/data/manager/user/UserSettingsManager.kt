package ru.loginov.serbian.bot.data.manager.user

import org.springframework.stereotype.Service

@Service
interface UserSettingsManager {

    val possibleSettings: List<String>

    fun getPossibleValues(key: String): List<String>?

}