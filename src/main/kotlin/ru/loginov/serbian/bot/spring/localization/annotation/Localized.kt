package ru.loginov.serbian.bot.spring.localization.annotation

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER])
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Localized()
