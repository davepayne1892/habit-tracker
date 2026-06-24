package com.example.habittracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitLog
import com.example.habittracker.data.model.WeightLog
import com.example.habittracker.ui.components.BrutalButton
import com.example.habittracker.ui.components.Heatmap
import com.example.habittracker.ui.components.WeightTrendChart
import com.example.habittracker.ui.theme.*
import com.example.habittracker.ui.viewmodel.HabitViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailsScreen(
    habitId: Long,
    viewModel: HabitViewModel,
    onBack: () -> Unit
) {
    var habit by remember { mutableStateOf<Habit?>(null) }
    val logs by viewModel.getLogsForHabit(habitId).collectAsState(initial = emptyList())
    val weightLogs by viewModel.getWeightLogsForHabit(habitId).collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogWeightDialog by remember { mutableStateOf(false) }

    LaunchedEffect(habitId) {
        habit = viewModel.getHabitById(habitId)
    }

    if (showLogWeightDialog) {
        val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val todayLog = weightLogs.find { it.date == todayStr }
        LogWeightDialog(
            initialWeight = todayLog?.weightKg,
            onDismiss = { showLogWeightDialog = false },
            onSave = { weight ->
                viewModel.logWeight(habitId, todayStr, weight)
                showLogWeightDialog = false
            }
        )
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (habit != null) {
                if (habit!!.description.isNotEmpty()) {
                    Text(
                        text = habit!!.description,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Heatmap(logs = logs, modifier = Modifier.fillMaxWidth())

                if (habit!!.isWeight) {
                    WeightTrendChart(weightLogs = weightLogs, modifier = Modifier.fillMaxWidth())

                    val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val todayLog = weightLogs.find { it.date == todayStr }

                    BrutalButton(
                        text = if (todayLog != null) "UPDATE TODAY'S WEIGHT (${todayLog.weightKg} KG)" else "LOG TODAY'S WEIGHT",
                        onClick = { showLogWeightDialog = true },
                        backgroundColor = BrutalYellow,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (weightLogs.isNotEmpty()) {
                        Text(
                            text = "WEIGHT HISTORY",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            weightLogs.sortedByDescending { it.date }.forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(2.dp, Black)
                                        .background(Color.White)
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = log.date,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${log.weightKg} kg",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            color = BrutalMagenta
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.removeWeightLog(habitId, log.date)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete entry",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                BrutalButton(
                    text = "CLOSE",
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
