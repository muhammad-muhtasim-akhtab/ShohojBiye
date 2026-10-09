package com.example.shohojbiye.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shohojbiye.data.local.dao.ShohojBiyeDao
import com.example.shohojbiye.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        ProfileEntity::class,
        RegistrationEntity::class,
        KabinRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShohojBiyeDatabase : RoomDatabase() {
    abstract fun dao(): ShohojBiyeDao
}
