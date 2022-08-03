package ru.loginov.simple.permissions.configuration

import io.github.edmondantes.simple.permissions.data.storage.GroupReplacer
import io.github.edmondantes.simple.permissions.data.storage.PermissionGroupStorage
import io.github.edmondantes.simple.permissions.data.storage.PermissionNodeStorage
import io.github.edmondantes.simple.permissions.data.storage.PermissionStorage
import io.github.edmondantes.simple.permissions.data.storage.impl.EmptyGroupReplacer
import io.github.edmondantes.simple.permissions.manager.PermissionManager
import io.github.edmondantes.simple.permissions.manager.impl.DefaultPermissionManager
import io.github.edmondantes.simple.permissions.tree.mapper.impl.DefaultPermissionMapperToNodeValue
import io.github.edmondantes.simple.permissions.validator.PermissionValidator
import io.github.edmondantes.simple.permissions.validator.impl.DefaultPermissionValidator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["ru.loginov.simple.permissions.spring.data.repository"])
@EntityScan(basePackages = ["ru.loginov.simple.permissions.spring.data.*"])
open class SimplePermissionConfiguration {

    @Value("\${spring.simple.permission.default.group.name:}")
    private var defaultGroupName: String? = null

    @Value("\${spring.simple.permission.super.user.group.name:}")
    private var superUserGroupName: String? = null

    @Bean
    @Qualifier("permissionValidator")
    open fun permissionValidator(): PermissionValidator = DefaultPermissionValidator()

    @Bean
    @Qualifier("groupReplacer")
    open fun groupReplacer(): GroupReplacer = EmptyGroupReplacer()

    @Bean
    @Qualifier("permissionManager")
    open fun permissionManager(
            @Qualifier("permissionGroupStorage") permissionGroupStorage: PermissionGroupStorage,
            @Qualifier("permissionNodeStorage") permissionNodeStorage: PermissionNodeStorage,
            @Qualifier("permissionStorage") permissionStorage: PermissionStorage,
            @Qualifier("groupReplacer") groupReplacer: GroupReplacer
    ): PermissionManager = DefaultPermissionManager(
            permissionGroupStorage,
            permissionNodeStorage,
            permissionStorage,
            groupReplacer,
            DefaultPermissionMapperToNodeValue(),
            defaultGroupName,
            superUserGroupName
    )

}