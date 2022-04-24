package ru.loginov.serbian.bot.data.manager.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.loginov.serbian.bot.data.dto.user.UserDataDto
import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.data.repository.user.UserDataDtoRepository
import ru.loginov.serbian.bot.data.repository.user.UserDtoRepository

@Service
class DefaultUserManager : UserManager {

    @Autowired
    private lateinit var userDtoRepository: UserDtoRepository

    @Autowired
    private lateinit var userDataDtoRepository: UserDataDtoRepository
    override fun createUser(
            userId: Long,
            charId: Long,
            language: String?
    ): UserDto? = userDtoRepository.save(UserDto().also { user ->
        user.id = userId
        user.chatId = charId
        user.language = language
    })


    override fun getUser(userId: Long): UserDto? = userDtoRepository.findById(userId).orElse(null)

    override fun getUserWithData(userId: Long, additionalDataKeys: List<String>): UserDto? =
            getUser(userId)?.also { dto ->
                if (additionalDataKeys.isNotEmpty()) {
                    dto.additionalData = getAdditionalData(userId, additionalDataKeys)
                }
            }

    override fun getAdditionalData(userId: Long, additionalDataKeys: List<String>): Map<String, String> =
            userDataDtoRepository.findAllByUserIdAndKeyIn(userId, additionalDataKeys)
                    .filter { data -> data.key != null && data.value != null }
                    .associate { data -> data.key!! to data.value!! }

    override fun setAdditionalData(userId: Long, key: String, value: Any?) {

        if (value == null) {
            userDataDtoRepository.removeAllByUserIdAndKey(userId, key)
            return
        }

        userDataDtoRepository.save(UserDataDto().also { data ->
            data.userId = userId
            data.key = key
            data.value = value.toString()
        })
    }
}