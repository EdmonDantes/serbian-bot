package ru.loginov.serbian.bot.data.manager.user

import ru.loginov.serbian.bot.data.dto.user.UserDto

interface UserManager {

    fun createUser(userId: Long, charId: Long, language: String?) : UserDto?

    fun getUser(userId: Long) : UserDto?
    fun getUserWithData(userId: Long, additionalDataKeys: List<String>) : UserDto?

    fun getAdditionalData(userId: Long, additionalDataKeys: List<String>) : Map<String, String>
    fun setAdditionalData(userId: Long, key: String, value: Any?)
}