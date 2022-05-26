package ru.loginov.serbian.bot.configuration

import io.ktor.client.engine.okhttp.OkHttp
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.loginov.http.HttpClient

@Configuration
class HttpConfiguration {

    @Bean
    fun httpClient(): HttpClient {
        return HttpClient(OkHttp)
    }

}