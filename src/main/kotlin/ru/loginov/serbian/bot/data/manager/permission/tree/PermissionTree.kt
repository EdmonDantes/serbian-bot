package ru.loginov.serbian.bot.data.manager.permission.tree

import ru.loginov.serbian.bot.data.dto.permission.PermissionDto
import ru.loginov.serbian.bot.data.manager.permission.PermissionOwner

class PermissionNodeTree {

    val parent: PermissionNodeTree?
    val id: Int
    val children: Map<String, PermissionNodeTree>
    val excluded: Boolean

    constructor(
            parent: PermissionNodeTree? = null,
            id: Int,
            children: Map<String, PermissionNodeTree>,
            excluded: Boolean
    ) {
        this.parent = parent
        this.id = id
        this.children = children
        this.excluded = excluded
    }

    private constructor(
            id: Int,
            parent: PermissionNodeTree? = null,
            children: Map<String, PermissionNodeTreeBuilder>,
            excluded: Boolean
    ) {
        this.parent = parent
        this.id = id
        this.children = children.mapValues { it.value.also { it.parent = this }.build() }
        this.excluded = excluded
    }

    fun getNextNode(part: String): PermissionNodeTree? = children[part]

    fun getAllPermissionNode(): PermissionNodeTree? = children[PermissionTree.ALL_PERMISSION_STRING]

    fun getNextNode(permission: List<String>, index: Int = 0): Pair<Boolean, PermissionNodeTree>? {
        if (index < 0 || index >= permission.size) {
            return null
        }

        val key = permission[index]
        val child = children[key]

        return if (child != null) {
            false to child
        } else {
            children[PermissionTree.ALL_PERMISSION_STRING]?.let { true to it }
        }
    }

    fun toIdentifyDto(): PermissionDto = PermissionDto().also {
        it.id = id
    }

    fun toDto(): PermissionDto {
        val dto = PermissionDto()
        dto.id = id
        dto.excluded = excluded
        dto.parent = parent?.let { it.toIdentifyDto() }
        return dto
    }

    class PermissionNodeTreeBuilder {
        var parent: PermissionNodeTree? = null
        var id: Int? = null
        var children: MutableMap<String, PermissionNodeTreeBuilder> = HashMap()
        var excluded: Boolean = false

        fun build(): PermissionNodeTree {
            return PermissionNodeTree(
                    id ?: error("Id can not be null"),
                    parent,
                    children,
                    excluded
            )
        }
    }
}

class PermissionsMutation(
        val forInsertOrUpdate: List<PermissionDto> = emptyList(),
        val forDelete: List<Int> = emptyList()
) {
    constructor(forInsertOrUpdate: PermissionDto) : this(listOf(forInsertOrUpdate))
}

class PermissionTree(rootNode: PermissionDto) : PermissionOwner {

    private val root: PermissionNodeTree

    init {
        val rootBuilder = PermissionNodeTree.PermissionNodeTreeBuilder()
        fillBuilder(rootBuilder, rootNode)
        this.root = rootBuilder.build()
    }

    fun addPermission(permission: String): PermissionsMutation? {
        val parts = splitPermissionToParts(permission) ?: return null
        var index = 0
        var currentNode: PermissionNodeTree = root
        while (index < parts.size) {
            val part = parts[index]

            if (part == ALL_PERMISSION_STRING) {
                val nextNode = currentNode.getAllPermissionNode()
                val forRemove = currentNode.children
                        .mapNotNull { if (nextNode?.id == it.value.id) null else it.value.id }
                return if (nextNode == null || nextNode.excluded) {
                    val parentDto = currentNode.toDto()
                    val dto = PermissionDto()
                    dto.parent = parentDto
                    dto.id = nextNode?.id
                    dto.excluded = false
                    dto.children = emptyList()
                    parentDto.children = listOf(dto)
                    PermissionsMutation(listOf(parentDto, dto), forRemove)
                } else {
                    PermissionsMutation(emptyList(), forRemove)
                }
            }

            val nextNode = currentNode.getNextNode(part)
            if (nextNode == null) {
                val allPermissionPart = currentNode.getAllPermissionNode() ?: break
                if (allPermissionPart.excluded) {
                    break
                }

                return PermissionsMutation()
            }

            currentNode = nextNode
            index++
        }

        if (index >= parts.size) {
            return if (currentNode.excluded) {
                val enablePermissionDto = PermissionDto()
                enablePermissionDto.id = currentNode.id
                enablePermissionDto.excluded = false
                PermissionsMutation(enablePermissionDto)
            } else {
                PermissionsMutation()
            }
        }

        val result = ArrayList<PermissionDto>()
        val startDto = PermissionDto()
        startDto.parent = currentNode.toIdentifyDto()
        startDto.value = parts[index++]
        startDto.excluded = false
        result.add(startDto)

        var dto = startDto
        while (index < parts.size) {
            val localDto = PermissionDto()
            localDto.parent = dto
            localDto.excluded = false
            localDto.value = parts[index++]
            dto.children = listOf(localDto)
            dto = localDto
            result.add(localDto)
        }

        return PermissionsMutation(result)
    }

