package dev.realism.musicapp

import android.app.Application
import dev.realism.data.di.AppModule
import dev.realism.data.di.DatabaseModule
import dev.realism.data.di.NetworkModule
import dev.realism.musicapp.di.AppComponent
import dev.realism.musicapp.di.DaggerAppComponent
import dev.realism.tracks.di.TracksComponent
import dev.realism.tracks.di.TracksComponentProvider

class MyApplication : Application(),TracksComponentProvider {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .databaseModule(DatabaseModule())
            .networkModule(NetworkModule())
            .appModule(AppModule(applicationContext))
            .build()
    }

    override fun getTracksComponent(): TracksComponent {
        return appComponent
    }
}

