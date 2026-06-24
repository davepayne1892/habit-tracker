package com.example.habittracker.data.repository

import com.example.habittracker.data.dao.HabitDao
import com.example.habittracker.data.dao.PetDao
import com.example.habittracker.data.dao.HabitWithLog
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitLog
import com.example.habittracker.data.model.PetState
import com.example.habittracker.data.model.WeightLog
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao, private val petDao: PetDao) {
    fun getHabitsWithLogsForDate(date: String): Flow<List<HabitWithLog>> =
        habitDao.getHabitsWithLogsForDate(date)

    suspend fun getHabitsWithLogsForDateSuspend(date: String): List<HabitWithLog> =
        habitDao.getHabitsWithLogsForDateSuspend(date)

    suspend fun getHabitById(id: Long): Habit? = habitDao.getHabitById(id)

    suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    suspend fun archiveHabit(id: Long) = habitDao.archiveHabit(id)

    suspend fun toggleHabit(habitId: Long, date: String, isCompleted: Boolean) {
        habitDao.insertLog(HabitLog(habitId, date, isCompleted))
    }

    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>> =
        habitDao.getLogsForHabit(habitId)

    fun getPetState(): Flow<PetState?> = petDao.getPetState()

    suspend fun updatePetState(petState: PetState) = petDao.updatePetState(petState)

    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    fun getAllLogs(): Flow<List<HabitLog>> = habitDao.getAllLogs()

    suspend fun insertWeightLog(weightLog: WeightLog) = habitDao.insertWeightLog(weightLog)

    suspend fun getWeightLog(habitId: Long, date: String): WeightLog? =
        habitDao.getWeightLog(habitId, date)

    fun getWeightLogsForHabit(habitId: Long): Flow<List<WeightLog>> =
        habitDao.getWeightLogsForHabit(habitId)

    suspend fun deleteWeightLog(habitId: Long, date: String) =
        habitDao.deleteWeightLog(habitId, date)
}
