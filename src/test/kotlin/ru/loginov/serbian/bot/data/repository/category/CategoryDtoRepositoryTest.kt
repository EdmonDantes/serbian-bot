package ru.loginov.serbian.bot.data.repository.category

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootTest(classes = [CategoryDtoRepository::class])
@EnableJpaRepositories
@EnableAutoConfiguration
@EntityScan(basePackages = ["ru.loginov.serbian.bot.data.*"])
class CategoryDtoRepositoryTest {

    @Autowired
    private lateinit var repository: CategoryDtoRepository

    @Test
    public fun test() {
        Assertions.assertEquals(0, repository.findAll().size)
    }

}