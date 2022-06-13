package ru.loginov.simple.permissions.exception

class AccessDeniedException : RuntimeException {
    constructor() : super()
    constructor(th: Throwable) : super(th)
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) :
            super(message, cause, enableSuppression, writableStackTrace)
}