    fun deletePermission(permission: String): PermissionsMutation? {
        val parts = splitPermissionToParts(permission) ?: return null
        if (!isValidParts(parts)) {
            return null
        }
        var index = 0
        var currentNode: PermissionNodeTree = root
        while (index < parts.size) {
            val part = parts[index]

            if (part == ALL_PERMISSION_STRING) {
                val nextNode = currentNode.getAllPermissionNode()
                val forRemove = currentNode.children
                        .mapNotNull { if (nextNode?.id == it.value.id) null else it.value.id }
                return if (nextNode == null || nextNode.excluded) {
                    val parentDto = currentNode.toDto()
                    val dto = PermissionDto()
                    dto.parent = parentDto
                    dto.id = nextNode?.id
                    dto.excluded = true
                    dto.children = emptyList()
                    parentDto.children = listOf(dto)
                    PermissionsMutation(listOf(parentDto, dto), forRemove)
                } else {
                    PermissionsMutation(emptyList(), forRemove)
                }
            }

            val nextNode = currentNode.getNextNode(part)
            if (nextNode == null) {
                val allPermissionPart = currentNode.getAllPermissionNode() ?: break
                if (!allPermissionPart.excluded) {
                    break
                }

                return PermissionsMutation()
            }

            currentNode = nextNode
            index++
        }

        if (index >= parts.size) {
            return if (!currentNode.excluded) {
                val enablePermissionDto = PermissionDto()
                enablePermissionDto.id = currentNode.id
                enablePermissionDto.excluded = true
                PermissionsMutation(enablePermissionDto)
            } else {
                PermissionsMutation()
            }
        }

        val result = ArrayList<PermissionDto>()
        val startDto = PermissionDto()
        startDto.parent = currentNode.toIdentifyDto()
        startDto.value = parts[index++]
        startDto.excluded = true
        result.add(startDto)

        var dto = startDto
        while (index < parts.size) {
            val localDto = PermissionDto()
            localDto.parent = dto
            localDto.excluded = true
            localDto.value = parts[index++]
            dto.children = listOf(localDto)
            dto = localDto
            result.add(localDto)
        }

        return PermissionsMutation(result)
    }

    override fun havePermission(permission: String): Boolean {
        val parts = splitPermissionToParts(permission) ?: return false
        var currentNode: PermissionNodeTree = root
        var index = 0
        while (index < parts.size) {
            val part = parts[index++]
            val nextNode = currentNode.getNextNode(part)
            if (nextNode == null) {
                val allPermissionNode = currentNode.getAllPermissionNode()
                if (allPermissionNode == null || allPermissionNode.excluded) {
                    return false
                }

                return true
            }

            currentNode = nextNode
        }

        return !currentNode.excluded
    }

    private fun fillBuilder(builder: PermissionNodeTree.PermissionNodeTreeBuilder, node: PermissionDto) {
        builder.id = node.id
        builder.excluded = node.excluded ?: false

        node.children.forEach {
            if (it.value != null) {
                val childBuilder = PermissionNodeTree.PermissionNodeTreeBuilder()
                builder.children[it.value!!] = childBuilder
                fillBuilder(childBuilder, it)
            }
        }
    }

    companion object {
        const val SPLIT_STRING = "."
        const val ALL_PERMISSION_STRING = "*"

        fun isValidPermission(permission: String): Boolean = splitPermissionToParts(permission)
                ?.let { isValidParts(it) }
                ?: false

        private fun splitPermissionToParts(str: String): List<String>? =
                if (str.isEmpty()) null else str.split(SPLIT_STRING)

        private fun isValidParts(parts: List<String>): Boolean =
                parts.all { part ->
                    if (part.isEmpty()) {
                        false
                    } else {
                        for (i in part.indices) {
                            val ch = part[i]
                            if (ch !in 'a'..'z' && ch !in 'A'..'Z'
                                    || ch == '*' && i != part.lastIndex) {
                                return@all false
                            }
                        }
                        true
                    }
                }
    }
}