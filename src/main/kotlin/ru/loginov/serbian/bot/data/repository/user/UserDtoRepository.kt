package ru.loginov.serbian.bot.data.repository.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.serbian.bot.data.dto.user.UserDto

@Repository
interface UserDtoRepository : JpaRepository<UserDto, Long> {}