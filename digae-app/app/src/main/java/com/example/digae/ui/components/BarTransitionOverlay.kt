package com.example.digae.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun BarTransitionOverlay(
    isVisible: Boolean,
    onCoverScreen: () -> Unit,
    onTransitionFinished: () -> Unit
) {
    if (!isVisible) return

    val numBars = 6
    val greenColor = Color(0xFF2E7D32) // FigmaGreenPrimary

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()
        val barHeight = screenHeightPx / numBars
        
        // Fase 1: Barras horizontales entran de derecha a izquierda
        val barOffsets = remember { List(numBars) { Animatable(screenWidthPx) } }
        
        // Fase 2: Desplazamiento Y hacia arriba
        val yOffset = remember { Animatable(0f) }

        var currentPhase by remember { mutableStateOf(1) }

        LaunchedEffect(Unit) {
            // Fase 1: 6 barras horizontales entran
            barOffsets.mapIndexed { index, animatable ->
                async {
                    delay(index * 80L)
                    animatable.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                    )
                }
            }.awaitAll()

            // Pantalla cubierta
            onCoverScreen()
            currentPhase = 2

            // Fase 2: Capa verde sube más rápido (más agresivo)
            yOffset.animateTo(
                targetValue = -(screenHeightPx + 400f), // Sube completamente fuera de la pantalla
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )

            onTransitionFinished()
        }

        // Renderizado
        if (currentPhase == 1) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (i in 0 until numBars) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .offset { IntOffset(barOffsets[i].value.roundToInt(), 0) }
                            .background(greenColor)
                    )
                }
            }
        } else {
            // Bloque sólido con bezier inferior subiendo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(0, yOffset.value.roundToInt()) }
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(this@BoxWithConstraints.maxHeight + 200.dp)) {
                    val curveDepth = 45.dp.toPx()
                    val bottomY = size.height

                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        // Borde derecho hasta el inicio de la curva
                        lineTo(size.width, bottomY - (curveDepth * 0.66f))
                        // Curva bezier exacta a la del dashboard
                        cubicTo(
                            size.width * 0.7f, bottomY - curveDepth,
                            size.width * 0.3f, bottomY,
                            0f, bottomY - (curveDepth * 0.33f)
                        )
                        lineTo(0f, 0f)
                        close()
                    }
                    drawPath(path, greenColor)
                }
            }
        }
    }
}
