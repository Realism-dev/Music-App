package dev.realism.data.di

import dagger.Module
import dagger.Provides
import dev.realism.data.DataSource

@Module
class DataModule {

    @Provides
    fun provideDataSource(): DataSource {
        return DataSource.LOCAL
    }
}
