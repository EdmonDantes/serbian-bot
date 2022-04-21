//package ru.loginov.serbian.bot.telegram
//
//import kotlinx.coroutines.runBlocking
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import ru.loginov.serbian.bot.telegram.service.DefaultTelegramService
//
//@SpringBootTest(classes = [DefaultTelegramService::class])
//class TelegramServiceTest {
//
//    @Autowired
//    private lateinit var telegramService: DefaultTelegramService
//
//    @Test
//    fun test() {
//        val user = runBlocking {
//            telegramService.getMe()
//        }
//
//        println(user)
//    }
//
//}