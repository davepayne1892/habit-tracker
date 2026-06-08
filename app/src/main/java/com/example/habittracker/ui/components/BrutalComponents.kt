package com.example.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.ui.theme.Black

fun Modifier.brutalShadow(
    color: Color = Black,
    offsetX: Dp = 4.dp,
    offsetY: Dp = 4.dp
) = this.drawBehind {
    drawRect(
        color = color,
        topLeft = Offset(offsetX.toPx(), offsetY.toPx()),
        size = size
    )
}

// Comic book style Ben-Day / Halftone Dots background pattern
fun Modifier.halftoneDots(
    dotColor: Color = Color.Black.copy(alpha = 0.12f),
    dotRadius: Dp = 2.dp,
    spacing: Dp = 8.dp
) = this.drawBehind {
    val r = dotRadius.toPx()
    val s = spacing.toPx()
    val width = size.width
    val height = size.height
    var y = 0f
    while (y < height) {
        // Offset alternate rows for a hex grid look
        val xOffset = if ((y / s).toInt() % 2 == 0) 0f else s / 2
        var x = xOffset
        while (x < width) {
            drawCircle(
                color = dotColor,
                radius = r,
                center = Offset(x, y)
            )
            x += s
        }
        y += s
    }
}

@Composable
fun BrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .brutalShadow()
            .background(backgroundColor)
            .border(2.5.dp, Black)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun BrutalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFFFF000)
) {
    Box(
        modifier = modifier
            .offset(x = (-2).dp, y = (-2).dp)
            .clickable(onClick = onClick)
            .brutalShadow(offsetX = 4.dp, offsetY = 4.dp)
            .background(backgroundColor)
            .border(2.5.dp, Black)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Black
        )
    }
}

// A custom drawn comic-book starburst / action splash
@Composable
fun ComicBurst(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .drawBehind {
                val path = Path()
                val centerX = size.width / 2
                val centerY = size.height / 2
                val numPoints = 14
                val outerRadius = size.width / 2
                val innerRadius = size.width / 3.2f
                
                for (i in 0 until numPoints * 2) {
                    val angle = i * Math.PI / numPoints
                    val r = if (i % 2 == 0) outerRadius else innerRadius
                    val x = centerX + r * Math.cos(angle).toFloat()
                    val y = centerY + r * Math.sin(angle).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                
                // Draw drop shadow burst
                val shadowPath = Path()
                val shadowOffset = 3.dp.toPx()
                for (i in 0 until numPoints * 2) {
                    val angle = i * Math.PI / numPoints
                    val r = if (i % 2 == 0) outerRadius else innerRadius
                    val x = centerX + shadowOffset + r * Math.cos(angle).toFloat()
                    val y = centerY + shadowOffset + r * Math.sin(angle).toFloat()
                    if (i == 0) shadowPath.moveTo(x, y) else shadowPath.lineTo(x, y)
                }
                shadowPath.close()
                drawPath(shadowPath, Black)
                
                // Draw filled burst
                drawPath(path, backgroundColor)
                // Draw thick black outline
                drawPath(path, Black, style = Stroke(width = 2.dp.toPx()))
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            color = textColor,
            modifier = Modifier.offset(y = (-1).dp)
        )
    }
}
