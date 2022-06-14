package ru.loginov.simple.permissions.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.loginov.simple.permissions.entity.PermissionGroup
import ru.loginov.simple.permissions.entity.PermissionNode
import ru.loginov.simple.permissions.manager.PermissionManager
import ru.loginov.simple.permissions.manager.impl.DefaultPermissionManager
import ru.loginov.simple.permissions.permission.PermissionValidator
import ru.loginov.simple.permissions.permission.impl.DefaultPermissionValidator
import ru.loginov.simple.permissions.spring.data.repository.JpaPermissionGroupStorage
import ru.loginov.simple.permissions.spring.data.repository.JpaPermissionNodeStorage
import ru.loginov.simple.permissions.storage.GroupReplacer
import ru.loginov.simple.permissions.storage.PermissionStorage
import ru.loginov.simple.permissions.storage.Storage
import ru.loginov.simple.permissions.storage.impl.EmptyGroupReplacer

@Configuration
@EnableJpaRepositories(basePackages = ["ru.loginov.simple.permissions.spring.data.repository"])
@EntityScan(basePackages = ["ru.loginov.simple.permissions.spring.data.*"])
open class SimplePermissionConfiguration {

    @Value("\${spring.simple.permission.default.group.name:}")
    private var defaultGroupName: String? = null

    @Value("\${spring.simple.permission.super.user.group.name:}")
    private var superUserGroupName: String? = null

    @Bean
    open fun permissionValidator(): PermissionValidator = DefaultPermissionValidator()

    @Bean
    open fun groupReplacer(): GroupReplacer = EmptyGroupReplacer()

    @Bean
    open fun permissionGroupStorage(): Storage<String, PermissionGroup> = JpaPermissionGroupStorage()

    @Bean
    open fun permissionNodeStorage(): Storage<Int, PermissionNode> = JpaPermissionNodeStorage()

    @Bean
    open fun permissionManager(
            permissionGroupStorage: Storage<String, PermissionGroup>,
            permissionNodeStorage: Storage<Int, PermissionNode>,
            permissionStorage: PermissionStorage,
            groupReplacer: GroupReplacer
    ): PermissionManager = DefaultPermissionManager(
            permissionGroupStorage,
            permissionNodeStorage,
            permissionStorage,
            groupReplacer,
            defaultGroupName,
            superUserGroupName
    )

}