package com.example.shohojbiye.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val phone: String,
    val email: String,
    val role: String // GENERAL_USER, KAZI, ADMIN
)
