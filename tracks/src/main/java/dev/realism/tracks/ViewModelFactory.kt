package dev.realism.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.realism.data.DataSource
import dev.realism.data.TracksRepository
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val repository: TracksRepository,
    private val dataSource: DataSource
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(TracksViewModel::class.java)->TracksViewModel(repository, dataSource) as T
            modelClass.isAssignableFrom(PlayViewModel::class.java) ->PlayViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


