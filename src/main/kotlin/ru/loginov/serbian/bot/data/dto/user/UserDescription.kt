package ru.loginov.serbian.bot.data.dto.user

import ru.loginov.serbian.bot.data.dto.WithId
import java.util.Collections
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class UserDescription : WithId {

    constructor() {}
    constructor(
            id: Int? = null,
            language: String? = null,
            permissionGroup: String? = null,
            additionalData: Map<String, String> = Collections.emptyMap()
    ) {
        this.id = id
        this.language = language
        this.permissionGroup = permissionGroup
        this.additionalData = additionalData
    }

    @Id
    @GeneratedValue
    override var id: Int? = null //FIXME: Telegram user id move to additional data

    //TODO: Move to UserAdditionalData
//    /**
//     * Direct chat id
//     */
//    var chatId: Long? = null

    /**
     * Language for user
     */
    @Column(nullable = false)
    var language: String? = null

    //TODO: Move to UserAdditionalData
//    /**
//     * If true, bot will send menu for language choosing
//     */
//    @Column(nullable = false)
//    var canInputDifferentLanguages: Boolean? = null

    /**
     * Permissions group
     */
    @Column(nullable = false)
    var permissionGroup: String? = null

    /**
     * Additional data for user (custom)
     * If you want to modify it, please use manager's methods
     */
    @Transient // Will be set in manager, because we can save many data for different commands
    var additionalData: Map<String, String> = emptyMap()
}