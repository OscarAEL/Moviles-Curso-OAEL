package com.eneque.avisoscampus.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.avisoscampus.R
import com.eneque.avisoscampus.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    duracionMs: Long = 1200L,
    alTerminar: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(duracionMs)
        visible = false
        delay(250)
        alTerminar()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(250))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0A2242),
                            FondoMedianoche,
                            Color(0xFF030710)
                        )
                    )
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    visible = false
                    alTerminar()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(240.dp, 105.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp, 80.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        CyanTecsup.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.logo_tecsup),
                        contentDescription = "Tecsup Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(CyanTecsup)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AvisosCampus",
                    color = TextoPrincipal,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "SISTEMA DE NOTIFICACIONES",
                    color = CyanClaro.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(120.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = CyanTecsup,
                    trackColor = MicroBorde
                )
            }
        }
    }
}
