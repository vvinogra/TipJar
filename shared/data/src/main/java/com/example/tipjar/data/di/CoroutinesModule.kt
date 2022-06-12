package com.example.tipjar.data.di

import com.example.tipjar.data.coroutines.DefaultDispatcherProvider
import com.example.tipjar.data.coroutines.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class CoroutinesModule {

    companion object {
        @Singleton
        @Provides
        internal fun providesCoroutineScope(defaultDispatcherProvider: DefaultDispatcherProvider): CoroutineScope {
            return CoroutineScope(SupervisorJob() + defaultDispatcherProvider.io)
        }
    }

    @Binds
    internal abstract fun bindDispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider
}