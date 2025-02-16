package dev.realism.data

import android.util.Log
import dev.realism.data.api.TrackApiResponse
import dev.realism.data.model.TrackDao
import dev.realism.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


class TracksRepository @Inject constructor(
    private val apiService: TrackApiService,
    private val trackDao: TrackDao,
    private val filesDir: File
) {
    // Получение треков по запросу из локальной базы данных
    suspend fun getTracksFromLocal(query: String): List<Track> {
        val list = if (query.isEmpty())
        {
            Log.d("TRACKS DOWNLOADED","getAllTracks")
            trackDao.getAllTracks()
        }
        else {
            Log.d("TRACKS LOADED","query = $query")
            trackDao.getTracks(query)
        }
        Log.d("TRACKS LOADED",list.toString())
        return list
    }

    // Получение треков по запросу из локальной базы данных
    suspend fun getTrackUrlFromLocal(trackId: Int): String {
        return trackDao.getTrackUrl(trackId)
    }

    // Получение url превью для конкретного трека
    suspend fun getTrackPreviewUrl(trackId: Int): String? {
        return try {
            val preview = apiService.getTrackPreview(trackId).preview
            preview
        } catch (e: Exception) {
            //TODO Обработка ошибок, логирование
            null  // Возвращаем null, если произошла ошибка или preview нет
        }
    }

    // Получение списка треков по запросу
    suspend fun getTrackListFromApi(query: String): List<Track> {
        return try {
            val tracks = apiService.searchTracks(query)
            tracks.data
                .map { convertTrackApiResponseToTrack(it) }
                .ifEmpty {  emptyList() }
        } catch (e: Exception) {
//            Log.d("DEEZER API", e.toString())
            //TODO Обработка ошибок, например, логирование
            emptyList()  // Возвращаем пустой список, если произошла ошибка
        }
    }


    private fun convertTrackApiResponseToTrack(trackApi: TrackApiResponse): Track {
//        Log.d("DEEZER API ALBUM", trackApi.album.toString())
        return Track(
            id = trackApi.id,
            title = trackApi.title,
            artist = trackApi.artist.name,
            imageUrl = trackApi.album.cover,
            previewUrl = trackApi.preview
        )
    }

    //Скачиваем превью трека по ссылке
    suspend fun downloadTrack(track:TrackApiResponse) {
        val url = track.preview.toString()
        val response = apiService.downloadTrack(url)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                saveTrack(body,track)
            }
        } else {
            Log.d("DEEZER API DOWNLOAD",response.message())
        }
    }

    //Скачиваем превью трека по названию трека
    suspend fun downloadTrack(track:Track) {
        val url = track.previewUrl.toString()
        val response = apiService.downloadTrack(url)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                saveTrack(body,track)
            }
        } else {
            Log.d("DEEZER API DOWNLOAD",response.message())
        }
    }

    // Сохраняем трек во внутреннем хранилище и создаем запись в таблице Tracks
    private suspend fun saveTrack(body: ResponseBody, track: Track) {
        val file = File(filesDir, track.id.toString())
        val outputStream =
            withContext(Dispatchers.IO) {
                FileOutputStream(file)
            }
        body.byteStream().copyTo(outputStream)
        withContext(Dispatchers.IO) {
            outputStream.close()
        }
        track.apply {
            uri = file.absolutePath
        }
        trackDao.insertTrack(track)
    }

    // Сохраняем трек во внутреннем хранилище и создаем запись в таблице Tracks
    private suspend fun saveTrack(body: ResponseBody, trackApiResponse: TrackApiResponse) {
        val file = File(filesDir, trackApiResponse.id.toString())
        val outputStream =
            withContext(Dispatchers.IO) {
                FileOutputStream(file)
            }
        body.byteStream().copyTo(outputStream)
        withContext(Dispatchers.IO) {
            outputStream.close()
        }
        val track = convertTrackApiResponseToTrack(trackApiResponse).apply {
            uri = file.absolutePath
        }
        trackDao.insertTrack(track)
    }

    suspend fun isTrackDownLoaded(track: Track): Boolean {
        return trackDao.findTrack(track.id)>0
    }
}


