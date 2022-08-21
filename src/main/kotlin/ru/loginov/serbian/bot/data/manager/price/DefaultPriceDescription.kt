//package ru.loginov.serbian.bot.data.manager.price
//
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Service
//import ru.loginov.serbian.bot.data.dto.price.PriceDescription
//import ru.loginov.serbian.bot.util.useSuspend
//
//@Service
//class DefaultPriceDescription(
//        //private val priceDescriptionRepository: PriceDescriptionRepository
//) : PriceDescriptionManager {
//
//    override suspend fun findAllForCategory(categoryId: Int): List<PriceDescription> =
//            priceDescriptionRepository.useSuspend {
//                it.findAllByCategoryId(categoryId)
//            }
//
//
//    override suspend fun findAllForShop(shopId: Int): List<PriceDescription> =
//            priceDescriptionRepository.useSuspend {
//                it.findAllByShopId(shopId)
//            }
//
//    override suspend fun findAllForProduct(productId: Int): List<PriceDescription> =
//            priceDescriptionRepository.useSuspend {
//                it.findAllByProductId(productId)
//            }
//
//    override suspend fun findForCategory(categoryId: Int): PriceDescription? =
//            priceDescriptionRepository.useSuspend {
//                it.findByCategoryIdAndShopIdAndProductId(categoryId, null, null).orElse(null)
//                        ?: updatePriceDescription(it.findAllByCategoryId(categoryId), categoryId = categoryId)
//            }
//
//    override suspend fun findForShop(shopId: Int): PriceDescription? =
//            priceDescriptionRepository.useSuspend {
//                it.findByCategoryIdAndShopIdAndProductId(null, shopId, null).orElse(null)
//                        ?: updatePriceDescription(it.findAllByShopId(shopId), shopId = shopId)
//            }
//
//    override suspend fun findForProduct(productId: Int): PriceDescription? =
//            priceDescriptionRepository.useSuspend {
//                it.findByCategoryIdAndShopIdAndProductId(null, null, productId).orElse(null)
//                        ?: updatePriceDescription(it.findAllByProductId(productId), productId = productId)
//            }
//
//    override suspend fun findForShopAndCategory(shopId: Int, categoryId: Int): PriceDescription? =
//            priceDescriptionRepository.useSuspend {
//                it.findByCategoryIdAndShopIdAndProductId(categoryId, shopId, null).orElse(null)
//            }
//
//    override suspend fun findForShopAndProduct(shopId: Int, productId: Int): PriceDescription? =
//            priceDescriptionRepository.useSuspend {
//                it.findByCategoryIdAndShopIdAndProductId(null, shopId, productId).orElse(null)
//            }
//
//
//    private fun updatePriceDescription(
//            descriptions: List<PriceDescription>,
//            categoryId: Int? = null,
//            shopId: Int? = null,
//            productId: Int? = null
//    ): PriceDescription? =
//            descriptions
//                    .filter { it.minPrice != null && it.maxPrice != null }
//                    .map { it.minPrice!! to it.maxPrice!! }
//                    .let {
//                        if (it.isEmpty()) {
//                            null
//                        } else {
//                            val description = PriceDescription()
//                            description.categoryId = categoryId
//                            description.shopId = shopId
//                            description.productId = productId
//                            description.minPrice = it.minOf { it.first }
//                            description.maxPrice = it.maxOf { it.second }
//                            try {
//                                priceDescriptionRepository.save(description)
//                            } catch (e: Exception) {
//
//                                LOGGER.error("Can not save new price description for category with id '$categoryId'")
//                                description
//                            }
//                        }
//                    }
//
//    companion object {
//        private val LOGGER = LoggerFactory.getLogger(DefaultPriceDescription::class.java)
//    }
//}