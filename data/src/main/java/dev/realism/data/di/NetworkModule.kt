package dev.realism.data.di

import dagger.Module
import dagger.Provides
import dev.realism.data.TrackApiService
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideTrackApiService(): TrackApiService {
        return TrackApiService.create()
    }
}
