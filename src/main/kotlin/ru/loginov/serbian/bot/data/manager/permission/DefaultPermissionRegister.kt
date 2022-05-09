package ru.loginov.serbian.bot.data.manager.permission

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultPermissionRegister : PermissionRegister {

    private val register = HashSet<String>()

    override fun getAllRegisteredPermissions(): List<String> {
        synchronized(register) {
            return register.toList()
        }
    }

    override fun registerPermission(permission: String) {
        synchronized(register) {
            register.add(permission)
        }
        LOGGER.debug("Registered permission: '$permission'")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultPermissionRegister::class.java)
    }
}