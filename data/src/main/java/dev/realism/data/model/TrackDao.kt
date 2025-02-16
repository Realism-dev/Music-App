package dev.realism.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackDao {
    @Insert
    suspend fun insertTrack(track: Track)

    @Query("SELECT * FROM tracks WHERE title LIKE :query OR artist LIKE :query ORDER BY artist,title")
    suspend fun getTracks(query: String): List<Track>

    @Query("SELECT * FROM tracks ORDER BY artist,title")
    suspend fun getAllTracks(): List<Track>

    @Query("SELECT previewUrl FROM tracks WHERE id= :trackId")
    suspend fun getTrackUrl(trackId: Int): String

    @Query("SELECT COUNT(id) FROM tracks WHERE id= :trackId")
    suspend fun findTrack(trackId: Int): Int
}

