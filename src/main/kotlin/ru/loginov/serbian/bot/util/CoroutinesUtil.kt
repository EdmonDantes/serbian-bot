package ru.loginov.serbian.bot.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.jpa.repository.JpaRepository

suspend inline fun <T, I, R, REPO : JpaRepository<T, I>> REPO.useSuspend(noinline block: (REPO) -> R): R =
        withContext(Dispatchers.IO) {
            block(this@useSuspend)
        }
