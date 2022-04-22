package ru.loginov.telegram.api.exception

class ResponseErrorException : Exception {
    val errorMessage: String?

    constructor() : this(null)

    constructor(message: String?) : this(message, null)

    constructor(
            message: String?,
            cause: Throwable?
    ) : super("Error: $message", cause) {
        this.errorMessage = message
    }
}