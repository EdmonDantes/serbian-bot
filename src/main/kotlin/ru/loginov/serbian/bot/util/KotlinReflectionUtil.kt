package ru.loginov.serbian.bot.util

import com.fasterxml.jackson.module.kotlin.isKotlinClass
import java.lang.reflect.Method
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaMethod

internal fun Class<*>.getAnyAnnotations(): List<Annotation> =
        if (isKotlinClass()) {
            this.kotlin.annotations
        } else {
            this.annotations.toList()
        }

fun KCallable<*>.tryToGetJavaMethods(): List<Method> = when (this) {
    is KFunction<*> -> listOf(javaMethod)
    is KMutableProperty<*> -> listOf(getter.javaMethod, setter.javaMethod)
    is KProperty<*> -> listOf(getter.javaMethod)
    else -> emptyList()
}.mapNotNull { it }