package ru.loginov.serbian.bot.spring.permission.annotation

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION])
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class PermissionCheck(val enabled: Boolean = true)
