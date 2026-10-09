package com.example.shohojbiye.data.local.dao

import androidx.room.*
import com.example.shohojbiye.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShohojBiyeDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM profiles WHERE userId = :userId")
    suspend fun getProfileByUserId(userId: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Query("SELECT * FROM registrations")
    fun getAllRegistrations(): Flow<List<RegistrationEntity>>

    @Query("SELECT * FROM registrations WHERE id = :id")
    suspend fun getRegistrationById(id: String): RegistrationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: RegistrationEntity)

    @Query("SELECT * FROM kabin_records WHERE registrationId = :regId")
    suspend fun getKabinRecord(regId: String): KabinRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKabinRecord(kabinRecord: KabinRecordEntity)
}
