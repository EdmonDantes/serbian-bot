//package ru.loginov.simple.permissions.storage.impl
//
//import ru.loginov.simple.permissions.entity.PermissionGroup
//import ru.loginov.simple.permissions.entity.PermissionNode
//import ru.loginov.simple.permissions.storage.PermissionRepository
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.atomic.AtomicInteger
//
//class InMemoryPermissionRepository : PermissionRepository {
//
//    private val groups = ConcurrentHashMap<String, PermissionGroup>()
//    private val nodes = ConcurrentHashMap<Int, PermissionNode>()
//    private val nodesParents = ConcurrentHashMap<Int, Int>()
//    private val nodeId = AtomicInteger(1)
//
//    override fun save(group: PermissionGroup): PermissionGroup? =
//            if (group.name == null) {
//                null
//            } else {
//                val rootNode = if (group.rootNode?.id == null) {
//                    save(group.rootNode ?: PermissionNode())
//                } else {
//                    group.rootNode
//                }
//
//                val result = PermissionGroup(group.name, rootNode)
//                groups[group.name!!.lowercase()] = result
//                result
//            }
//
//    override fun save(node: PermissionNode): PermissionNode? {
//        val result = PermissionNode(
//                node.id ?: nodeId.getAndIncrement(),
//                node.value,
//                node.excluded ?: false,
//                node.parent?.id?.let { nodes[it] },
//                null,
//        )
//
//        result.children = node.children?.mapNotNull { it.parent = result; save(it) } ?: emptyList()
//        result.children?.forEach { child ->
//            val prevNode = nodesParents.put(child.id!!, result.id!!)
//            if (prevNode != null && prevNode != result.id) {
//                nodes[prevNode]?.also {
//                    it.children = it.children?.filter { it.id != child.id }
//                }
//            }
//        }
//
//        nodes[result.id!!] = result
//
//        val parentId = nodesParents[result.id]
//        if (parentId != null) {
//            nodes[parentId]?.also {
//                it.children = it.children?.map { if (it.id == result.id) result else it }
//            }
//        }
//
//        return result
//    }
//
//    override fun saveAllGroups(groups: List<PermissionGroup>): List<PermissionGroup> =
//            groups.mapNotNull { save(it) }
//
//    override fun saveAllNodes(nodes: List<PermissionNode>): List<PermissionNode> =
//            nodes.mapNotNull { save(it) }
//
//    override fun deleteByIdGroup(groupName: String) {
//        groups.remove(groupName.lowercase())
//    }
//
//    override fun deleteAllByIdNodes(nodes: List<Int>) {
//        nodes.forEach {
//            this.nodes.remove(it)
//        }
//    }
//
//    override fun findAllGroups(): List<PermissionGroup> = groups.values.toList()
//
//    override fun findAllNodes(): List<PermissionNode> = nodes.values.toList()
//
//    override fun findByIdGroup(groupName: String): PermissionGroup? = groups[groupName]
//
//    override fun replaceGroup(oldGroup: String, newGroup: String): Boolean {
//        return true
//    }
//}