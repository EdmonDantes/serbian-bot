package ru.loginov.simple.permissions.annotation

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION])
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ForcePermissionCheck(val enabled: Boolean = true)
