package com.example.habittracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.dao.HabitWithLog
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.habittracker.notifications.NotificationHelper

class HabitViewModel(application: Application, private val repository: HabitRepository) : AndroidViewModel(application) {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val habitsForSelectedDate: Flow<List<HabitWithLog>> = _selectedDate.flatMapLatest { date ->
        repository.getHabitsWithLogsForDate(date.format(dateFormatter))
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun toggleHabit(habitId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleHabit(habitId, _selectedDate.value.format(dateFormatter), isCompleted)
        }
    }

    suspend fun getHabitById(id: Long): Habit? = repository.getHabitById(id)

    fun addHabit(name: String, description: String, reminderTime: String?) {
        viewModelScope.launch {
            val habit = Habit(name = name, description = description, reminderTime = reminderTime)
            val id = repository.insertHabit(habit)
            if (reminderTime != null) {
                NotificationHelper.scheduleReminder(getApplication(), habit.copy(id = id))
            }
        }
    }

    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>> =
        repository.getLogsForHabit(habitId)

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            NotificationHelper.cancelReminder(getApplication(), habit.id)
        }
    }
}

// Since I don't have a DI framework like Hilt set up, I'll need a Factory or just a simple provider for now.
// For the sake of this CLI-generated project, I'll assume a simple manual injection in MainActivity.
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.Flow

class HabitViewModelFactory(private val application: Application, private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
