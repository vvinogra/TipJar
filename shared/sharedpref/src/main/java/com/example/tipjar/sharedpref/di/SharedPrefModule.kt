package com.example.tipjar.sharedpref.di

import com.example.tipjar.sharedpref.AppSharedPref
import com.example.tipjar.sharedpref.IAppSharedPref
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class SharedPrefModule {
    @Binds
    internal abstract fun binAppSharedPref(pref: AppSharedPref): IAppSharedPref
}