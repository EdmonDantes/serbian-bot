package ru.loginov.serbian.bot.spring.permission.annotation

@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.CLASS])
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IgnorePermissionCheck()
