package dev.realism.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import java.io.File

@Module
class FileModule {
    @Provides
    fun provideFileDirectory(context: Context): File {
        return context.filesDir
    }
}

