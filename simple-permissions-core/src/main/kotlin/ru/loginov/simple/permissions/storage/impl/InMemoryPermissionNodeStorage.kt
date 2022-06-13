package ru.loginov.simple.permissions.storage.impl

import ru.loginov.simple.permissions.entity.PermissionNode
import java.util.concurrent.atomic.AtomicInteger

class InMemoryPermissionNodeStorage : AbstractInMemoryStorage<Int, PermissionNode>() {

    private val nextId = AtomicInteger(1)

    override fun getIdFromObj(obj: PermissionNode): Int? {
        val objId = obj.id
        if (objId != null) {
            nextId.updateAndGet { if (it < objId) objId else it }
        }
        return objId
    }

    override fun generateIdForObj(obj: PermissionNode): Int {
        val id = nextId.getAndIncrement()
        obj.id = id
        return id
    }
}