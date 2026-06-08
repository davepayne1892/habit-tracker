package com.example.habittracker.ui.screens

import androidx.compose.foundation.border
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
import com.example.habittracker.data.model.HabitLog
import com.example.habittracker.ui.components.BrutalButton
import com.example.habittracker.ui.components.Heatmap
import com.example.habittracker.ui.theme.BrutalMagenta
import com.example.habittracker.ui.theme.BrutalGreen
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
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(habitId) {
        habit = viewModel.getHabitById(habitId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("REMOVE '${habit?.name?.uppercase()}'?", fontWeight = FontWeight.Black) },
            text = { Text("ARCHIVING HIDES THE HABIT FROM YOUR DAILY LIST BUT PRESERVES SPIKE'S XP AND CALENDAR HISTORY. DELETING WILL PERMANENTLY REMOVE ALL HISTORY.") },
            confirmButton = {
                BrutalButton(
                    text = "ARCHIVE",
                    onClick = {
                        habit?.let {
                            viewModel.archiveHabit(it.id)
                            showDeleteDialog = false
                            onBack()
                        }
                    },
                    backgroundColor = BrutalGreen,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            },
            dismissButton = {
                BrutalButton(
                    text = "DELETE PERMANENTLY",
                    onClick = {
                        habit?.let {
                            viewModel.deleteHabit(it)
                            showDeleteDialog = false
                            onBack()
                        }
                    },
                    backgroundColor = BrutalMagenta,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            containerColor = Color.White,
            modifier = Modifier.border(2.5.dp, Color.Black)
        )
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
                    IconButton(onClick = { showDeleteDialog = true }) {
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
