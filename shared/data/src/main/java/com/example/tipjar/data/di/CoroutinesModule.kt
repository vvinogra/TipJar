package com.example.tipjar.data.di

import com.example.tipjar.data.coroutines.DefaultDispatcherProvider
import com.example.tipjar.data.coroutines.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class CoroutinesModule {
    @Binds
    internal abstract fun bindDispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider
}