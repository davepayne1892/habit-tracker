package com.example.habittracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.ui.components.BrutalButton
import com.example.habittracker.ui.theme.BrutalYellow
import com.example.habittracker.ui.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    viewModel: HabitViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf<String?>(null) } // Format "HH:mm"
    var isWeight by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NEW HABIT", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrutalYellow)
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("NAME", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("DESCRIPTION (OPTIONAL)", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
            )

            OutlinedTextField(
                value = reminderTime ?: "",
                onValueChange = { reminderTime = it },
                label = { Text("REMINDER TIME (HH:MM)", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 08:30") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isWeight = !isWeight }
                    .border(2.5.dp, Color.Black)
                    .background(if (isWeight) BrutalYellow else Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "WEIGHT TRACKING HABIT",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Checkbox(
                    checked = isWeight,
                    onCheckedChange = { isWeight = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Black,
                        uncheckedColor = Color.Transparent,
                        checkmarkColor = BrutalYellow
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            BrutalButton(
                text = "SAVE HABIT",
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addHabit(name, description, reminderTime, isWeight)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BrutalYellow
            )
        }
    }
}
