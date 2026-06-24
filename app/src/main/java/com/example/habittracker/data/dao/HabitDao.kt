package com.example.habittracker.data.dao

import androidx.room.*
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitLog
import com.example.habittracker.data.model.WeightLog
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

    @Query("SELECT * FROM habit_logs")
    fun getAllLogs(): Flow<List<HabitLog>>

    @Query("""
        SELECT h.*, hl.isCompleted 
        FROM habits h 
        LEFT JOIN habit_logs hl ON h.id = hl.habitId AND hl.date = :date
        WHERE h.isArchived = 0
    """)
    fun getHabitsWithLogsForDate(date: String): Flow<List<HabitWithLog>>

    @Query("""
        SELECT h.*, hl.isCompleted 
        FROM habits h 
        LEFT JOIN habit_logs hl ON h.id = hl.habitId AND hl.date = :date
        WHERE h.isArchived = 0
    """)
    suspend fun getHabitsWithLogsForDateSuspend(date: String): List<HabitWithLog>

    @Query("UPDATE habits SET isArchived = 1 WHERE id = :id")
    suspend fun archiveHabit(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(weightLog: WeightLog)

    @Query("SELECT * FROM weight_logs WHERE habitId = :habitId AND date = :date")
    suspend fun getWeightLog(habitId: Long, date: String): WeightLog?

    @Query("SELECT * FROM weight_logs WHERE habitId = :habitId ORDER BY date ASC")
    fun getWeightLogsForHabit(habitId: Long): Flow<List<WeightLog>>

    @Query("DELETE FROM weight_logs WHERE habitId = :habitId AND date = :date")
    suspend fun deleteWeightLog(habitId: Long, date: String)
}

data class HabitWithLog(
    @Embedded val habit: Habit,
    val isCompleted: Boolean?
)
