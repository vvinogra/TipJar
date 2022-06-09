package com.example.tipjar.data.di

import com.example.tipjar.data.image.IImageStorageManager
import com.example.tipjar.data.image.ImageStorageManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class ImageModule {
    @Binds
    internal abstract fun bindImageStorageManager(imageStorageManager: ImageStorageManager): IImageStorageManager
}