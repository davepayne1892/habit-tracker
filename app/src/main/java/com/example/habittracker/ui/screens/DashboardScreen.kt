package com.example.habittracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.data.dao.HabitWithLog
import com.example.habittracker.ui.components.BrutalCard
import com.example.habittracker.ui.theme.BrutalCyan
import com.example.habittracker.ui.theme.BrutalGreen
import com.example.habittracker.ui.viewmodel.HabitViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HabitViewModel,
    onAddHabitClick: () -> Unit,
    onHabitClick: (Long) -> Unit
) {
    val habits by viewModel.habitsForSelectedDate.collectAsState(initial = emptyList())
    val selectedDate by viewModel.selectedDate.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "HABITS",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = onAddHabitClick,
                containerColor = BrutalCyan,
                contentColor = Color.Black,
                shape = MaterialTheme.shapes.medium // We could customize this for brutalism
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit", modifier = Modifier.size(36.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = selectedDate.format(dateFormatter).uppercase(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(habits) { habitWithLog ->
                    HabitItem(
                        habitWithLog = habitWithLog,
                        onToggle = { viewModel.toggleHabit(habitWithLog.habit.id, it) },
                        onClick = { onHabitClick(habitWithLog.habit.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HabitItem(
    habitWithLog: HabitWithLog,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val isCompleted = habitWithLog.isCompleted ?: false
    
    BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isCompleted) BrutalGreen else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habitWithLog.habit.name.uppercase(),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                if (habitWithLog.habit.description.isNotEmpty()) {
                    Text(
                        text = habitWithLog.habit.description,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(onClick = { onToggle(!isCompleted) }) {
                Icon(
                    imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = "Toggle Habit",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black
                )
            }
        }
    }
}
