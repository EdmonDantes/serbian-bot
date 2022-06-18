package ru.loginov.serbian.bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["ru.loginov"])
@EnableJpaRepositories(basePackages = ["ru.loginov.serbian.bot.data.repository.*"])
@EntityScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
class SerbianBotApplication

fun main(args: Array<String>) {
	System.setProperty("org.jboss.logging.provider", "slf4j")
	System.setProperty("org.springframework.boot.logging.LoggingSystem", "none")
	runApplication<SerbianBotApplication>(*args)
}
