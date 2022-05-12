package ru.loginov.serbian.bot.configuration

import com.google.maps.GeoApiContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy

@Configuration
@ConditionalOnProperty(name = ["bot.google.maps.api.key"])
class GoogleApiConfiguration {

    private var context: GeoApiContext? = null

    @Bean
    fun geoApiContext(
            @Value("\${bot.google.maps.api.key}") token: String?,
            @Value("\${bot.google.maps.api.request.limit:10}") requestsLimit: Int
    ): GeoApiContext {
        context = GeoApiContext.Builder()
                .apiKey(token ?: error("Google map api key is null"))
                .queryRateLimit(requestsLimit)
                .build()
        return context!!
    }

    @PreDestroy
    fun preDestroy() {
        context?.shutdown()
    }

}