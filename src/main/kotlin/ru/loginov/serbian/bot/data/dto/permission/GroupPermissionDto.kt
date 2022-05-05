package ru.loginov.serbian.bot.data.dto.permission

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
//@Table(name = "groups")
class GroupPermissionDto {

    @Id
    @GeneratedValue
    var id: Int? = null

    @Column(nullable = false, unique = true)
    var name: String? = null

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var rootNode: PermissionDto? = null

}