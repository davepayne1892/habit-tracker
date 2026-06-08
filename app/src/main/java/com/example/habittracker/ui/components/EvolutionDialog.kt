package com.example.habittracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.habittracker.ui.theme.*

@Composable
fun EvolutionDialog(
    oldLevel: Int,
    newLevel: Int,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    val formName = when (newLevel) {
        2 -> "THE BALL"
        3 -> "THE BANDIT"
        else -> "THE GUARDIAN"
    }

    val formDescription = when (newLevel) {
        2 -> "Your egg hatched! Spike has grown spikes and feet, and can now waddle around."
        3 -> "Spike has trained hard! Wearing a rogue bandana, his spikes are sharper than ever."
        else -> "Spike has evolved into the ultimate Guardian! He wears a mask and a cape to protect your habits!"
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .brutalShadow(color = Black, offsetX = 8.dp, offsetY = 8.dp)
                .background(BrutalMagenta)
                .border(3.dp, Black)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Halftone backdrop
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .border(2.5.dp, Black, shape = RoundedCornerShape(12.dp))
                        .halftoneDots(Color.Black.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .rotate(rotation)
                    ) {
                        ComicBurst(
                            text = "POW!",
                            backgroundColor = BrutalYellow,
                            textColor = Black,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    HedgehogSprite(level = newLevel, health = 100, modifier = Modifier.size(80.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "EVOLVED!",
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SPIKE HAS EVOLVED INTO $formName!",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "(LVL. $oldLevel → LVL. $newLevel)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formDescription.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                BrutalButton(
                    text = "AWESOME!",
                    onClick = onDismiss,
                    backgroundColor = BrutalCyan,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
