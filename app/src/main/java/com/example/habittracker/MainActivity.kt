package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
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

        // Request POST_NOTIFICATIONS runtime permission on Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), 101)
            }
        }
        
        val database = AppDatabase.getDatabase(this)
        val repository = HabitRepository(database.habitDao(), database.petDao())
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
