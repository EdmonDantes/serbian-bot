package ru.loginov.serbian.bot.data.manager.permission

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.loginov.serbian.bot.data.dto.permission.GroupPermissionDto
import ru.loginov.serbian.bot.data.dto.permission.PermissionDto
import ru.loginov.serbian.bot.data.dto.user.UserDto
import ru.loginov.serbian.bot.data.manager.permission.tree.PermissionTree
import ru.loginov.serbian.bot.data.manager.permission.tree.PermissionsMutation
import ru.loginov.serbian.bot.data.repository.permission.GroupPermissionDtoRepository
import ru.loginov.serbian.bot.data.repository.permission.PermissionNodeDtoRepository
import ru.loginov.serbian.bot.data.repository.user.UserDtoRepository
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.annotation.PostConstruct
import kotlin.concurrent.read
import kotlin.concurrent.write

@Component
class DefaultPermissionManager : PermissionManager {

    @Autowired
    @Qualifier("scheduler")
    private lateinit var scheduler: ScheduledExecutorService

    @Autowired
    private lateinit var groupPermissionDtoRepository: GroupPermissionDtoRepository

    @Autowired
    private lateinit var permissionNodeDtoRepository: PermissionNodeDtoRepository

    @Autowired
    private lateinit var permissionRegister: PermissionRegister

    @Autowired
    private lateinit var userDtoRepository: UserDtoRepository

    private lateinit var adminIds: List<Long>

    private val readWriteLock = ReentrantReadWriteLock()

    private var permissions: MutableMap<String, PermissionTree> = HashMap()

    @Value("\${bot.user.admin.ids}")
    fun setAdminIds(value: String?) {
        if (!value.isNullOrEmpty()) {
            adminIds = value.split(';').mapNotNull { it.toLongOrNull() }
            LOGGER.info("Admins ids = '$adminIds'")
        }
    }

    @PostConstruct
    fun start() {
        scheduler.scheduleAtFixedRate(this::reloadPermissions, 0, 60, TimeUnit.MINUTES)
    }

    override fun getPermissionsForGroup(name: String): PermissionOwner? =
            readWriteLock.read {
                permissions[name.lowercase()]
            }

    override fun getPermissionsForUser(user: UserDto): PermissionOwner? =
            if (adminIds.contains(user.id)) {
                PermissionOwner.ALL_PERMISSION
            } else {
                user.permissionGroup?.let { getPermissionsForGroup(it.lowercase()) }
            }

    override fun addPermissionForGroup(groupName: String, permission: String): Boolean {
        flush()
        val tree = readWriteLock.read { permissions[groupName.lowercase()] } ?: return false
        val mutations = tree.addPermission(permission.lowercase()) ?: return false
        try {
            return processMutations(mutations)
        } finally {
            flush()
        }
    }

    override fun deletePermissionForGroup(groupName: String, permission: String): Boolean {
        flush()
        val tree = readWriteLock.read { permissions[groupName.lowercase()] } ?: return false
        val mutations = tree.deletePermission(permission.lowercase()) ?: return false
        try {
            return processMutations(mutations)
        } finally {
            flush()
        }
    }

    override fun createGroup(name: String): Boolean {
        val group = GroupPermissionDto()
        group.name = name.lowercase()
        group.rootNode = PermissionDto().also {
            it.excluded = false
        }

        return try {
            groupPermissionDtoRepository.saveAndFlush(group)
            true
        } catch (e: Exception) {
            LOGGER.warn("Can not create group with name '$name'", e)
            false
        } finally {
            flush()
        }
    }

    override fun deleteGroup(name: String, forReplace: String?): Boolean {
        return try {
            userDtoRepository.replacePermissionGroup(name.lowercase(), forReplace?.lowercase())
            groupPermissionDtoRepository.deleteById(name.lowercase())
            true
        } catch (e: Exception) {
            LOGGER.warn("Can not delete group with name '${name.lowercase()}'", e)
            false
        } finally {
            flush()
        }
    }

    override fun getAllGroups(): List<GroupPermissionDto> = groupPermissionDtoRepository.findAll()

    override fun getAllPermissions(): List<String> = permissionRegister.getAllRegisteredPermissions()

    private fun flush() {
        if (readWriteLock.writeLock().tryLock()) {
            reloadPermissions()
        }
    }

    private fun reloadPermissions() {
        readWriteLock.write {
            permissions.clear()
            val nodes = permissionNodeDtoRepository.findAll().associateBy { it.id }
            val groups = groupPermissionDtoRepository.findAll()
            groups.forEach {
                it.name?.let { name ->
                    it.rootNode?.id?.let { rootNodeId ->
                        nodes[rootNodeId]?.let { rootNode ->
                            permissions[name.lowercase()] = PermissionTree(rootNode)
                        }
                    }
                }
            }
        }
    }

    private fun processMutations(mutations: PermissionsMutation): Boolean {
        try {
            permissionNodeDtoRepository.deleteAllById(mutations.forDelete)
        } catch (e: Exception) {
            LOGGER.error("Can not delete permissions with ids: '${mutations.forDelete}'", e)
            return false
        }


        try {
            permissionNodeDtoRepository.saveAll(mutations.forInsertOrUpdate)
        } catch (e: Exception) {
            LOGGER.error(
                    "Can not save or update permissions with ids: '${mutations.forInsertOrUpdate.map { it.id }}'",
                    e
            )
            return false
        }

        return true
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultPermissionManager::class.java)
    }
}