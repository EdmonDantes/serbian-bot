package ru.loginov.serbian.bot.configuration

import com.google.maps.GeoApiContext
import org.slf4j.LoggerFactory
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
        if (token.isNullOrEmpty()) {
            LOGGER.warn("Google map api token is empty. Map service will not work and will throw exception.")
        }

        context = GeoApiContext.Builder()
                .apiKey(token)
                .queryRateLimit(requestsLimit)
                .build()
        return context!!
    }

    @PreDestroy
    fun preDestroy() {
        context?.shutdown()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(GoogleApiConfiguration::class.java)
    }

}