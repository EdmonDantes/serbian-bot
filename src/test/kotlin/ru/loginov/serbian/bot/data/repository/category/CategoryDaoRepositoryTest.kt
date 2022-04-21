//package ru.loginov.serbian.bot.data.repository.category
//
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.boot.test.context.SpringBootTest
//import ru.loginov.serbian.bot.configuration.InMemoryJdbcConfiguration
//import ru.loginov.serbian.bot.data.dao.category.CategoryDao
//import ru.loginov.serbian.bot.data.dao.category.CategoryDaoLocalization
//import ru.loginov.serbian.bot.data.repository.search.AbstractSearchRepository
//import ru.loginov.serbian.bot.data.repository.search.SearchRepository
//
//@SpringBootTest(classes = [InMemoryJdbcConfiguration::class])
//class CategoryDaoRepositoryTest {
//
//    @Autowired
//    private lateinit var categoryDaoRepository: CategoryDaoRepository
//
//    @Autowired
//    private lateinit var categoryDaoLocalizationRepository: CategoryDaoLocalizationRepository
//
//    @Autowired
//    @Qualifier("categoryDaoLocalization")
//    private lateinit var categoryDaoLocalizationSearchRepository: SearchRepository<CategoryDaoLocalization>
//
//    @Test
//    fun test() {
//        val category = CategoryDao()
//
//        category.putLocalization("ru", "Категория")
//        category.putLocalization("en", "Category")
//
//        categoryDaoRepository.saveAndFlush(category);
//
//        val count = categoryDaoLocalizationSearchRepository.findAllByGeneralProperty("Category").count()
//        assertEquals(1, count)
//    }
//
//}