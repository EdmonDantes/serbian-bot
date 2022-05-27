package ru.loginov.serbian.bot.data.manager.shop

import com.google.maps.FindPlaceFromTextRequest
import com.google.maps.GeoApiContext
import com.google.maps.PlaceDetailsRequest
import com.google.maps.PlacesApi
import io.ktor.client.statement.readText
import io.ktor.http.HttpMethod
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.domain.AbstractPersistable_
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.loginov.http.HttpClient
import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionCommentDto
import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionDto
import ru.loginov.serbian.bot.data.repository.shop.ShopDescriptionCommentDtoRepository
import ru.loginov.serbian.bot.data.repository.shop.ShopDescriptionDtoRepository
import ru.loginov.serbian.bot.util.google.suspendAndAwait
import ru.loginov.serbian.bot.util.saveOr
import ru.loginov.serbian.bot.util.useSuspend
import java.time.LocalDateTime

@Service
class DefaultShopDescriptionManager(
        private val shopDescriptionDtoRepository: ShopDescriptionDtoRepository,
        private val shopDescriptionCommentDtoRepository: ShopDescriptionCommentDtoRepository,
        private val httpClient: HttpClient,
        private val geoApiContext: GeoApiContext
) : ShopDescriptionManager {

    override suspend fun create(googleMapLink: String, floor: Int?): ShopDescriptionDto? {
        val name = parseNameFrom(googleMapLink) ?: return null

        val results = try {
            PlacesApi.findPlaceFromText(
                    geoApiContext,
                    name,
                    FindPlaceFromTextRequest.InputType.TEXT_QUERY
            ).suspendAndAwait()
        } catch (e: Exception) {
            LOGGER.warn("Can not find place with name '$name'", e)
            return null
        }
        val candidates = results.candidates
        if (candidates.size != 1) {
            return null
        }

        val candidate = candidates[0]

        val details = try {
            PlacesApi.placeDetails(geoApiContext, candidate.placeId).fields(
                    PlaceDetailsRequest.FieldMask.NAME,
                    PlaceDetailsRequest.FieldMask.URL,
                    PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS
            ).suspendAndAwait()
        } catch (e: Exception) {
            LOGGER.warn("Can not get place default from places api for place with id '${candidate.placeId}'", e)
            return null
        }

        val dto = ShopDescriptionDto()
        dto.googleMapLink = details.url.toString()
        dto.address = details.formattedAddress
        dto.googleMapId = candidate.placeId
        dto.shopName = details.name
        dto.floor = floor

        return shopDescriptionDtoRepository.useSuspend {
            it.saveOr(dto) { e ->
                LOGGER.warn(
                        "Can not save shop description for place from google map link with place id '${candidate.placeId}'",
                        e
                )
                null
            }
        }
    }

    override suspend fun create(name: String, address: String, floor: Int?): ShopDescriptionDto? {
        val dto = ShopDescriptionDto()
        dto.shopName = name
        dto.address = address
        dto.floor = floor

        return shopDescriptionDtoRepository.useSuspend {
            it.saveOr(dto) { e ->
                LOGGER.warn("Can not save shop description for {name:'$name';address:'$address';floor:'$floor'}", e)
                null
            }
        }
    }

    override suspend fun findById(id: Int): ShopDescriptionDto? =
            shopDescriptionDtoRepository.useSuspend {
                try {
                    it.findByIdOrNull(id)
                } catch (e: Exception) {
                    LOGGER.warn("Can not get shop with id '${AbstractPersistable_.id}'", e)
                    null
                }
            }


    override suspend fun existsById(id: Int): Boolean =
            shopDescriptionDtoRepository.useSuspend {
                try {
                    it.existsById(id)
                } catch (e: Exception) {
                    LOGGER.warn("Can not check existing for shop with id '$id'", e)
                    false
                }
            }

    override suspend fun remove(id: Int): Boolean =
            shopDescriptionDtoRepository.useSuspend {
                try {
                    it.deleteById(id)
                    true
                } catch (e: Exception) {
                    LOGGER.warn("Can not delete shop with id '$id'", e)
                    false
                }
            }


    override suspend fun addComment(shopId: Int, comment: String): Boolean {
        if (!existsById(shopId)) {
            return false
        }

        val dto = ShopDescriptionCommentDto()
        dto.entityId = shopId
        dto.comment = comment
        dto.createdTime = LocalDateTime.now()

        return shopDescriptionCommentDtoRepository.useSuspend {
            try {
                it.save(dto)
                true
            } catch (e: Exception) {
                LOGGER.warn("Can not create new comment for shop with id '$shopId'", e)
                false
            }
        }
    }

    override suspend fun getComments(shopId: Int, beforeDate: LocalDateTime?): List<ShopDescriptionCommentDto> =
            shopDescriptionCommentDtoRepository.useSuspend {
                try {
                    it.findTop10ByEntityIdAndCreatedTimeBeforeOrderByCreatedTimeDesc(
                            shopId,
                            beforeDate ?: LocalDateTime.now()
                    )
                } catch (e: Exception) {
                    LOGGER.warn("Can not get comment for shop with id '$shopId' before time '$beforeDate'", e)
                    emptyList()
                }
            }

    private suspend fun parseNameFrom(googleMapLink: String): String? {
        val response = httpClient.request(
                HttpMethod.Get,
                googleMapLink,
                headersUser = mapOf("User-Agent" to DEFAULT_USER_AGENT, "Accept" to "*/*", "Connection" to "keep-alive")
        )

        val html = response.readText()
        val match = NAME_META_CONTENT_REGEX.find(html) ?: return null
        val groups = match.groupValues
        if (groups.size < 2) {
            return null
        }

        return groups[1]
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultShopDescriptionManager::class.java)

        private val DEFAULT_USER_AGENT = "Mozilla/5.0 (Linux; rv:47.0) Gecko/20100101 Firefox/47.0"
        private val NAME_META_CONTENT_REGEX = Regex("<meta\\s*content\\s*=\\s*\"((.(?!\"))*.?)\"\\s*property\\s*=\\s*\"og:title\"\\s*>")
    }
}