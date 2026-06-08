package com.example.habittracker.data.dao

import androidx.room.*
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitLog
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLog)

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun getLogForDate(habitId: Long, date: String): HabitLog?

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId")
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>>

    @Query("""
        SELECT h.*, hl.isCompleted 
        FROM habits h 
        LEFT JOIN habit_logs hl ON h.id = hl.habitId AND hl.date = :date
    """)
    fun getHabitsWithLogsForDate(date: String): Flow<List<HabitWithLog>>
}

data class HabitWithLog(
    @Embedded val habit: Habit,
    val isCompleted: Boolean?
)
