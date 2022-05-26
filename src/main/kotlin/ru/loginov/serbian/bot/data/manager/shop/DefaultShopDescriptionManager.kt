package ru.loginov.serbian.bot.data.manager.shop

import com.google.maps.FindPlaceFromTextRequest
import com.google.maps.GeoApiContext
import com.google.maps.PlaceDetailsRequest
import com.google.maps.PlacesApi
import io.ktor.client.statement.readText
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.loginov.http.HttpClient
import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionCommentDto
import ru.loginov.serbian.bot.data.dto.shop.ShopDescriptionDto
import ru.loginov.serbian.bot.data.repository.shop.ShopDescriptionCommentDtoRepository
import ru.loginov.serbian.bot.data.repository.shop.ShopDescriptionDtoRepository
import ru.loginov.serbian.bot.util.google.suspendAndAwait
import java.time.LocalDateTime

@Service
class DefaultShopDescriptionManager(
        private val shopDescriptionDtoRepository: ShopDescriptionDtoRepository,
        private val shopDescriptionCommentDtoRepository: ShopDescriptionCommentDtoRepository,
        private val httpClient: HttpClient,
        private val geoApiContext: GeoApiContext
) : ShopDescriptionManager {

    override suspend fun create(googleMapLink: String, floor: Int?): ShopDescriptionDto? {
        val name = parseNameFrom(googleMapLink)

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

        return try {
            withContext(Dispatchers.IO) {
                shopDescriptionDtoRepository.save(dto)
            }
        } catch (e: Exception) {
            LOGGER.warn(
                    "Can not save shop description for place from google map link with place id '${candidate.placeId}'",
                    e
            )
            null
        }
    }

    override suspend fun create(name: String, address: String, floor: Int?): ShopDescriptionDto? {
        val response =
                try {
                    PlacesApi.textSearchQuery(geoApiContext, "$name, $address").await()
                } catch (e: Exception) {
                    LOGGER.debug("Can not execute text search for {name:'$name';address:'$address'}", e)
                    null
                }

        val dto = ShopDescriptionDto()
        dto.shopName = name
        dto.address = address

        if (response != null && response.results.size == 1) {
            val result = response.results[0]
            dto.googleMapId = result.placeId

            val details = try {
                PlacesApi.placeDetails(geoApiContext, result.placeId)
                        .fields(
                                PlaceDetailsRequest.FieldMask.NAME,
                                PlaceDetailsRequest.FieldMask.URL,
                                PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS
                        )
                        .await()
            } catch (e: Exception) {
                LOGGER.debug("Can not get place details for place with id '${result.placeId}'", e)
                null
            }

            if (details != null) {
                dto.shopName = details.name
                dto.googleMapLink = details.url.toString()
                dto.address = details.formattedAddress
            }
        }

        dto.floor = floor

        return try {
            shopDescriptionDtoRepository.save(dto)
        } catch (e: Exception) {
            LOGGER.warn("Can not save shop description for {name:'$name';address:'$address';floor:'$floor'}", e)
            null
        }
    }

    override suspend fun findById(id: Int): ShopDescriptionDto? =
            withContext(Dispatchers.IO) {
                try {
                    shopDescriptionDtoRepository.findByIdOrNull(id)
                } catch (e: Exception) {
                    LOGGER.warn("Can not get shop with id '$id'", e)
                    null
                }
            }


    override suspend fun containsWithId(id: Int): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    shopDescriptionDtoRepository.existsById(id)
                } catch (e: Exception) {
                    LOGGER.warn("Can not check existing for shop with id '$id'", e)
                    false
                }
            }

    override suspend fun remove(id: Int): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    shopDescriptionDtoRepository.deleteById(id)
                    true
                } catch (e: Exception) {
                    LOGGER.warn("Can not delete shop with id '$id'", e)
                    false
                }
            }


    override suspend fun addComment(shopId: Int, comment: String): Boolean {
        if (!containsWithId(shopId)) {
            return false
        }

        val dto = ShopDescriptionCommentDto()
        dto.entityId = shopId
        dto.comment = comment
        dto.createdTime = LocalDateTime.now()

        return withContext(Dispatchers.IO) {
            try {
                shopDescriptionCommentDtoRepository.save(dto)
                true
            } catch (e: Exception) {
                LOGGER.warn("Can not create new comment for shop with id '$shopId'", e)
                false
            }
        }
    }

    override suspend fun getComments(shopId: Int, beforeDate: LocalDateTime?): List<ShopDescriptionCommentDto> =
            withContext(Dispatchers.IO) {
                try {
                    shopDescriptionCommentDtoRepository.findTop10ByEntityIdAndCreatedTimeBefore(
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
        private val DEFAULT_USER_AGENT = "Mozilla/5.0 (Linux; rv:47.0) Gecko/20100101 Firefox/47.0"
        private val LOGGER = LoggerFactory.getLogger(DefaultShopDescriptionManager::class.java)
        private val NAME_META_CONTENT_REGEX = Regex("<meta\\s*content\\s*=\\s*\"((.(?!\"))*.?)\"\\s*property\\s*=\\s*\"og:title\"\\s*>")
    }
}