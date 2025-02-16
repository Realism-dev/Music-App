package dev.realism.tracks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.realism.data.DataSource
import dev.realism.data.TracksRepository
import dev.realism.data.model.Track
import kotlinx.coroutines.launch

class TracksViewModel(private val repository: TracksRepository, private val dataSource: DataSource) : ViewModel() {
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _downloadedTracks = MutableLiveData<List<Track>>()
    val downloadedTracks: LiveData<List<Track>> = _downloadedTracks


    // Метод загрузки данных с передачей источника
    fun searchTracks(query: String = "") {
        viewModelScope.launch {
            try {
                val loadedTracks = when (dataSource) {
                    is DataSource.NETWORK -> repository.getTrackListFromApi(query)  // Загружаем из сети
                    is DataSource.LOCAL -> repository.getTracksFromLocal(query)  // Загружаем из базы данных
                }
                _tracks.value = loadedTracks
            } catch (e: Exception) {
                Log.d("TRACKS",e.toString())
            }
        }
    }

    fun getDownloadedTracks() {
        viewModelScope.launch {
            try {
                val loaded = repository.getTracksFromLocal("")
                _downloadedTracks.value = loaded
                Log.d("TRACKS DOWNLOADED LIST DONE", _downloadedTracks.value.toString())
            }
            catch (e:Exception){
                Log.d("TRACKS DOWNLOADED LIST FAIL", _downloadedTracks.value.toString())
            }
        }
    }
}


