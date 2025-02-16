package dev.realism.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tracks")  // Указываем имя таблицы
data class Track(
    @PrimaryKey val id: Int,  // Устанавливаем id как PrimaryKey
    val title: String,
    val artist: String,
    val imageUrl: String?,
    val previewUrl: String? = null,
    var uri :String? = null,
) : Parcelable



