package ru.loginov.serbian.bot.spring.permission.exception

import ru.loginov.serbian.bot.data.dto.user.UserDto

class NotFoundPermissionException : RuntimeException {
    constructor(userDto: UserDto) : super("Can not found permissions for user with id: '${userDto.id}'")
    constructor(group: String) : super("Can not found permissions for group: '$group'")
}