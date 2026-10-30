package com.nen.gatekeeper.di

import com.nen.gatekeeper.data.api.MainApi
import com.nen.gatekeeper.data.plugin.FourKHDHub
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object NetworkModule {
    @Provides
    @Singleton
    fun provideMainApi(): MainApi {
        return FourKHDHub()
    }
}