package ru.loginov.serbian.bot.data.dto.permission

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class GroupPermissionDto {

    @Id
    var name: String? = null

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var rootNode: PermissionDto? = null

}