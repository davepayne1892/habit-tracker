package com.example.habittracker.data.repository

import com.example.habittracker.data.dao.HabitDao
import com.example.habittracker.data.dao.HabitWithLog
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitLog
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {
    fun getHabitsWithLogsForDate(date: String): Flow<List<HabitWithLog>> =
        habitDao.getHabitsWithLogsForDate(date)

    suspend fun getHabitById(id: Long): Habit? = habitDao.getHabitById(id)

    suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    suspend fun toggleHabit(habitId: Long, date: String, isCompleted: Boolean) {
        habitDao.insertLog(HabitLog(habitId, date, isCompleted))
    }

    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>> =
        habitDao.getLogsForHabit(habitId)
}
