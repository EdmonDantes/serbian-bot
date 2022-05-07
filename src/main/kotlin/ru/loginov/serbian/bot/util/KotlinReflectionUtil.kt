package ru.loginov.serbian.bot.util

import java.lang.reflect.Method
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaMethod

fun KCallable<*>.tryToGetJavaMethods(): List<Method> = when (this) {
    is KFunction<*> -> listOf(javaMethod)
    is KMutableProperty<*> -> listOf(getter.javaMethod, setter.javaMethod)
    is KProperty<*> -> listOf(getter.javaMethod)
    else -> emptyList()
}.mapNotNull { it }