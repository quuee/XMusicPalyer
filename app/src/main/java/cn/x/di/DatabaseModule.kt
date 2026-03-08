package cn.x.di

import android.app.Application
import androidx.room.Room
import cn.x.data.db.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideAppDatabase(application: Application): MusicDatabase {
        return Room.databaseBuilder(
            application,
            MusicDatabase::class.java,
            "music_db"
        ).build()
    }
}