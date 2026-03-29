package cn.x.di

import android.app.Application
import cn.x.util.MediaStoreScanUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppleModule {


    @Singleton
    @Provides
    fun provideMusicScanFlow(application: Application): MediaStoreScanUtil {
        return MediaStoreScanUtil(application)
    }

}