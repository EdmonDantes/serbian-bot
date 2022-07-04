package ru.loginov.serbian.bot.data.manager.user

import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.simple.localization.exception.LanguageNotSupportedException

/**
 * Class for manage users
 */
interface UserManager {

    /**
     * Create new user with properties.
     * @param language if language is null or application isn't support it, it will be set to default ('en' - English)
     * @return if created it returns [UserDto], else null
     */
    fun create(
            userId: Long,
            chatId: Long? = null,
            language: String? = null,
            canInputDifferentLanguages: Boolean? = null,
            permissionGroup: String? = null
    ): UserDto?

    /**
     * Update new user with properties
     * @param language If application isn't support this language throws exception
     * @throws LanguageNotSupportedException if application is not support [language]
     * @return If updated it returns [UserDto], else null
     */
    @Throws(LanguageNotSupportedException::class)
    fun update(
            userId: Long,
            chatId: Long? = null,
            language: String? = null,
            canInputDifferentLanguages: Boolean? = null,
            permissionGroup: String? = null
    ): UserDto?

    /**
     * Try to find user with [userId]
     * @return If found it returns [UserDto], else null
     */
    fun findById(userId: Long): UserDto?

    /**
     * Try to find user with [userId], and if it found user, load additional data for [additionalDataKeys].
     * Additional data may be null, if it isn't saved
     * @return If found it returns [UserDto] with additional data, else null
     */
    fun findByIdWithData(userId: Long, additionalDataKeys: List<String>): UserDto?

    /**
     * Try to find only additional data for [additionalDataKeys] for user with [userId]
     * @return key - one of [additionalDataKeys], values - additional data value.
     * Can return empty map, if additional data wasn't found
     */
    fun findAdditionalDataByUserId(userId: Long, additionalDataKeys: List<String>): Map<String, String>

    /**
     * Set additional data.
     * @param value if this parameter is null, additional data for [key] will be deleted
     * @return If set new value it returns true, else false
     */
    fun setAdditionalDataByUserId(userId: Long, key: String, value: Any?): Boolean
}