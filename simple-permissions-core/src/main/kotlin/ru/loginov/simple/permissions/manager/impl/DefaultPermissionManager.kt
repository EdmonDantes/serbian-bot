package ru.loginov.simple.permissions.manager.impl


import org.slf4j.LoggerFactory
import ru.loginov.simple.permissions.PermissionOwner
import ru.loginov.simple.permissions.entity.PermissionGroup
import ru.loginov.simple.permissions.entity.PermissionNode
import ru.loginov.simple.permissions.manager.PermissionManager
import ru.loginov.simple.permissions.storage.GroupReplacer
import ru.loginov.simple.permissions.storage.PermissionStorage
import ru.loginov.simple.permissions.storage.Storage
import ru.loginov.simple.permissions.storage.impl.EmptyGroupReplacer
import ru.loginov.simple.permissions.tree.NodeMutations
import ru.loginov.simple.permissions.tree.PermissionTree
import ru.loginov.simple.permissions.tree.PermissionTreeNode
import ru.loginov.simple.permissions.tree.impl.DefaultPermissionTree
import java.util.LinkedList
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class DefaultPermissionManager(
        private val permissionGroupStorage: Storage<String, PermissionGroup>,
        private val permissionNodeStorage: Storage<Int, PermissionNode>,
        private val permissionStorage: PermissionStorage,
        private val groupReplacer: GroupReplacer = EmptyGroupReplacer(),
        defaultGroupId: String? = null,
        superAdminGroupName: String? = null
) : PermissionManager {

    private val readWriteLock = ReentrantReadWriteLock()
    private val groupPermissions: MutableMap<String, PermissionTree> = HashMap()
    private val _groups: MutableSet<String> = HashSet()

    override val adminGroup: String? = superAdminGroupName?.lowercase()

    override val groups: List<String>
        get() = readWriteLock.read {
            ArrayList(_groups)
        }

    override val permissions: Collection<String>
        get() = permissionStorage.permissions

    override val defaultGroup: String?

    init {
        defaultGroup = initDefaultGroupId(defaultGroupId)

        permissionGroupStorage.findAll().forEach {
            it.name?.also { name ->
                _groups.add(name.lowercase())
            }
        }
    }

    private fun initDefaultGroupId(defaultGroupId: String?): String? {
        if (defaultGroupId.isNullOrBlank()) {
            return null
        }

        LOGGER.debug("Using group with name '$defaultGroupId' like default group")
        val group = try {
            permissionGroupStorage.findById(defaultGroupId.lowercase())
        } catch (e: Exception) {
            LOGGER.warn("Can not find group with name '$defaultGroupId'", e)
            return null
        }

        if (group != null) {
            return defaultGroupId
        }

        LOGGER.trace("Can not find group with name '$defaultGroupId' in storage. Will create")
        val rootNode =
                try {
                    permissionNodeStorage.save(PermissionNode(excluded = true))
                } catch (e: Exception) {
                    LOGGER.warn("Can not save new root node", e)
                    return null
                }

        if (rootNode?.id == null) {
            LOGGER.debug("Can not create root node for group with name '$defaultGroupId'")
            return null
        }

        return try {
            permissionGroupStorage.save(PermissionGroup(defaultGroupId.lowercase(), rootNode.id))
            LOGGER.debug("Group with name '$defaultGroupId' was be created")
            defaultGroupId
        } catch (e: Exception) {
            LOGGER.warn("Can not create new group with name '${defaultGroupId.lowercase()}'", e)
            null
        }
    }

    override fun hasPermission(permission: String): Boolean = permissionStorage.hasPermission(permission)

    override fun addPermissionForGroup(groupName: String, permission: String): Boolean {
        val prepareGroupName = groupName.lowercase()
        val preparePermission = permission.lowercase()

        if (prepareGroupName == adminGroup) {
            return true
        }

        if (!_groups.contains(prepareGroupName) || !permissionStorage.hasPermission(preparePermission)) {
            return false
        }

        reloadGroup(prepareGroupName)
        val tree = readWriteLock.read { groupPermissions[prepareGroupName] } ?: return false
        val mutations = tree.addPermission(preparePermission) ?: return false
        try {
            return processMutations(mutations).also {
                if (it) {
                    LOGGER.debug("Successfully added permission '$preparePermission' to group '$prepareGroupName'")
                } else {
                    LOGGER.debug("Failed to added permission '$preparePermission' to group '$prepareGroupName'")
                }
            }
        } finally {
            reloadGroup(prepareGroupName)
        }
    }

    override fun deletePermissionForGroup(groupName: String, permission: String): Boolean {
        val prepareGroupName = groupName.lowercase()
        val preparePermission = permission.lowercase()

        if (prepareGroupName == adminGroup) {
            return false
        }

        if (!_groups.contains(prepareGroupName) || !permissionStorage.hasPermission(preparePermission)) {
            return false
        }

        reloadGroup(prepareGroupName)
        val tree = readWriteLock.read { groupPermissions[prepareGroupName] } ?: return false
        val mutations = tree.deletePermission(preparePermission) ?: return false
        try {
            return processMutations(mutations).also {
                if (it) {
                    LOGGER.debug("Successfully delete permission '$preparePermission' to group '$prepareGroupName'")
                } else {
                    LOGGER.debug("Failed to delete permission '$preparePermission' to group '$prepareGroupName'")
                }
            }
        } finally {
            reloadGroup(prepareGroupName)
        }
    }

    override fun hasGroup(name: String): Boolean =
            name.lowercase() == adminGroup?.lowercase()
                    ||
                    readWriteLock.read {
                        groupPermissions.containsKey(name.lowercase())
                    }

    override fun getOwnerForGroup(groupName: String): PermissionOwner? =
            if (groupName.lowercase() == adminGroup?.lowercase()) {
                PermissionOwner.ALL_PERMISSION
            } else {
                readWriteLock.read {
                    groupPermissions[groupName.lowercase()]
                }
            }

    override fun getOwnerForGroupOrDefault(groupName: String?): PermissionOwner? =
            groupName?.let { getOwnerForGroup(it) } ?: defaultGroup?.let { getOwnerForGroup(it) }


    override fun createGroup(name: String): Boolean {
        val prepareGroupName = name.lowercase()

        if (prepareGroupName == adminGroup || prepareGroupName == defaultGroup) {
            return false
        }

        readWriteLock.write {
            reloadGroup(prepareGroupName)
            if (groupPermissions.containsKey(prepareGroupName)) {
                return false
            }

            val rootNode = try {
                permissionNodeStorage.save(PermissionNode(excluded = true))
            } catch (e: Exception) {
                LOGGER.warn("Can not create root node for new group", e)
                return false
            }

            if (rootNode?.id == null) {
                error("Can not create root node for new group. Root node id is null")
            }

            return try {
                permissionGroupStorage.save(PermissionGroup(prepareGroupName, rootNode.id))
                LOGGER.debug("Successfully created new group with name '$prepareGroupName'")
                true
            } catch (e: Exception) {
                LOGGER.warn("Can not create group with name '$prepareGroupName'", e)
                false
            } finally {
                reloadGroup(prepareGroupName)
            }
        }
    }

    override fun deleteGroup(name: String, forReplace: String?): Boolean {
        val prepareGroupName = name.lowercase()
        val prepareForReplace = forReplace?.lowercase()

        if (prepareGroupName == defaultGroup || prepareGroupName == adminGroup) {
            return false
        }

        val replaced = if (prepareForReplace != null) {
            try {
                groupReplacer.replace(prepareGroupName, prepareForReplace)
            } catch (e: Exception) {
                LOGGER.warn(
                        "Can not replace group with name '$name' to '$forReplace'." +
                                if (defaultGroup != null) "Will try to replace by default group with name '${defaultGroup}'" else "",
                        e
                )
                false
            }
        } else false

        if (!replaced && defaultGroup != null) {
            try {
                groupReplacer.replace(prepareGroupName, defaultGroup)
            } catch (e: Exception) {
                LOGGER.error(
                        "Can not replace group with name '$name' to default group with name '${defaultGroup}'",
                        e
                )
            }
        }

        return try {
            permissionGroupStorage.deleteById(prepareGroupName)
            LOGGER.debug("Successfully deleted group with name '${prepareGroupName}'")
            true
        } catch (e: Exception) {
            LOGGER.warn("Can not delete group with name '${prepareGroupName}'", e)
            false
        } finally {
            reloadGroup(prepareGroupName)
        }
    }

    override fun flush(force: Boolean, timeout: Long, unit: TimeUnit) {
        val writeLock = readWriteLock.writeLock()

        if (force) {
            if (!writeLock.tryLock(timeout, unit)) {
                return
            }
        } else if (!writeLock.tryLock()) {
            return
        }

        try {
            groupPermissions.clear()
            _groups.clear()
            permissionGroupStorage.findAll().forEach(::reloadGroup)
        } catch (e: Exception) {
            LOGGER.warn("Can not update data from storage", e)
        } finally {
            writeLock.unlock()
        }
    }

    private fun reloadGroup(name: String): Boolean {
        val groupName = name.lowercase()

        readWriteLock.write {
            val group = permissionGroupStorage.findById(groupName) ?: return false
            return reloadGroup(group).also {
                if (!it)
                    _groups.remove(groupName)
            }
        }
    }

    private fun reloadGroup(group: PermissionGroup): Boolean {
        if (group.name == null) {
            return false
        }
        val groupName = group.name!!.lowercase()

        readWriteLock.write {
            val rootNode = permissionNodeStorage.findById(group.rootNodeId ?: return false) ?: return false
            groupPermissions[groupName] = DefaultPermissionTree(permissionNodeStorage, rootNode)
            _groups.add(groupName)
            return true
        }
    }

    private fun processMutations(mutations: NodeMutations): Boolean {
        try {
            permissionNodeStorage.deleteAllByIds(mutations.forDelete)
        } catch (e: Exception) {
            LOGGER.error("Can not delete permissions with ids: '${mutations.forDelete}'", e)
            return false
        }

        val rootNode = mutations.forInsertOrUpdate ?: return true
        val nodes = LinkedList<PermissionTreeNode>()
        nodes.add(rootNode)

        while (nodes.isNotEmpty()) {
            val node = nodes.removeFirst()
            val entity = node.entity

            if (entity.excluded == null) {
                entity.excluded = false
            }

            val newEntity =
                    try {
                        permissionNodeStorage.save(entity)
                    } catch (e: Exception) {
                        LOGGER.error("Can not save or update permission node: '$entity'", e)
                        return false
                    }

            if (newEntity == null) {
                LOGGER.warn("Can not save or update permission node: '$entity'")
                return false
            }

            moveDataToAnotherNode(newEntity, entity)

            node.children.forEach { (_, child) ->
                child.entity.parentId = newEntity.id
                nodes.addLast(child)
            }
        }

        nodes.add(rootNode)

        while (nodes.isNotEmpty()) {
            val node = nodes.removeFirst()
            val entity = node.entity
            val children = entity.childrenIds?.toMutableList() ?: ArrayList()

            node.children.forEach { (_, child) ->
                if (child.entity.id != null) {
                    children.add(child.entity.id!!)
                    nodes.addLast(child)
                }
            }

            entity.childrenIds = children

            try {
                permissionNodeStorage.save(entity)
            } catch (e: Exception) {
                LOGGER.warn("Can not save or update permission node: '$entity'")
                return false
            }
        }

        return true
    }

    private fun moveDataToAnotherNode(from: PermissionNode, to: PermissionNode) {
        to.id = from.id
        to.value = from.value
        to.excluded = from.excluded
        to.childrenIds = from.childrenIds
        to.parentId = from.parentId
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultPermissionManager::class.java)
    }
}