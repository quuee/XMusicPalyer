package cn.x.di

import android.app.Application
import cn.x.util.MusicScanFlow
import cn.x.util.SPUtil
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
    fun provideMusicScanFlow(application: Application): MusicScanFlow {
        return MusicScanFlow(application)
    }

    @Singleton
    @Provides
    fun provideSPUtil(application: Application): SPUtil {
        return SPUtil(application)
    }
}