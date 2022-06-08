package com.example.tipjar.navigation

import com.example.tipjar.core.navigation.CoreNavigation
import com.example.tipjar.navigation.core.CoreNavigationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class NavigationModule {
    @Binds
    internal abstract fun bindCoreNavigation(coreNavigationImpl: CoreNavigationImpl): CoreNavigation
}