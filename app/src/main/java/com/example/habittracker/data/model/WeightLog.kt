package com.example.habittracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "weight_logs",
    primaryKeys = ["habitId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId")]
)
data class WeightLog(
    val habitId: Long,
    val date: String, // Format: "yyyy-MM-dd"
    val weightKg: Double
)
