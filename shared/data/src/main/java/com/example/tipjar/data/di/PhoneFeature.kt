package com.example.tipjar.data.di

import com.example.tipjar.data.phonefeature.IUserPhoneFeatureManager
import com.example.tipjar.data.phonefeature.UserPhoneFeatureManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class PhoneFeature {
    @Binds
    internal abstract fun bindUserPhoneFeatureManager(userPhoneFeatureManager: UserPhoneFeatureManager): IUserPhoneFeatureManager
}