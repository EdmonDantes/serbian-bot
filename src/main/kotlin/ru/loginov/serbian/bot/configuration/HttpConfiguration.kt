package ru.loginov.serbian.bot.configuration

import io.ktor.client.engine.okhttp.OkHttp
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.loginov.http.HttpClient

@Configuration
class HttpConfiguration {

    @Bean
    @Qualifier("telegramHttpClient")
    fun httpClient(): HttpClient {
        return HttpClient(OkHttp)
    }

}