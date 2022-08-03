package ru.loginov.serbian.bot.web.rest.category

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.loginov.serbian.bot.data.dto.category.CategoryDescription
import ru.loginov.serbian.bot.data.manager.category.CategoryManager

@RestController
@RequestMapping("/category")
class CategoryRestController(
        private val categoryManager: CategoryManager
) {

    @GetMapping("/category", produces = ["application/json"])
    fun rootCategories(): List<CategoryDescription> =
            categoryManager.getAllRootCategories()

}