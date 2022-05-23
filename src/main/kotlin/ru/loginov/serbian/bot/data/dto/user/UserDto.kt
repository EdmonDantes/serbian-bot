package ru.loginov.serbian.bot.data.dto.user

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserDto {

    constructor() {}
    constructor(id: Long) {
        this.id = id
    }

    @Id
    var id: Long? = null

    /**
     * Direct chat id
     */
    var chatId: Long? = null

    /**
     * Language for user
     */
    var language: String? = null

    /**
     * If true, bot will send menu for language choosing
     */
    var canInputDifferentLanguages: Boolean? = null

    /**
     * Permissions group
     */
    var permissionGroup: String? = null

    /**
     * Additional data for user (custom)
     * If you want to modify it, please use manager's methods
     */
    @Transient // Will be set in manager, because we can save many data for different commands
    var additionalData: Map<String, String> = emptyMap()

    fun getInputLanguageOr(supplier: () -> String?): String? =
            if (language == null || canInputDifferentLanguages == true) {
                supplier()
            } else language

    suspend fun getInputLanguageOr(supplier: suspend () -> String?): String? =
            if (language == null || canInputDifferentLanguages == true) {
                supplier()
            } else language
}