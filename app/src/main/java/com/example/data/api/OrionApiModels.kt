package com.example.data.api

import com.squareup.moshi.Json

data class OrionResponseDto(
    @Json(name = "result") val result: OrionResultDto? = null,
    @Json(name = "data") val data: OrionDataDto? = null
)

data class OrionResultDto(
    @Json(name = "status") val status: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "message") val message: String? = null
)

data class OrionDataDto(
    @Json(name = "count") val count: Int? = 0,
    @Json(name = "total") val total: Int? = 0,
    @Json(name = "streams") val streams: List<OrionStreamDto>? = emptyList()
)

data class OrionStreamDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "file") val file: OrionFileDto? = null,
    @Json(name = "video") val video: OrionVideoDto? = null,
    @Json(name = "audio") val audio: OrionAudioDto? = null,
    @Json(name = "stream") val stream: OrionStreamLinkDto? = null,
    @Json(name = "seeds") val seeds: Int? = 0,
    @Json(name = "cached") val cached: Boolean? = false,
    @Json(name = "source") val source: String? = null,
    @Json(name = "debrid") val debrid: String? = null
)

data class OrionFileDto(
    @Json(name = "name") val name: String? = null,
    @Json(name = "size") val size: Long? = 0L,
    @Json(name = "hash") val hash: String? = null
)

data class OrionVideoDto(
    @Json(name = "quality") val quality: String? = null,
    @Json(name = "codec") val codec: String? = null,
    @Json(name = "width") val width: Int? = null,
    @Json(name = "height") val height: Int? = null
)

data class OrionAudioDto(
    @Json(name = "channels") val channels: Double? = null,
    @Json(name = "codec") val codec: String? = null
)

data class OrionStreamLinkDto(
    @Json(name = "type") val type: String? = null,
    @Json(name = "host") val host: String? = null,
    @Json(name = "link") val link: String? = null,
    @Json(name = "magnet") val magnet: String? = null
)

data class OrionUserResponseDto(
    @Json(name = "result") val result: OrionResultDto? = null,
    @Json(name = "data") val data: OrionUserDataDto? = null
)

data class OrionUserDataDto(
    @Json(name = "username") val username: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "account") val account: OrionUserAccountDto? = null,
    @Json(name = "limit") val limit: OrionUserLimitsDto? = null
)

data class OrionUserAccountDto(
    @Json(name = "type") val type: String? = null,
    @Json(name = "valid") val valid: Boolean? = false,
    @Json(name = "expire") val expire: String? = null
)

data class OrionUserLimitsDto(
    @Json(name = "link") val link: OrionUserLimitDetailDto? = null,
    @Json(name = "hash") val hash: OrionUserLimitDetailDto? = null,
    @Json(name = "container") val container: OrionUserLimitDetailDto? = null
)

data class OrionUserLimitDetailDto(
    @Json(name = "daily") val daily: Int? = 0,
    @Json(name = "remaining") val remaining: Int? = 0
)

