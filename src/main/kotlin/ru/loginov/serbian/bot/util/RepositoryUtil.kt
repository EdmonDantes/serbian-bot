package ru.loginov.serbian.bot.util

import org.springframework.data.jpa.repository.JpaRepository

inline fun <T, I, REPO : JpaRepository<T, I>> REPO.saveOr(obj: T, supplier: (e: Exception) -> T?): T? =
        try {
            save(obj)
        } catch (e: Exception) {
            supplier(e)
        }

inline fun <T, I, REPO : JpaRepository<T, I>> REPO.saveAndFlushOr(obj: T, supplier: (e: Exception) -> T?): T? =
        try {
            saveAndFlush(obj)
        } catch (e: Exception) {
            supplier(e)
        }