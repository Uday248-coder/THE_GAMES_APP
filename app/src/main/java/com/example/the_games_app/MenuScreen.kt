package com.example.the_games_app

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuScreen(
    onStartGame: () -> Unit,
    highScore: Int
) {
    var buttonPressed by remember { mutableStateOf(false) }

    val buttonScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))


            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = Color(0xFF00FFA3),
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚡",
                    fontSize = 64.sp,
                    color = Color(0xFF121212)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "CHAOS TAP",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00FFA3),
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(16.dp))


            if (highScore > 0) {
                Text(
                    text = "BEST: $highScore",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(80.dp))


            Button(
                onClick = {
                    buttonPressed = true
                    onStartGame()
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(64.dp)
                    .scale(buttonScale),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FFA3)
                )
            ) {
                Text(
                    text = "START",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.weight(1.5f))
            Text(
                text = "made by Uday Kumar Bansal",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}