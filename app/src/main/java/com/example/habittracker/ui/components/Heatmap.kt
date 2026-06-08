package com.example.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.data.model.HabitLog
import com.example.habittracker.ui.theme.Black
import com.example.habittracker.ui.theme.BrutalGreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun Heatmap(
    logs: List<HabitLog>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val startDate = today.minusDays(364) // Last 365 days
    val logsMap = logs.associateBy { it.date }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val days = (0..364).map { startDate.plusDays(it.toLong()) }

    Column(modifier = modifier) {
        Text(
            "PROGRESS HEATMAP",
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        BrutalCard(backgroundColor = Color.White) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(7),
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(days) { date ->
                    val dateString = date.format(dateFormatter)
                    val isCompleted = logsMap[dateString]?.isCompleted == true
                    
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(if (isCompleted) BrutalGreen else Color.LightGray)
                            .border(1.dp, Black)
                    )
                }
            }
        }
    }
}
