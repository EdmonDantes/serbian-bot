package ru.loginov.serbian.bot.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.jpa.repository.JpaRepository
import ru.loginov.serbian.bot.data.repository.search.SearchRepository

suspend inline fun <T, I, R, REPO : JpaRepository<T, I>> REPO.useSuspend(noinline block: (REPO) -> R): R =
        withContext(Dispatchers.IO) {
            block(this@useSuspend)
        }

suspend inline fun <T, R, REPO : SearchRepository<T>> REPO.useSuspend(noinline block: (REPO) -> R): R =
        withContext(Dispatchers.IO) {
            block(this@useSuspend)
        }
