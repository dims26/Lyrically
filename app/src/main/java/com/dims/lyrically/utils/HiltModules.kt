package com.dims.lyrically.utils

import com.dims.lyrically.models.Song
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltModules {


    private val gsonBuilder: GsonBuilder  = GsonBuilder()

    @Singleton
    @Provides
    @Named("GeniusGson")
    fun provideGeniusGson(deserializer: SongListDeserializer): Gson {
        gsonBuilder.registerTypeAdapter(Song::class.java, deserializer)

        return gsonBuilder.create()
    }

    @Singleton
    @Provides
    @Named("DispatcherIO")
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient = OkHttpClient()
}