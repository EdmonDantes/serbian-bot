package ru.loginov.serbian.bot.spring.permission.annotation

@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IgnorePermissionCheckFor(
        val memberNames: Array<String> = [],
        val all: Boolean = true
)
