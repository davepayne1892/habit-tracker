package com.example.habittracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.data.model.Habit
import com.example.habittracker.ui.components.BrutalButton
import com.example.habittracker.ui.components.Heatmap
import com.example.habittracker.ui.theme.BrutalMagenta
import com.example.habittracker.ui.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailsScreen(
    habitId: Long,
    viewModel: HabitViewModel,
    onBack: () -> Unit
) {
    var habit by remember { mutableStateOf<Habit?>(null) }
    val logs by viewModel.getLogsForHabit(habitId).collectAsState(initial = emptyList())

    LaunchedEffect(habitId) {
        habit = viewModel.getHabitById(habitId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(habit?.name?.uppercase() ?: "DETAILS", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        habit?.let {
                            viewModel.deleteHabit(it)
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Habit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrutalMagenta)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (habit != null) {
                Text(
                    text = habit!!.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Heatmap(logs = logs, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.weight(1f))

                BrutalButton(
                    text = "CLOSE",
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}
