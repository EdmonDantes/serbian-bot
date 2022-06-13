package ru.loginov.serbian.bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["ru.loginov"])
class SerbianBotApplication

fun main(args: Array<String>) {
	runApplication<SerbianBotApplication>(*args)
}
