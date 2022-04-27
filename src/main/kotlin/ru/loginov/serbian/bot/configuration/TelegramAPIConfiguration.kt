package ru.loginov.serbian.bot.configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.loginov.http.HttpClient
import ru.loginov.telegram.api.DefaultTelegramAPI
import ru.loginov.telegram.api.TelegramAPI
import java.util.ResourceBundle
import java.util.concurrent.Executor

@Configuration
class TelegramAPIConfiguration {

    @Value("\${bot.server.url}")
    private lateinit var telegramServerUrl: String

    @Value("\${bot.token}")
    private lateinit var telegramToken: String

    @Bean
    fun telegramApi(): TelegramAPI = DefaultTelegramAPI(HttpClient(), telegramServerUrl + telegramToken)
}