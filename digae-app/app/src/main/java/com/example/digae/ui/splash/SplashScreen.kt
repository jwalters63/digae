package com.example.digae.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreenWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    val barCount = 6
    val barHeight = screenHeight / barCount
    
    // Lista de animaciones para el desplazamiento (offset) de cada barra en X
    val offsets = remember { List(barCount) { Animatable(0f) } }
    
    // Estado para desmontar las barras cuando terminen para no gastar memoria
    var animationFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Lanzamos las 6 animaciones de forma independiente pero coordinada
        offsets.forEachIndexed { index, animatable ->
            launch {
                // 100ms de delay (0.1s) multiplicado por el índice de la barra
                delay(100L * index)
                animatable.animateTo(
                    targetValue = screenWidth.value, // Se desliza hacia la derecha saliendo de la pantalla
                    animationSpec = tween(
                        durationMillis = 400, // Cada barra tarda solo 400ms en salir (más agresivo)
                        easing = LinearOutSlowInEasing
                    )
                )
                // Cuando la última barra termina de animarse, eliminamos el splash
                if (index == barCount - 1) {
                    animationFinished = true
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. El contenido real de la app (LoginScreen) dibujado debajo
        Box(modifier = Modifier.fillMaxSize().zIndex(0f)) {
            content()
        }
        
        // 2. Las 6 barras dibujadas encima bloqueando la vista hasta que se deslizan
        if (!animationFinished) {
            Column(modifier = Modifier.fillMaxSize().zIndex(1f)) {
                offsets.forEach { animatable ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(barHeight)
                            .offset(x = animatable.value.dp) // Anima el desplazamiento a la derecha
                            .background(Color(0xFF019AA8)) // Tu nuevo color primario #019AA8
                    )
                }
            }
        }
    }
}
