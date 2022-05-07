package ru.loginov.serbian.bot.spring.permission.annotation

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION])
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@MustBeDocumented
annotation class RequiredPermission(val permission: String)
