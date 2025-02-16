package dev.realism.musicapp.di

import android.app.Application
import android.content.Context
import dagger.Component
import dagger.Provides
import dev.realism.data.TrackApiService
import dev.realism.data.TracksRepository
import dev.realism.data.di.AppModule
import dev.realism.data.di.DataModule
import dev.realism.data.di.DatabaseModule
import dev.realism.data.di.FileModule
import dev.realism.data.di.NetworkModule
import dev.realism.data.model.TrackDao
import dev.realism.musicapp.MainActivity
import dev.realism.tracks.BaseTracksFragment
import dev.realism.tracks.TracksViewModel
import dev.realism.tracks.di.TracksComponent
import dev.realism.tracks.di.TracksModule
import java.io.File
import javax.inject.Singleton

@Component(modules = [AppModule::class,
    DatabaseModule::class,
    NetworkModule::class,
    DataModule::class,
    TracksModule::class,
    FileModule::class])
@Singleton
interface AppComponent:TracksComponent {
    fun inject(fragment: BaseTracksFragment)
    fun inject(viewModel: TracksViewModel)

    // Предоставляем зависимости напрямую
    fun provideTrackDao(): TrackDao
    fun provideTrackApiService(): TrackApiService
    fun provideFileDir():File

    override fun injectBaseTracksFragment(baseTracksFragment: BaseTracksFragment)
}
