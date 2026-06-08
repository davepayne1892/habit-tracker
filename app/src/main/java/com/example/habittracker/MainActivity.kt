package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habittracker.data.AppDatabase
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.screens.AddHabitScreen
import com.example.habittracker.ui.screens.DashboardScreen
import com.example.habittracker.ui.screens.HabitDetailsScreen
import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.example.habittracker.ui.viewmodel.HabitViewModel
import com.example.habittracker.ui.viewmodel.HabitViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = HabitRepository(database.habitDao())
        val viewModelFactory = HabitViewModelFactory(application, repository)

        setContent {
            HabitTrackerTheme {
                val viewModel: HabitViewModel = viewModel(factory = viewModelFactory)
                HabitTrackerApp(viewModel)
            }
        }
    }
}

@Composable
fun HabitTrackerApp(viewModel: HabitViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onAddHabitClick = { navController.navigate("add_habit") },
                onHabitClick = { habitId -> 
                    navController.navigate("habit_details/$habitId")
                }
            )
        }
        composable("add_habit") {
            AddHabitScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("habit_details/{habitId}") { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId")?.toLongOrNull()
            if (habitId != null) {
                HabitDetailsScreen(
                    habitId = habitId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

import androidx.compose.material3.Text
