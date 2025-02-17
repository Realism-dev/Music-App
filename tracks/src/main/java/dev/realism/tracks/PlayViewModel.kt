package dev.realism.tracks

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.realism.data.TracksRepository
import dev.realism.data.model.Track
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class PlayViewModel @Inject constructor(
    private val repository: TracksRepository
) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private var trackList: List<Track> = emptyList()
    private var currentTrackIndex: Int = 0

    private val _trackTitle = MutableLiveData<String>()
    val trackTitle: LiveData<String> = _trackTitle

    private val _trackArtist = MutableLiveData<String>()
    val trackArtist: LiveData<String> = _trackArtist

    private val _trackImage = MutableLiveData<String>()
    val trackImage: LiveData<String> = _trackImage

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = _currentTime

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _duration = MutableLiveData<Int>()
    val duration: LiveData<Int> = _duration

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _isDownLoaded = MutableLiveData<Boolean>()
    val isDownLoaded: LiveData<Boolean> = _isDownLoaded

    private val _isDownLoading = MutableLiveData<Boolean>()
    val isDownLoading: LiveData<Boolean> = _isDownLoading

    init {
        mediaPlayer = MediaPlayer()
    }

    fun downloadCurrentTrack(track: Track) {
        // При начале загрузки показываем индикатор
        _isDownLoading.value = true
        _isDownLoaded.value = false // Пока не загружено

        viewModelScope.launch {
            try {
                // Загрузить трек
                repository.downloadTrack(track)

                // После успешной загрузки обновляем состояние
                _isDownLoading.value = false
                _isDownLoaded.value = true
            } catch (e: Exception) {
                // В случае ошибки
                _isDownLoading.value = false
                _isDownLoaded.value = false
            }
        }
    }


    // Логика для проверки, скачан ли трек
    private fun isTrackDownloaded(track: Track): Boolean {
        var result  = true
        try {
            result = repository.isTrackDownLoaded(track)
//            Log.d("TRACK DOWNLOADED DONE",track.toString())
//            Log.d("TRACK DOWNLOADED CASH",repository.getTracksIdFromCash().toString())
        }
        catch (e:Exception){
//            Log.d("TRACK DOWNLOADED FAILED",e.toString())
        }
        return result
    }

    fun loadTrack(track: Track) {
        _trackTitle.value = track.title
        _trackArtist.value = track.artist
        _trackImage.value = track.imageUrl ?: ""
        _isDownLoaded.value = isTrackDownloaded(track)

        try {
            mediaPlayer?.reset()
        } catch (e: IllegalStateException) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()
        }
        setMediaPlayer(track)
    }

    private fun setMediaPlayer(track: Track) {
        mediaPlayer?.setDataSource(track.previewUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            // Устанавливаем максимальное значение SeekBar равным длительности трека
            _duration.value = mediaPlayer?.duration ?: 0
            mediaPlayer?.start()
            togglePlay()

            // Начинаем обновление прогресса
            startProgressUpdater()
        }
        mediaPlayer?.setOnCompletionListener {
            playNextTrack()
        }
    }

    fun seekToProgress(progress: Int) {
        mediaPlayer?.seekTo(progress)
    }


    fun playNextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % trackList.size
        loadTrack(trackList[currentTrackIndex])
        togglePlay()
    }

    fun playPreviousTrack() {
        currentTrackIndex =
            if (currentTrackIndex - 1 < 0) trackList.size - 1 else currentTrackIndex - 1
        loadTrack(trackList[currentTrackIndex])
        togglePlay()
    }

    private fun togglePlay() {
        _isPlaying.value = true
    }


    fun togglePlayPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            _isPlaying.value = false
        } else {
            mediaPlayer?.start()
            _isPlaying.value = true
        }
    }

    private fun startProgressUpdater() {
        val handler = Handler(Looper.getMainLooper()) // Используем главный поток

        val updateProgressRunnable = object : Runnable {
            override fun run() {
                try {
                    mediaPlayer?.let {
                        if (it.isPlaying) {
                            val currentPos = it.currentPosition // Текущее время в миллисекундах
                            _progress.postValue(currentPos)
                            //Отображаем оставшееся время в секундах
                            _currentTime.postValue(
                                formatTime(duration.value?.minus(currentPos)?.div(1000) ?: 0)
                            )
                            handler.postDelayed(this, 200) // Обновляем каждую секунду
                        }
                    }
                } catch (e: IllegalStateException) {
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer()
                }
            }
        }
        handler.post(updateProgressRunnable) // Стартуем обновление прогресса
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
    }

    fun setTrackList(tracks: List<Track>, index:Int) {
        currentTrackIndex = index
        trackList = tracks
    }

    fun clear() {
        onCleared()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}