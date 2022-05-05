package ru.loginov.serbian.bot.data.dto.permission

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class PermissionDto {

    @Id
    @GeneratedValue
    var id: Int? = null

    @Column
    var value: String? = null

    @Column(nullable = false)
    var excluded: Boolean? = null

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", orphanRemoval = true)
    var children: List<PermissionDto> = emptyList()

    @ManyToOne
    var parent: PermissionDto? = null

}