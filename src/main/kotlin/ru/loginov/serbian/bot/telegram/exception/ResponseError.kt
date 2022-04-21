package ru.loginov.serbian.bot.telegram.exception

class ResponseError : Exception {
    val errorMessage: String?
    val code: Int?

    constructor() : this(-1, "")

    constructor(code: Int?, message: String?) : this(code, message, null)

    constructor(
            code: Int?,
            message: String?,
            cause: Throwable?
    ) : super("Error ($code): $message", cause) {
        this.errorMessage = message
        this.code = code
    }
}