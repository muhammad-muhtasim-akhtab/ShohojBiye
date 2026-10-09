package com.example.shohojbiye.di

import android.content.Context
import androidx.room.Room
import com.example.shohojbiye.data.local.ShohojBiyeDatabase
import com.example.shohojbiye.data.local.dao.ShohojBiyeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ShohojBiyeDatabase {
        return Room.databaseBuilder(
            context,
            ShohojBiyeDatabase::class.java,
            "shohoj_biye_db"
        ).build()
    }

    @Provides
    fun provideDao(database: ShohojBiyeDatabase): ShohojBiyeDao {
        return database.dao()
    }
}
