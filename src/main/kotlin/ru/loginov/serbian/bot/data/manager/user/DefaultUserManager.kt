package ru.loginov.serbian.bot.data.manager.user

import io.github.edmondantes.simple.localization.Localizer
import io.github.edmondantes.simple.localization.exception.LanguageNotSupportedException
import io.github.edmondantes.simple.permissions.manager.PermissionManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.user.UserDataDto
import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.data.repository.user.UserDataDtoRepository
import ru.loginov.serbian.bot.data.repository.user.UserDtoRepository
import ru.loginov.serbian.bot.util.transaction.TransactionHelper

@Service
class DefaultUserManager(
        private val userDtoRepository: UserDtoRepository,
        private val userDataDtoRepository: UserDataDtoRepository,
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
    ): UserDto? {
        val user = UserDto()
        user.id = userId
        user.chatId = chatId
        user.language =
                if (language == null || !localizationManager.isSupport(language))
                    localizationManager.defaultLanguage
                else
                    language
        user.canInputDifferentLanguages = canInputDifferentLanguages ?: false
        user.permissionGroup = permissionGroup
                ?: (if (adminIds.contains(userId)) permissionManager.adminGroup else null)
                        ?: permissionManager.defaultGroup

        return try {
            userDtoRepository.save(user)
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
    ): UserDto? {
        if (language != null && !localizationManager.isSupport(language)) {
            throw LanguageNotSupportedException(language)
        }

        return try {
            transactionHelper.transaction {
                if (chatId != null) {
                    userDtoRepository.setChatId(userId, chatId)
                }

                if (language != null) {
                    userDtoRepository.setLanguage(userId, language)
                }

                if (canInputDifferentLanguages != null) {
                    userDtoRepository.setCanInputDifferentLanguages(userId, canInputDifferentLanguages)
                }

                if (permissionGroup != null) {
                    userDtoRepository.setPermissionGroup(userId, permissionGroup)
                }

                userDtoRepository.findByIdOrNull(userId)
            }
        } catch (e: Exception) {
            LOGGER.warn("Can not update user with id '$userId'", e)
            null
        }
    }

    override fun findById(userId: Long): UserDto? =
            try {
                userDtoRepository.findById(userId).orElse(null)
            } catch (e: Exception) {
                LOGGER.warn("Can not find user with id '$userId'", e)
                null
            }

    override fun findByIdWithData(userId: Long, additionalDataKeys: List<String>): UserDto? =
            findById(userId)?.also { dto ->
                if (additionalDataKeys.isNotEmpty()) {
                    dto.additionalData = findAdditionalDataByUserId(userId, additionalDataKeys)
                }
            }

    override fun findAdditionalDataByUserId(userId: Long, additionalDataKeys: List<String>): Map<String, String> =
            try {
                userDataDtoRepository.findAllByUserIdAndKeyIn(userId, additionalDataKeys)
                        .filter { data -> data.key != null && data.value != null }
                        .associate { data -> data.key!! to data.value!! }
            } catch (e: Exception) {
                LOGGER.warn("Can not get additional data for keys '$additionalDataKeys' and user with id '$userId'", e)
                emptyMap()
            }

    override fun setAdditionalDataByUserId(userId: Long, key: String, value: Any?): Boolean {
        if (value == null) {
            userDataDtoRepository.removeAllByUserIdAndKey(userId, key)
            return true
        }

        if (!userDtoRepository.existsById(userId)) {
            return false
        }

        return try {
            userDataDtoRepository.save(UserDataDto().also { data ->
                data.userId = userId
                data.key = key
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