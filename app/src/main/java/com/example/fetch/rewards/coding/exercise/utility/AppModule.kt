package com.example.fetch.rewards.coding.exercise.utility

import com.example.fetch.rewards.coding.exercise.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import retrofit2.Converter.Factory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    companion object {
        @Singleton
        @Provides
        fun provideIoDispatcher() = Dispatchers.IO

        @Singleton
        @Provides
        fun provideGson(): Gson = GsonBuilder().create()

        @Singleton
        @Provides
        fun provideGsonConverter(gson: Gson): Factory = GsonConverterFactory.create(gson)

        @Singleton
        @Provides
        fun provideRetrofitInstance(gsonConverter: Factory): Retrofit =
            Retrofit.Builder().baseUrl("https://fetch-hiring.s3.amazonaws.com/").addConverterFactory(gsonConverter).build()

        @Singleton
        @Provides
        fun provideItemService(retrofit: Retrofit): ItemService = retrofit.create(ItemService::class.java)
    }

    // Qualifiers help differentiate different implementations of various interfaces
    @Qualifier
    @Retention
    annotation class RestApiDataSource

    // By qualifying the following Hilt Binding with @RestApiDataSource,
    // any dependencies qualified by the same annotation will use this binding to get the correct implementation injected
    @Singleton
    @RestApiDataSource
    @Binds
    abstract fun bindItemRestApiDataSource(itemRestApiDataSource: ItemRestApiDataSource): ItemDataSource
}

// By splitting the Repository into its own module, can more easily swap in Stub Repositories for testing
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun provideItemRepository(appItemRepository: AppItemRepository): ItemRepository
}