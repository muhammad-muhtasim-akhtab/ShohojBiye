package com.example.shohojbiye.di

import com.example.shohojbiye.data.remote.GovernmentRegistrySyncService
import com.example.shohojbiye.data.remote.MockGovernmentRegistrySyncServiceImpl
import com.example.shohojbiye.data.remote.MockNidVerificationServiceImpl
import com.example.shohojbiye.data.remote.NidVerificationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindNidVerificationService(
        impl: MockNidVerificationServiceImpl
    ): NidVerificationService

    @Binds
    @Singleton
    abstract fun bindGovernmentRegistrySyncService(
        impl: MockGovernmentRegistrySyncServiceImpl
    ): GovernmentRegistrySyncService
}
