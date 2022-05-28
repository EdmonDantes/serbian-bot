package ru.loginov.serbian.bot.data.manager.permission

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultPermissionRegister : PermissionRegister {

    private val register = HashSet<String>()

    override val permissions: List<String>
        get() = register.toList()

    override fun hasPermission(permission: String) =
            register.contains(permission)

    override fun registerPermission(permission: String) {
        val added = synchronized(register) {
            register.add(permission)
        }
        if (added) {
            LOGGER.debug("Registered permission: '$permission'")
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultPermissionRegister::class.java)
    }
}