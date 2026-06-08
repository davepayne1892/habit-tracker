package com.example.habittracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitLog
import com.example.habittracker.data.model.PetState
import com.example.habittracker.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PetWidget(
    petState: PetState,
    allHabits: List<Habit>,
    allLogs: List<HabitLog>,
    modifier: Modifier = Modifier
) {
    val speechText = when {
        petState.health == 0 -> "ZZZ... (FAINTED! HELP ME!)"
        petState.health < 30 -> "I'M STARVING... FEED ME HABITS!"
        petState.health >= 70 -> "FEELING STRONG! BAM!"
        else -> "KEEP IT UP! WE GOT THIS!"
    }

    val maxLvlXp = when (petState.level) {
        1 -> 100
        2 -> 200 // 100 to 300
        3 -> 400 // 300 to 700
        else -> 1000 // Max level
    }

    val currentLvlXp = when (petState.level) {
        1 -> petState.xp
        2 -> petState.xp - 100
        3 -> petState.xp - 300
        else -> maxLvlXp
    }

    val progress = if (petState.level >= 4) 1f else (currentLvlXp.toFloat() / maxLvlXp).coerceIn(0f, 1f)

    BrutalCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = BrutalYellow
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left: animated Hedgehog Sprite + Heatmap Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .border(2.dp, Black, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    HedgehogSprite(
                        level = petState.level,
                        health = petState.health,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                CombinedMiniHeatmap(
                    allHabits = allHabits,
                    allLogs = allLogs,
                    modifier = Modifier.width(90.dp)
                )
            }

            // Right: stats
            Column(modifier = Modifier.weight(1f)) {
                // Name + Level
                Text(
                    text = "${petState.name} (LVL. ${petState.level})",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Speech Bubble
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(4.dp))
                        .border(1.5.dp, Black, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = speechText,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        color = Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Health Progress Bar
                Text(
                    text = "HEALTH: ${petState.health}/100",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(Color.White)
                        .border(1.5.dp, Black)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(petState.health / 100f)
                            .background(if (petState.health < 30) BrutalMagenta else BrutalGreen)
                            .border(
                                width = if (petState.health > 0) 1.5.dp else 0.dp,
                                color = Black
                            )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // XP Progress Bar
                Text(
                    text = if (petState.level >= 4) "MAX LEVEL reached!" else "XP: $currentLvlXp/$maxLvlXp",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(Color.White)
                        .border(1.5.dp, Black)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(BrutalCyan)
                            .border(
                                width = if (progress > 0f) 1.5.dp else 0.dp,
                                color = Black
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun HedgehogSprite(
    level: Int,
    health: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hedgehog")

    val bobOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    Canvas(modifier = modifier.padding(4.dp)) {
        val width = size.width
        val height = size.height
        val isFainted = health == 0
        val isSick = health in 1..29

        // Adjust coordinates
        val centerY = (height * 0.55f) + (bobOffset * 3.dp.toPx())
        val centerX = width * 0.45f

        val baseBodyRadius = when (level) {
            1 -> width * 0.24f
            2 -> width * 0.28f
            3 -> width * 0.30f
            else -> width * 0.32f
        }
        val bodyRadiusY = baseBodyRadius * breathScale
        val bodyRadiusX = baseBodyRadius / breathScale

        val outlineColor = Color.Black
        val strokeWidth = 2.dp.toPx()

        val faceColor = if (isFainted) Color(0xFFD3D3D3) else if (isSick) Color(0xFFC3DCA0) else Color(0xFFFFDAB9)
        val spikeColor = when {
            isFainted -> Color(0xFF808080)
            isSick -> Color(0xFF8B7355)
            level == 4 -> Color(0xFFFFC107)
            else -> Color(0xFF4F4F4F)
        }

        // Draw Cape (Level 4 Guardian)
        if (level == 4 && !isFainted) {
            val capePath = Path().apply {
                moveTo(centerX - bodyRadiusX * 0.8f, centerY)
                lineTo(centerX - bodyRadiusX * 2.0f, centerY + bodyRadiusY * 0.6f)
                lineTo(centerX - bodyRadiusX * 1.5f, centerY + bodyRadiusY * 1.2f)
                lineTo(centerX - bodyRadiusX * 0.2f, centerY + bodyRadiusY * 0.8f)
                close()
            }
            drawPath(capePath, Color(0xFFE50914))
            drawPath(capePath, outlineColor, style = Stroke(width = strokeWidth))
        }

        // Generate spikes path
        val spikePath = Path()
        val numSpikes = when (level) {
            1 -> 6
            2 -> 10
            3 -> 14
            else -> 18
        }

        val startAngle = 100f
        val endAngle = 320f
        val angleStep = (endAngle - startAngle) / (numSpikes - 1)

        for (i in 0 until numSpikes) {
            val angleDeg = startAngle + i * angleStep
            val angleRad = Math.toRadians(angleDeg.toDouble())

            val xBase1 = centerX + (bodyRadiusX * 0.9f) * cos(angleRad).toFloat()
            val yBase1 = centerY + (bodyRadiusY * 0.9f) * sin(angleRad).toFloat()

            val angleRadNext = Math.toRadians((angleDeg + angleStep / 2).toDouble())
            val spikeLength = when (level) {
                1 -> bodyRadiusX * 0.25f
                2 -> bodyRadiusX * 0.35f
                3 -> bodyRadiusX * 0.45f
                else -> bodyRadiusX * 0.55f
            }
            val xPeak = centerX + (bodyRadiusX + spikeLength) * cos(angleRadNext).toFloat()
            val yPeak = centerY + (bodyRadiusY + spikeLength) * sin(angleRadNext).toFloat()

            val angleRadEnd = Math.toRadians((angleDeg + angleStep).toDouble())
            val xBase2 = centerX + (bodyRadiusX * 0.9f) * cos(angleRadEnd).toFloat()
            val yBase2 = centerY + (bodyRadiusY * 0.9f) * sin(angleRadEnd).toFloat()

            if (i == 0) {
                spikePath.moveTo(xBase1, yBase1)
            }
            spikePath.lineTo(xPeak, yPeak)
            spikePath.lineTo(xBase2, yBase2)
        }
        spikePath.close()

        drawPath(spikePath, spikeColor)
        drawPath(spikePath, outlineColor, style = Stroke(width = strokeWidth))

        // Draw Feet (Levels 2, 3, 4)
        if (level > 1) {
            val footRadius = 5.dp.toPx()
            val footY = centerY + bodyRadiusY * 0.9f

            // Left foot
            drawCircle(Color.Black, radius = footRadius, center = Offset(centerX - bodyRadiusX * 0.4f + 1.dp.toPx(), footY + 1.dp.toPx()))
            drawCircle(faceColor, radius = footRadius, center = Offset(centerX - bodyRadiusX * 0.4f, footY))
            drawCircle(outlineColor, radius = footRadius, center = Offset(centerX - bodyRadiusX * 0.4f, footY), style = Stroke(width = strokeWidth))

            // Right foot
            drawCircle(Color.Black, radius = footRadius, center = Offset(centerX + bodyRadiusX * 0.2f + 1.dp.toPx(), footY + 1.dp.toPx()))
            drawCircle(faceColor, radius = footRadius, center = Offset(centerX + bodyRadiusX * 0.2f, footY))
            drawCircle(outlineColor, radius = footRadius, center = Offset(centerX + bodyRadiusX * 0.2f, footY), style = Stroke(width = strokeWidth))
        }

        // Draw Body Shape (Face profile snout)
        val bodyPath = Path().apply {
            val snoutX = centerX + bodyRadiusX * 1.3f
            val snoutY = centerY + bodyRadiusY * 0.2f

            moveTo(centerX - bodyRadiusX * 0.5f, centerY + bodyRadiusY * 0.8f)
            val leftBound = centerX - bodyRadiusX
            val topBound = centerY - bodyRadiusY
            val rightBound = centerX + bodyRadiusX
            val bottomBound = centerY + bodyRadiusY

            arcTo(
                rect = androidx.compose.ui.geometry.Rect(leftBound, topBound, rightBound, bottomBound),
                startAngleDegrees = 120f,
                sweepAngleDegrees = 220f,
                forceMoveTo = false
            )
            lineTo(snoutX, snoutY)
            lineTo(centerX + bodyRadiusX * 0.8f, centerY + bodyRadiusY * 0.6f)
            close()
        }

        drawPath(bodyPath, faceColor)
        drawPath(bodyPath, outlineColor, style = Stroke(width = strokeWidth))

        // Snout Nose Tip
        val noseRadius = 4.dp.toPx()
        val noseX = centerX + bodyRadiusX * 1.3f
        val noseY = centerY + bodyRadiusY * 0.2f
        drawCircle(outlineColor, radius = noseRadius, center = Offset(noseX, noseY))

        // Eye Coordinates
        val eyeX = centerX + bodyRadiusX * 0.4f
        val eyeY = centerY - bodyRadiusY * 0.1f
        val eyeRadius = when (level) {
            1 -> 2.5.dp.toPx()
            else -> 5.dp.toPx()
        }

        if (isFainted) {
            val sizeX = 4.dp.toPx()
            drawLine(outlineColor, Offset(eyeX - sizeX, eyeY - sizeX), Offset(eyeX + sizeX, eyeY + sizeX), strokeWidth = 1.5.dp.toPx())
            drawLine(outlineColor, Offset(eyeX - sizeX, eyeY + sizeX), Offset(eyeX + sizeX, eyeY - sizeX), strokeWidth = 1.5.dp.toPx())
            if (level > 2) {
                val secondEyeX = eyeX - bodyRadiusX * 0.4f
                drawLine(outlineColor, Offset(secondEyeX - sizeX, eyeY - sizeX), Offset(secondEyeX + sizeX, eyeY + sizeX), strokeWidth = 1.5.dp.toPx())
                drawLine(outlineColor, Offset(secondEyeX - sizeX, eyeY + sizeX), Offset(secondEyeX + sizeX, eyeY - sizeX), strokeWidth = 1.5.dp.toPx())
            }
        } else if (isSick) {
            drawLine(outlineColor, Offset(eyeX - 3.dp.toPx(), eyeY - 1.5.dp.toPx()), Offset(eyeX + 3.dp.toPx(), eyeY + 1.5.dp.toPx()), strokeWidth = 2.dp.toPx())
            if (level > 2) {
                val secondEyeX = eyeX - bodyRadiusX * 0.4f
                drawLine(outlineColor, Offset(secondEyeX - 3.dp.toPx(), eyeY + 1.5.dp.toPx()), Offset(secondEyeX + 3.dp.toPx(), eyeY - 1.5.dp.toPx()), strokeWidth = 2.dp.toPx())
            }
        } else if (level == 1) {
            val eyePath = Path().apply {
                moveTo(eyeX - 3.dp.toPx(), eyeY - 1.5.dp.toPx())
                quadraticBezierTo(eyeX, eyeY + 1.5.dp.toPx(), eyeX + 3.dp.toPx(), eyeY - 1.5.dp.toPx())
            }
            drawPath(eyePath, outlineColor, style = Stroke(width = 1.5.dp.toPx()))
        } else {
            drawCircle(Color.White, radius = eyeRadius, center = Offset(eyeX, eyeY))
            drawCircle(outlineColor, radius = eyeRadius, center = Offset(eyeX, eyeY), style = Stroke(width = 1.dp.toPx()))
            drawCircle(Color.Black, radius = eyeRadius * 0.5f, center = Offset(eyeX + eyeRadius * 0.2f, eyeY))
            drawCircle(Color.White, radius = eyeRadius * 0.15f, center = Offset(eyeX - eyeRadius * 0.2f, eyeY - eyeRadius * 0.2f))

            if (level > 2) {
                val secondEyeX = eyeX - bodyRadiusX * 0.4f
                drawCircle(Color.White, radius = eyeRadius, center = Offset(secondEyeX, eyeY))
                drawCircle(outlineColor, radius = eyeRadius, center = Offset(secondEyeX, eyeY), style = Stroke(width = 1.dp.toPx()))
                drawCircle(Color.Black, radius = eyeRadius * 0.5f, center = Offset(secondEyeX + eyeRadius * 0.2f, eyeY))
                drawCircle(Color.White, radius = eyeRadius * 0.15f, center = Offset(secondEyeX - eyeRadius * 0.2f, eyeY - eyeRadius * 0.2f))
            }
        }

        // Accessories (Mask / Bandana)
        if (!isFainted) {
            if (level == 3) {
                val bandanaPath = Path().apply {
                    moveTo(centerX - bodyRadiusX * 0.2f, centerY - bodyRadiusY * 0.8f)
                    lineTo(centerX + bodyRadiusX * 0.6f, centerY - bodyRadiusY * 0.4f)
                    lineTo(centerX + bodyRadiusX * 0.5f, centerY - bodyRadiusY * 0.2f)
                    lineTo(centerX - bodyRadiusX * 0.3f, centerY - bodyRadiusY * 0.6f)
                    close()
                }
                drawPath(bandanaPath, Color(0xFFD32F2F))
                drawPath(bandanaPath, outlineColor, style = Stroke(width = strokeWidth))

                val knotPath = Path().apply {
                    moveTo(centerX - bodyRadiusX * 0.2f, centerY - bodyRadiusY * 0.8f)
                    lineTo(centerX - bodyRadiusX * 0.5f, centerY - bodyRadiusY * 0.9f)
                    lineTo(centerX - bodyRadiusX * 0.4f, centerY - bodyRadiusY * 0.7f)
                    close()
                }
                drawPath(knotPath, Color(0xFFD32F2F))
                drawPath(knotPath, outlineColor, style = Stroke(width = strokeWidth))
            } else if (level == 4) {
                val maskPath = Path().apply {
                    moveTo(centerX - bodyRadiusX * 0.3f, centerY - bodyRadiusY * 0.3f)
                    lineTo(centerX + bodyRadiusX * 0.7f, centerY - bodyRadiusY * 0.2f)
                    lineTo(centerX + bodyRadiusX * 0.6f, centerY)
                    lineTo(centerX - bodyRadiusX * 0.2f, centerY - bodyRadiusY * 0.1f)
                    close()
                }
                drawPath(maskPath, Color(0xFF0077C2))
                drawPath(maskPath, outlineColor, style = Stroke(width = strokeWidth))

                // Re-draw white eye glares on top of mask
                val leftPupilX = eyeX
                val rightPupilX = eyeX - bodyRadiusX * 0.4f
                drawCircle(Color.White, radius = eyeRadius * 0.4f, center = Offset(leftPupilX, eyeY))
                drawCircle(Color.White, radius = eyeRadius * 0.4f, center = Offset(rightPupilX, eyeY))
            }
        }
    }
}

@Composable
fun CombinedMiniHeatmap(
    allHabits: List<Habit>,
    allLogs: List<HabitLog>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val startDate = today.minusDays(62) // 9 weeks = 63 days
    val localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val days = remember(startDate) {
        (0..62).map { startDate.plusDays(it.toLong()) }
    }

    val logsMap = remember(allLogs) {
        allLogs.filter { it.isCompleted }.groupBy { it.date }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PROGRESS HISTORY",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 7.sp,
            color = Black,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(4.dp))
                .border(1.5.dp, Black, shape = RoundedCornerShape(4.dp))
                .padding(4.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(1.5.dp)) {
                for (col in 0 until 9) {
                    Column(verticalArrangement = Arrangement.spacedBy(1.5.dp)) {
                        for (row in 0 until 7) {
                            val dayIdx = col * 7 + row
                            if (dayIdx < days.size) {
                                val date = days[dayIdx]
                                val dateString = date.format(localDateFormatter)
                                val completedCount = logsMap[dateString]?.size ?: 0

                                val habitsCount = allHabits.count { habit ->
                                    val createdLocalDate = java.time.Instant.ofEpochMilli(habit.createdAt)
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate()
                                    !createdLocalDate.isAfter(date)
                                }

                                val fraction = if (habitsCount > 0) completedCount.toFloat() / habitsCount else 0f

                                val color = when {
                                    habitsCount == 0 -> Color(0xFFEAEAEA)
                                    completedCount == 0 -> Color(0xFFEAEAEA)
                                    fraction <= 0.25f -> BrutalGreen.copy(alpha = 0.15f)
                                    fraction <= 0.50f -> BrutalGreen.copy(alpha = 0.45f)
                                    fraction <= 0.75f -> BrutalGreen.copy(alpha = 0.75f)
                                    else -> BrutalGreen
                                }

                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(color)
                                        .border(0.5.dp, Black)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

