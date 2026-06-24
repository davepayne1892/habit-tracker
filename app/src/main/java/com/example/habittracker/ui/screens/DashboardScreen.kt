package com.example.habittracker.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.data.dao.HabitWithLog
import com.example.habittracker.ui.components.*
import com.example.habittracker.ui.theme.*
import com.example.habittracker.ui.viewmodel.HabitViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HabitViewModel,
    onAddHabitClick: () -> Unit,
    onHabitClick: (Long) -> Unit
) {
    val habits by viewModel.habitsForSelectedDate.collectAsState(initial = emptyList())
    val selectedDate by viewModel.selectedDate.collectAsState()
    val allHabits by viewModel.allHabits.collectAsState(initial = emptyList())
    val allLogs by viewModel.allLogs.collectAsState(initial = emptyList())
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            BrutalTopBar(
                title = "HABITHOG",
                menuExpanded = menuExpanded,
                onMenuToggle = { menuExpanded = it },
                onAddHabitClick = onAddHabitClick
            )
        }
    ) { padding ->
        val petState by viewModel.petState.collectAsState(initial = null)
        var evolutionLevelPair by remember { mutableStateOf<Pair<Int, Int>?>(null) }
        var activeWeightHabitId by remember { mutableStateOf<Long?>(null) }
        var showDeleteWeightDialogId by remember { mutableStateOf<Long?>(null) }

        LaunchedEffect(Unit) {
            viewModel.evolutionEvent.collect { pair ->
                evolutionLevelPair = pair
            }
        }

        evolutionLevelPair?.let { (oldLevel, newLevel) ->
            EvolutionDialog(
                oldLevel = oldLevel,
                newLevel = newLevel,
                onDismiss = { evolutionLevelPair = null }
            )
        }

        activeWeightHabitId?.let { habitId ->
            LogWeightDialog(
                initialWeight = null,
                onDismiss = { activeWeightHabitId = null },
                onSave = { weight ->
                    viewModel.logWeight(habitId, selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), weight)
                    activeWeightHabitId = null
                }
            )
        }

        showDeleteWeightDialogId?.let { habitId ->
            AlertDialog(
                onDismissRequest = { showDeleteWeightDialogId = null },
                title = { Text("REMOVE WEIGHT LOG?", fontWeight = FontWeight.Black) },
                text = { Text("THIS WILL DELETE TODAY'S WEIGHT LOG AND MARK THE HABIT AS INCOMPLETE.") },
                confirmButton = {
                    BrutalButton(
                        text = "DELETE",
                        onClick = {
                            viewModel.removeWeightLog(habitId, selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            showDeleteWeightDialogId = null
                        },
                        backgroundColor = BrutalMagenta,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                },
                dismissButton = {
                    BrutalButton(
                        text = "CANCEL",
                        onClick = { showDeleteWeightDialogId = null },
                        backgroundColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                containerColor = Color.White,
                modifier = Modifier.border(2.5.dp, Color.Black)
            )
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .halftoneDots(Color.Black.copy(alpha = 0.04f), dotRadius = 1.5.dp, spacing = 10.dp)
                .padding(16.dp)
        ) {
            petState?.let {
                PetWidget(
                    petState = it,
                    allHabits = allHabits,
                    allLogs = allLogs,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Horizontal Date Picker Scroll
            DatePickerBar(
                selectedDate = selectedDate,
                onDateSelected = { viewModel.selectDate(it) },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Progress Header Summary Card
            ProgressHeader(
                habits = habits,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = selectedDate.format(dateFormatter).uppercase(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (habits.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    BrutalCard(
                        modifier = Modifier.padding(16.dp),
                        backgroundColor = BrutalYellow
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "NO HABITS YET!",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "TAP THE '+' BUTTON TO CREATE A NEW HABIT AND START TRACKING.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    itemsIndexed(habits) { index, habitWithLog ->
                        HabitItem(
                            habitWithLog = habitWithLog,
                            index = index,
                            onToggle = { isChecked ->
                                if (habitWithLog.habit.isWeight) {
                                    if (isChecked) {
                                        activeWeightHabitId = habitWithLog.habit.id
                                    } else {
                                        showDeleteWeightDialogId = habitWithLog.habit.id
                                    }
                                } else {
                                    viewModel.toggleHabit(habitWithLog.habit.id, isChecked)
                                }
                            },
                            onClick = { onHabitClick(habitWithLog.habit.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerBar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val dates = remember {
        (-7..7).map { today.plusDays(it.toLong()) }
    }

    val selectedIndex = remember(selectedDate) {
        dates.indexOf(selectedDate).coerceAtLeast(0)
    }

    val listState = rememberLazyListState()

    // Scroll to center the selected date when it changes or on load
    LaunchedEffect(selectedDate) {
        val scrollIndex = (selectedIndex - 2).coerceAtLeast(0)
        listState.scrollToItem(scrollIndex)
    }

    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dates) { date ->
            val isSelected = date == selectedDate
            val dayOfWeekName = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()).uppercase()
            val dayOfMonth = date.dayOfMonth.toString()

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .clickable { onDateSelected(date) }
                    .brutalShadow(
                        color = if (isSelected) Black else Color.Transparent,
                        offsetX = 3.dp,
                        offsetY = 3.dp
                    )
                    .background(if (isSelected) BrutalYellow else Color.White)
                    .border(2.dp, Black)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = dayOfWeekName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dayOfMonth,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Black
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressHeader(
    habits: List<HabitWithLog>,
    modifier: Modifier = Modifier
) {
    val total = habits.size
    val completed = habits.count { it.isCompleted == true }
    val progress = if (total > 0) completed.toFloat() / total else 0f
    val percentage = (progress * 100).toInt()

    BrutalCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = BrutalCyan
    ) {
        Column {
            Text(
                text = "TODAY'S SUMMARY",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (total == 0) "NO HABITS DEFINED!" else "$completed / $total COMPLETED ($percentage%)",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(Color.White)
                    .border(2.dp, Black)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(BrutalGreen)
                        .border(
                            width = if (progress > 0f) 2.dp else 0.dp,
                            color = Black
                        )
                )
            }
        }
    }
}

@Composable
fun HabitItem(
    habitWithLog: HabitWithLog,
    index: Int,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val isCompleted = habitWithLog.isCompleted ?: false
    val colors = listOf(BrutalYellow, BrutalCyan, BrutalMagenta)
    val cardColor = if (isCompleted) {
        BrutalGreen
    } else {
        colors[(habitWithLog.habit.id % colors.size).toInt()]
    }

    val rotation = if (index % 2 == 0) 1.2f else -1.2f

    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 200f
        )
    )

    BrutalCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { rotationZ = rotation }
            .clickable(onClick = onClick),
        backgroundColor = cardColor
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

            IconButton(
                onClick = { onToggle(!isCompleted) },
                modifier = Modifier
                    .scale(scale)
                    .size(50.dp)
            ) {
                if (isCompleted) {
                    ComicBurst(
                        text = "BAM!",
                        backgroundColor = BrutalYellow,
                        textColor = Black
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.White, shape = CircleShape)
                            .border(2.5.dp, Black, shape = CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun BrutalTopBar(
    title: String,
    menuExpanded: Boolean,
    onMenuToggle: (Boolean) -> Unit,
    onAddHabitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BrutalYellow)
            .halftoneDots(Color.Black.copy(alpha = 0.08f), dotRadius = 2.dp, spacing = 8.dp)
            .drawBehind {
                // Draw a thick black line at the bottom for a strong brutalist shadow border
                drawLine(
                    color = Black,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 4.dp.toPx()
                )
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Title + Neo-Brutalist Accent Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title.uppercase(),
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = 2.sp,
                    color = Black
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .background(BrutalMagenta)
                        .border(1.5.dp, Black)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "DO IT!",
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }

            // Right: Options Menu
            Box {
                IconButton(onClick = { onMenuToggle(true) }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { onMenuToggle(false) },
                    modifier = Modifier
                        .background(Color.White)
                        .border(2.dp, Black)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "ADD NEW HABIT",
                                fontWeight = FontWeight.Black,
                                fontFamily = SpaceGrotesk
                            )
                        },
                        onClick = {
                            onMenuToggle(false)
                            onAddHabitClick()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogWeightDialog(
    initialWeight: Double?,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var weightInput by remember { mutableStateOf(initialWeight?.toString() ?: "") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "LOG WEIGHT",
                fontWeight = FontWeight.Black,
                fontFamily = SpaceGrotesk,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "ENTER YOUR WEIGHT IN KG:",
                    fontWeight = FontWeight.Bold,
                    fontFamily = SpaceGrotesk,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = {
                        weightInput = it
                        isError = it.toDoubleOrNull() == null
                    },
                    label = { Text("WEIGHT (KG)", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black
                    )
                )
                if (isError) {
                    Text(
                        text = "PLEASE ENTER A VALID NUMBER",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            BrutalButton(
                text = "SAVE",
                onClick = {
                    val weight = weightInput.toDoubleOrNull()
                    if (weight != null && weight > 0) {
                        onSave(weight)
                    } else {
                        isError = true
                    }
                },
                backgroundColor = BrutalGreen,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        dismissButton = {
            BrutalButton(
                text = "CANCEL",
                onClick = onDismiss,
                backgroundColor = Color.White
            )
        },
        containerColor = Color.White,
        modifier = Modifier.border(2.5.dp, Color.Black)
    )
}


