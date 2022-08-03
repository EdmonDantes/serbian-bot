package ru.loginov.serbian.bot.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.jpa.repository.JpaRepository
import ru.loginov.serbian.bot.data.repository.search.SearchRepository

suspend inline fun <T, I, R, REPO : JpaRepository<T, I>> REPO.useSuspend(crossinline block: (REPO) -> R): R =
        withContext(Dispatchers.IO) {
            block(this@useSuspend)
        }

suspend inline fun <T, R, REPO : SearchRepository<T>> REPO.useSuspend(crossinline block: (REPO) -> R): R =
        withContext(Dispatchers.IO) {
            block(this@useSuspend)
        }

inline fun <T, I, REPO : JpaRepository<T, I>> REPO.saveOr(obj: T, exceptionHandler: (e: Exception) -> T?): T? =
        obj?.let {
            try {
                save(it)
            } catch (e: Exception) {
                exceptionHandler(e)
            }
        }

inline fun <T, I, REPO : JpaRepository<T, I>> REPO.saveAndFlushOr(obj: T, exceptionHandler: (e: Exception) -> T?): T? =
        obj?.let {
            try {
                saveAndFlush(it)
            } catch (e: Exception) {
                exceptionHandler(e)
            }
        }