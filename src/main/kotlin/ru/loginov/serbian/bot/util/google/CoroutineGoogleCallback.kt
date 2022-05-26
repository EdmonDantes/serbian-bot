package ru.loginov.serbian.bot.util.google

import com.google.maps.PendingResult
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CoroutineGoogleCallback<T>(private val continuation: Continuation<T>) : PendingResult.Callback<T> {
    override fun onResult(result: T) {
        continuation.resume(result)
    }

    override fun onFailure(e: Throwable) {
        continuation.resumeWithException(e)
    }
}

suspend fun <T> PendingResult<T>.suspendAndAwait(): T =
        suspendCoroutine { continuation ->
            this.setCallback(CoroutineGoogleCallback<T>(continuation))
        }