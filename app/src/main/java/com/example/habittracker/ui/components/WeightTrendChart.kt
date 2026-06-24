package com.example.habittracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.data.model.WeightLog
import com.example.habittracker.ui.theme.Black
import com.example.habittracker.ui.theme.BrutalMagenta
import com.example.habittracker.ui.theme.BrutalYellow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeightTrendChart(
    weightLogs: List<WeightLog>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Column(modifier = modifier) {
        Text(
            "WEIGHT TREND OVER TIME",
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BrutalCard(backgroundColor = Color.White) {
            if (weightLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO WEIGHT DATA YET. LOG YOUR WEIGHT DAILY!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            } else {
                val sortedLogs = weightLogs.sortedBy { it.date }
                val weights = sortedLogs.map { it.weightKg }
                
                val minVal = weights.minOrNull() ?: 0.0
                val maxVal = weights.maxOrNull() ?: 100.0
                
                // Add padding so chart points aren't cut off at top/bottom
                val minWeight = if (minVal == maxVal) minVal - 5.0 else minVal - (maxVal - minVal) * 0.2
                val maxWeight = if (minVal == maxVal) maxVal + 5.0 else maxVal + (maxVal - minVal) * 0.2
                val weightRange = maxWeight - minWeight

                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val outputFormatter = DateTimeFormatter.ofPattern("MM/dd")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        val paddingLeft = 40.dp.toPx()
                        val paddingBottom = 25.dp.toPx()
                        val paddingTop = 20.dp.toPx()
                        val paddingRight = 20.dp.toPx()

                        val chartWidth = width - paddingLeft - paddingRight
                        val chartHeight = height - paddingTop - paddingBottom

                        // 1. Draw horizontal grid lines and Y-axis labels
                        val numGridLines = 4
                        for (i in 0..numGridLines) {
                            val ratio = i.toFloat() / numGridLines
                            val y = paddingTop + ratio * chartHeight
                            
                            // Grid line
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(paddingLeft, y),
                                end = Offset(width - paddingRight, y),
                                strokeWidth = 1.dp.toPx()
                            )

                            // Y value text
                            val weightVal = maxWeight - (ratio * weightRange)
                            val labelText = String.format("%.1f", weightVal)
                            
                            drawText(
                                textMeasurer = textMeasurer,
                                text = labelText,
                                style = TextStyle(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Black
                                ),
                                topLeft = Offset(5.dp.toPx(), y - 7.dp.toPx())
                            )
                        }

                        // 2. Draw line path and points
                        if (sortedLogs.isNotEmpty()) {
                            val points = sortedLogs.mapIndexed { idx, log ->
                                val xRatio = if (sortedLogs.size > 1) idx.toFloat() / (sortedLogs.size - 1) else 0.5f
                                val yRatio = ((maxWeight - log.weightKg) / weightRange).toFloat()
                                
                                val px = paddingLeft + xRatio * chartWidth
                                val py = paddingTop + yRatio * chartHeight
                                Offset(px, py)
                            }

                            // Draw trend line if there is more than 1 point
                            if (points.size > 1) {
                                val path = Path().apply {
                                    moveTo(points[0].x, points[0].y)
                                    for (i in 1 until points.size) {
                                        lineTo(points[i].x, points[i].y)
                                    }
                                }
                                
                                // Draw shadow line first (brutalist offset)
                                val shadowPath = Path().apply {
                                    moveTo(points[0].x + 2.dp.toPx(), points[0].y + 2.dp.toPx())
                                    for (i in 1 until points.size) {
                                        lineTo(points[i].x + 2.dp.toPx(), points[i].y + 2.dp.toPx())
                                    }
                                }
                                drawPath(
                                    path = shadowPath,
                                    color = Color.LightGray,
                                    style = Stroke(width = 3.dp.toPx())
                                )

                                drawPath(
                                    path = path,
                                    color = Black,
                                    style = Stroke(width = 3.dp.toPx())
                                )
                            }

                            // Draw circles and weight labels at each point
                            points.forEachIndexed { idx, point ->
                                val log = sortedLogs[idx]
                                
                                // Point shadow
                                drawCircle(
                                    color = Black.copy(alpha = 0.3f),
                                    radius = 5.dp.toPx(),
                                    center = Offset(point.x + 1.5.dp.toPx(), point.y + 1.5.dp.toPx())
                                )
                                
                                // Point circle
                                drawCircle(
                                    color = BrutalMagenta,
                                    radius = 5.dp.toPx(),
                                    center = point
                                )
                                // Point outline
                                drawCircle(
                                    color = Black,
                                    radius = 5.dp.toPx(),
                                    center = point,
                                    style = Stroke(width = 2.dp.toPx())
                                )

                                // Draw weight value above the point
                                val weightLabel = String.format("%.1f kg", log.weightKg)
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = weightLabel,
                                    style = TextStyle(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Black
                                    ),
                                    topLeft = Offset(point.x - 15.dp.toPx(), point.y - 18.dp.toPx())
                                )

                                // Draw X date label below
                                val parsedDate = LocalDate.parse(log.date, inputFormatter)
                                val xLabel = parsedDate.format(outputFormatter)
                                
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = xLabel,
                                    style = TextStyle(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Black
                                    ),
                                    topLeft = Offset(point.x - 12.dp.toPx(), height - paddingBottom + 5.dp.toPx())
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
