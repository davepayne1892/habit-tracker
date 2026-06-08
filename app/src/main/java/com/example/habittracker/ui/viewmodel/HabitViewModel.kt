package com.example.habittracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.dao.HabitWithLog
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitLog
import com.example.habittracker.data.model.PetState
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.habittracker.notifications.NotificationHelper
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModel(application: Application, private val repository: HabitRepository) : AndroidViewModel(application) {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val habitsForSelectedDate: Flow<List<HabitWithLog>> = _selectedDate.flatMapLatest { date ->
        repository.getHabitsWithLogsForDate(date.format(dateFormatter))
    }

    val petState: Flow<PetState?> = repository.getPetState()

    val allHabits: Flow<List<Habit>> = repository.getAllHabits()
    val allLogs: Flow<List<HabitLog>> = repository.getAllLogs()

    private val _evolutionEvent = MutableSharedFlow<Pair<Int, Int>>()
    val evolutionEvent: SharedFlow<Pair<Int, Int>> = _evolutionEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            checkDailyDecay()
        }
    }

    private suspend fun checkDailyDecay() {
        val todayStr = LocalDate.now().format(dateFormatter)
        val currentPet = repository.getPetState().first()
        val pet = currentPet ?: PetState(lastCheckDate = todayStr)
        if (currentPet == null) {
            repository.updatePetState(pet)
            return
        }

        if (pet.lastCheckDate != todayStr) {
            val lastDate = LocalDate.parse(pet.lastCheckDate, dateFormatter)
            val today = LocalDate.now()
            var currentHealth = pet.health

            var d = lastDate.plusDays(1)
            while (d.isBefore(today)) {
                val dateStr = d.format(dateFormatter)
                val habitsWithLogs = repository.getHabitsWithLogsForDateSuspend(dateStr)
                if (habitsWithLogs.isNotEmpty()) {
                    val completedCount = habitsWithLogs.count { it.isCompleted == true }
                    if (completedCount == 0) {
                        currentHealth = (currentHealth - 10).coerceAtLeast(0)
                    } else {
                        val missedCount = habitsWithLogs.size - completedCount
                        currentHealth = (currentHealth - (2 * missedCount)).coerceAtLeast(0)
                    }
                }
                d = d.plusDays(1)
            }

            val updatedPet = pet.copy(
                health = currentHealth,
                lastCheckDate = todayStr
            )
            repository.updatePetState(updatedPet)
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    private fun calculateLevel(xp: Int): Int {
        return when {
            xp >= 700 -> 4
            xp >= 300 -> 3
            xp >= 100 -> 2
            else -> 1
        }
    }

    fun toggleHabit(habitId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(dateFormatter)

            val listBefore = repository.getHabitsWithLogsForDateSuspend(dateStr)
            val totalHabits = listBefore.size
            val completedBefore = listBefore.count { it.isCompleted == true }

            repository.toggleHabit(habitId, dateStr, isCompleted)

            val pet = repository.getPetState().first() ?: PetState(lastCheckDate = dateStr)

            val xpChange: Int
            val hpChange: Int

            if (isCompleted) {
                hpChange = 5
                var tempXp = 0
                if (pet.health > 0) {
                    tempXp = 15
                }
                if (totalHabits > 0 && completedBefore == totalHabits - 1) {
                    if (pet.health > 0) {
                        tempXp += 30
                    }
                }
                xpChange = tempXp
            } else {
                hpChange = -5
                var tempXp = -15
                if (totalHabits > 0 && completedBefore == totalHabits) {
                    tempXp -= 30
                }
                xpChange = tempXp
            }

            val newHealth = (pet.health + hpChange).coerceIn(0, 100)
            val newXp = (pet.xp + xpChange).coerceAtLeast(0)
            val newLevel = calculateLevel(newXp)

            if (newLevel > pet.level) {
                _evolutionEvent.emit(Pair(pet.level, newLevel))
            }

            val updatedPet = pet.copy(
                health = newHealth,
                xp = newXp,
                level = newLevel
            )
            repository.updatePetState(updatedPet)
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

    fun archiveHabit(habitId: Long) {
        viewModelScope.launch {
            repository.archiveHabit(habitId)
            NotificationHelper.cancelReminder(getApplication(), habitId)
        }
    }
}

// Since I don't have a DI framework like Hilt set up, I'll need a Factory or just a simple provider for now.
// For the sake of this CLI-generated project, I'll assume a simple manual injection in MainActivity.

class HabitViewModelFactory(private val application: Application, private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
