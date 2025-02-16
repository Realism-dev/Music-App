package dev.realism.data.api

import androidx.room.PrimaryKey

data class TrackSearchResponse(
    val data: List<TrackApiResponse>
)

data class TrackApiResponse(
    val id: Int,
    val title: String,
    val duration: Int,
    val preview: String?,
    val artist: ArtistApiResponse,
    val album: AlbumApiResponse
)

data class ArtistApiResponse(
    val id: Int,
    val name: String,
    val link: String,
    val picture: String?
)

data class AlbumApiResponse(
    val id: Int,
    val title: String,
    val cover: String,
)