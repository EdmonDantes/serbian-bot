package ru.loginov.serbian.bot.spring.permission.annotation

@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.CLASS])
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@MustBeDocumented
annotation class RequiredPermission(val permission: String)
