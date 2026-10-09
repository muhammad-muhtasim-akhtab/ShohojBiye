package com.example.shohojbiye.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val nid: String, // Encrypted
    val photoUrl: String?,
    val education: String?,
    val jobType: String?,
    val income: Double
)
