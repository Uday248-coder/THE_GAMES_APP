package com.example.the_games_app

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = viewModel.score.toString(),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )


                    if (viewModel.combo >= 3) {
                        Text(
                            text = "×${viewModel.multiplier} COMBO",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FFA3)
                        )
                    }
                }


                Text(
                    text = "${viewModel.timeRemaining}s",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (viewModel.timeRemaining < 6) Color(0xFFFF3333) else Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))


            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (row in 0 until 5) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (col in 0 until 5) {
                            val index = row * 5 + col

                            CircleButton(
                                index = index,
                                isActive = index == viewModel.activeCircleIndex,
                                modifier = Modifier.onGloballyPositioned { coords ->
                                    val position = coords.positionInWindow()
                                    val centerX = position.x + coords.size.width / 2f
                                    val centerY = position.y + coords.size.height / 2f


                                    if (index == viewModel.activeCircleIndex) {
                                                                        }
                                },
                                onClick = {

                                    viewModel.onCircleTap(index, 0f, 0f)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))


            Row(
                modifier = Modifier.padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < viewModel.strikes) Color(0xFFFF3333)
                                else Color(0xFF2A2A2A)
                            )
                    )
                }
            }
        }


        viewModel.particles.forEach { particle ->
            val scale by animateFloatAsState(
                targetValue = 0.5f,
                animationSpec = tween(500, easing = LinearEasing),
                label = "particle_scale_${particle.id}"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { (particle.x + particle.velocityX * 20).toDp() },
                        y = with(density) { (particle.y + particle.velocityY * 20).toDp() }
                    )
                    .size(8.dp)
                    .scale(scale)
                    .alpha(particle.alpha)
                    .clip(CircleShape)
                    .background(Color(0xFF00FFA3))
            )
        }


        if (viewModel.showComboAnimation && viewModel.combo >= 3) {
            val comboScale by animateFloatAsState(
                targetValue = 1.2f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "combo_scale"
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "×${viewModel.multiplier} COMBO!",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FFA3),
                    modifier = Modifier.scale(comboScale)
                )
            }
        }


        if (viewModel.isStrikeLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x40FF3333)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3333)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡ STRIKE LOCK",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CircleButton(
    index: Int,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.15f else 1.0f,
        animationSpec = tween(200),
        label = "scale_$index"
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isActive) Color(0xFF00FFA3) else Color(0xFF1E1E1E)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    )
}