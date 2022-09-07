package ru.loginov.serbian.bot.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.loginov.http.HttpClient
import ru.loginov.telegram.api.DefaultTelegramAPI
import ru.loginov.telegram.api.TelegramAPI

@Configuration
@ConditionalOnProperty(name = ["bot.telegram.server.url", "bot.telegram.token"])
@Import(HttpConfiguration::class)
class TelegramAPIConfiguration {

    @Value("\${bot.telegram.server.url}")
    private lateinit var telegramServerUrl: String

    @Value("\${bot.telegram.token}")
    private lateinit var telegramToken: String

    @Bean
    fun telegramApi(@Qualifier("telegramHttpClient") httpClient: HttpClient): TelegramAPI = DefaultTelegramAPI(
            httpClient,
            telegramServerUrl + telegramToken
    )
}