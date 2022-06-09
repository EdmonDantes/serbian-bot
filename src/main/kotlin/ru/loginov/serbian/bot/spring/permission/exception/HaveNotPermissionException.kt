package ru.loginov.serbian.bot.spring.permission.exception

class HaveNotPermissionException : RuntimeException {

    constructor() : super()
    constructor(th: Throwable) : super(th)

}
