package dev.realism.tracks

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.realism.data.model.Track

class TracksAdapter(private val onTrackClick: (Track) -> Unit) : ListAdapter<Track, TracksAdapter.TrackViewHolder>(DiffCallback()) {
    var downloadedTracks: List<Int> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.trackTitle)
        private val artist: TextView = itemView.findViewById(R.id.trackArtist)
        private val image: ImageView = itemView.findViewById(R.id.trackImage)
        private val actionImage: ImageView = itemView.findViewById(R.id.actionIcon)

        fun bind(track: Track) {
            title.text = track.title
            artist.text = track.artist

            // Проверка на загрузку
            val isDownloaded = downloadedTracks.contains(track.id)
            Log.d("TRACKS DOWNLOADED", "track is dl $isDownloaded = $track")

            if (isDownloaded) actionImage.setImageResource(R.drawable.ic_play)
            else actionImage.setImageResource(R.drawable.ic_download)

            try {
                Glide.with(itemView.context).load(track.imageUrl).into(image)
            } catch (e: Exception) {
                Log.d("DEEZER API IMAGE", e.toString())
            }

            // Обработка клика по треку
            itemView.setOnClickListener {
                onTrackClick(track)  // Вызываем коллбэк, передавая выбранный трек
            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Track, newItem: Track) = oldItem == newItem
    }
}

