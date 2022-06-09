package com.example.tipjar.data.di

import com.example.tipjar.data.tiphistory.ITipHistoryRepository
import com.example.tipjar.data.tiphistory.TipHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    internal abstract fun bindTipHistoryRepository(tipHistoryRepository: TipHistoryRepository): ITipHistoryRepository
}