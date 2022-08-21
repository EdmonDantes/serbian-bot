package ru.loginov.serbian.bot.data.manager.user

import io.github.edmondantes.simple.localization.exception.LanguageNotSupportedException
import ru.loginov.serbian.bot.data.dto.user.UserDescription

/**
 * Class for manage users
 */
interface UserManager {

    /**
     * Create new user with properties.
     * @param language if language is null or application isn't support it, it will be set to default ('en' - English)
     * @return if created it returns [UserDescription], else null
     */
    fun create(
            userId: Long,
            chatId: Long? = null,
            language: String? = null,
            canInputDifferentLanguages: Boolean? = null,
            permissionGroup: String? = null
    ): UserDescription?

    /**
     * Update new user with properties
     * @param language If application isn't support this language throws exception
     * @throws LanguageNotSupportedException if application is not support [language]
     * @return If updated it returns [UserDescription], else null
     */
    @Throws(LanguageNotSupportedException::class)
    fun update(
            userId: Long,
            chatId: Long? = null,
            language: String? = null,
            canInputDifferentLanguages: Boolean? = null,
            permissionGroup: String? = null
    ): UserDescription?

    /**
     * Try to find user with [userId]
     * @return If found it returns [UserDescription], else null
     */
    fun findById(userId: Long): UserDescription?

    /**
     * Try to find user with [userId], and if it found user, load additional data for [additionalDataKeys].
     * Additional data may be null, if it isn't saved
     * @return If found it returns [UserDescription] with additional data, else null
     */
    fun findByIdWithData(userId: Long, additionalDataKeys: List<String>): UserDescription?

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