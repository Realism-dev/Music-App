package dev.realism.data

import dev.realism.data.api.TrackPreviewResponse
import dev.realism.data.api.TrackSearchResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface TrackApiService {
    // Запрос на скачивание MP3 файла
    @GET
    suspend fun downloadTrack(@Url url: String): Response<ResponseBody>

    // Запрос на поиск трека по названию
    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): TrackSearchResponse

    // Запрос на воспроизведение превью
    @GET("tracks/{id}")
    suspend fun getTrackPreview(@Path("id") trackId: Int): TrackPreviewResponse

    companion object {
        private const val BASE_URL = "https://api.deezer.com/"

        fun create(): TrackApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(TrackApiService::class.java)
        }
    }
}

