package ru.loginov.telegram.api.entity.builder

abstract class AbstractKeyboardButtonBuilder<T> {
    abstract fun build() : T
}