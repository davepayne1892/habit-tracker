package com.example.habittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pet_state")
data class PetState(
    @PrimaryKey val id: Long = 1,
    val name: String = "SPIKE",
    val xp: Int = 0,
    val level: Int = 1,
    val health: Int = 100,
    val lastCheckDate: String
)
