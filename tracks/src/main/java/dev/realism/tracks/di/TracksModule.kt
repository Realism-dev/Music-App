package dev.realism.tracks.di

import dagger.Module
import dagger.Provides
import dev.realism.data.DataSource
import dev.realism.data.TrackApiService
import dev.realism.data.model.TrackDao
import dev.realism.data.TracksRepository
import dev.realism.tracks.ViewModelFactory
import java.io.File

@Module
class TracksModule {

    @Provides
    fun provideTracksRepository(apiService: TrackApiService, trackDao: TrackDao, fileDir: File): TracksRepository {
        return TracksRepository(apiService, trackDao, fileDir)
    }

    @Provides
    fun provideTracksViewModelFactory(repository: TracksRepository, dataSource: DataSource): ViewModelFactory {
        return ViewModelFactory(repository, dataSource)
    }
}
