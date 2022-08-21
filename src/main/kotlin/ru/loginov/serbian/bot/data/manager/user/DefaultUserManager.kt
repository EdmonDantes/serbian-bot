package ru.loginov.serbian.bot.data.manager.user

import io.github.edmondantes.simple.localization.Localizer
import io.github.edmondantes.simple.localization.exception.LanguageNotSupportedException
import io.github.edmondantes.simple.permissions.manager.PermissionManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.user.UserAdditionalData
import ru.loginov.serbian.bot.data.dto.user.UserDescription
import ru.loginov.serbian.bot.data.repository.user.UserAdditionalDataRepository
import ru.loginov.serbian.bot.data.repository.user.UserDescriptionRepository
import ru.loginov.serbian.bot.util.transaction.TransactionHelper

@Service
class DefaultUserManager(
        private val userDescriptionRepository: UserDescriptionRepository,
        private val userAdditionalDataRepository: UserAdditionalDataRepository,
        private val localizationManager: Localizer,
        private val permissionManager: PermissionManager,
        private val transactionHelper: TransactionHelper,
        @Value("\${bot.user.admin.ids:}") adminIdsStr: String
) : UserManager {

    private val adminIds: Set<Long>

    init {
        adminIds = adminIdsStr.split(';').mapNotNull { it.toLongOrNull() }.toSet()
    }

    override fun create(
            userId: Long,
            chatId: Long?,
            language: String?,
            canInputDifferentLanguages: Boolean?,
            permissionGroup: String?
    ): UserDescription? {
        val user = UserDescription()
//        user.id = userId
//        user.chatId = chatId
        user.language =
                if (language == null || !localizationManager.isSupport(language))
                    localizationManager.defaultLanguage
                else
                    language
//        user.canInputDifferentLanguages = canInputDifferentLanguages ?: false
        user.permissionGroup = permissionGroup
                ?: (if (adminIds.contains(userId)) permissionManager.adminGroup else null)
                        ?: permissionManager.defaultGroup

        return try {
            userDescriptionRepository.save(user)
        } catch (e: Exception) {
            LOGGER.error("Can not save user '$user'", e)
            null
        }
    }

    override fun update(
            userId: Long,
            chatId: Long?,
            language: String?,
            canInputDifferentLanguages: Boolean?,
            permissionGroup: String?
    ): UserDescription? {
        if (language != null && !localizationManager.isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        return try {
            transactionHelper.transaction {
                if (chatId != null) {
//                    userDescriptionRepository.setChatId(userId, chatId)
                }

                if (language != null) {
//                    userDescriptionRepository.setLanguage(userId, language)
                }

                if (canInputDifferentLanguages != null) {
//                    userDescriptionRepository.setCanInputDifferentLanguages(userId, canInputDifferentLanguages)
                }

                if (permissionGroup != null) {
//                    userDescriptionRepository.setPermissionGroup(userId, permissionGroup)
                }

//                userDescriptionRepository.findByIdOrNull(userId)
                null
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not update user with id '$userId'", e)
            null
        }
    }

    override fun findById(userId: Long): UserDescription? =
            try {
                null//userDescriptionRepository.findById(userId).orElse(null)
            } catch (e: Exception) {
                LOGGER.warn("Can not find user with id '$userId'", e)
                null
            }

    override fun findByIdWithData(userId: Long, additionalDataKeys: List<String>): UserDescription? =
            findById(userId)?.also { dto ->
                if (additionalDataKeys.isNotEmpty()) {
                    dto.additionalData = findAdditionalDataByUserId(userId, additionalDataKeys)
                }
            }

    override fun findAdditionalDataByUserId(userId: Long, additionalDataKeys: List<String>): Map<String, String> =
            try {
                emptyMap()
//                userAdditionalDataRepository.findAllByUserIdAndKeyIn(userId, additionalDataKeys)
//                        .filter { data -> data.key != null && data.value != null }
//                        .associate { data -> data.key!! to data.value!! }
            } catch (e: Exception) {
                LOGGER.warn("Can not get additional data for keys '$additionalDataKeys' and user with id '$userId'", e)
                emptyMap()
            }

    override fun setAdditionalDataByUserId(userId: Long, key: String, value: Any?): Boolean {
        if (value == null) {
//            userAdditionalDataRepository.removeAllByUserIdAndKey(userId, key)
            return true
        }

//        if (!userDescriptionRepository.existsById(userId)) {
//            return false
//        }

        return try {
            userAdditionalDataRepository.save(UserAdditionalData().also { data ->
//                data.userId = userId
//                data.key = key
                data.value = value.toString()
            })
            true
        } catch (e: Exception) {
            LOGGER.warn("Can not update additional data for user with id '$userId'", e)
            false
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultUserManager::class.java)
    }
}