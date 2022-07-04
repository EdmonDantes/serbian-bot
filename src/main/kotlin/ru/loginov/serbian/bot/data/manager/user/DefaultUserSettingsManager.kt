package ru.loginov.serbian.bot.data.manager.user

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DefaultUserSettingsManager(
        @Value("\${bot.user.settings:}") settings: String
) : UserSettingsManager {
    override val possibleSettings: List<String> = settings.split(";").filter { it.isNotEmpty() }
    override fun getPossibleValues(key: String): List<String>? = null
}