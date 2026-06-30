package com.example.digae.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.digae.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Paleta ──────────────────────────────────────────────
private val Teal         = Color(0xFF019AA8)
private val TealLight    = Color(0xFF02C4D4)
private val TealDark     = Color(0xFF017580)
private val Navy         = Color(0xFF0F172A)
private val TextDark     = Color(0xFF1E293B)
private val TextMuted    = Color(0xFF94A3B8)
private val FieldBorder  = Color(0xFFE2E8F0)
private val FieldBg      = Color(0xFFF8FAFC)
private val ErrorColor   = Color(0xFFEF4444)

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // ─── Animaciones de entrada ───
    val headerAlpha   = remember { Animatable(0f) }
    val headerOffset  = remember { Animatable(-30f) }
    val formAlpha     = remember { Animatable(0f) }
    val formOffset    = remember { Animatable(50f) }
    val footerAlpha   = remember { Animatable(0f) }

    // Animación continua para el shimmer del botón
    val shimmerTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by shimmerTransition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )

    LaunchedEffect(Unit) {
        launch {
            delay(100)
            launch { headerAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing)) }
            headerOffset.animateTo(0f, tween(700, easing = FastOutSlowInEasing))
        }
        launch {
            delay(350)
            launch { formAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing)) }
            formOffset.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
        }
        launch {
            delay(600)
            footerAlpha.animateTo(1f, tween(400))
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ─── Decoración de fondo: formas geométricas sutiles ───
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Círculo grande teal difuminado arriba-derecha
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Teal.copy(alpha = 0.06f), Color.Transparent),
                    center = Offset(w * 0.85f, h * 0.08f),
                    radius = 280f
                ),
                radius = 280f,
                center = Offset(w * 0.85f, h * 0.08f)
            )

            // Círculo pequeño abajo-izquierda
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(TealLight.copy(alpha = 0.05f), Color.Transparent),
                    center = Offset(w * 0.1f, h * 0.9f),
                    radius = 200f
                ),
                radius = 200f,
                center = Offset(w * 0.1f, h * 0.9f)
            )

            // Grid de puntos decorativos (top-right)
            val dotColor = Teal.copy(alpha = 0.07f)
            for (row in 0..5) {
                for (col in 0..4) {
                    drawCircle(
                        color = dotColor,
                        radius = 2.5f,
                        center = Offset(
                            w * 0.7f + col * 18f,
                            h * 0.04f + row * 18f
                        )
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // ─── HEADER: Logo + anillo decorativo ───
            Box(
                modifier = Modifier
                    .alpha(headerAlpha.value)
                    .offset(y = headerOffset.value.dp),
                contentAlignment = Alignment.Center
            ) {
                // Contenedor blanco con logo
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_digae),
                        contentDescription = "Logo DIGAE",
                        modifier = Modifier
                            .height(96.dp)
                            .wrapContentWidth(),
                        contentScale = ContentScale.FillHeight
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sistema de Gestión Ambiental y Energética",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.alpha(headerAlpha.value)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ─── FORMULARIO ───
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(formAlpha.value)
                    .offset(y = formOffset.value.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .drawBehind {
                        // Borde sutil con gradiente
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Teal.copy(alpha = 0.2f),
                                    FieldBorder.copy(alpha = 0.5f),
                                    TealLight.copy(alpha = 0.15f)
                                )
                            ),
                            cornerRadius = CornerRadius(24.dp.toPx()),
                            style = Stroke(width = 1.5f)
                        )
                    }
                    .padding(28.dp)
            ) {
                // Acento de color arriba del título
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Teal, TealLight))
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bienvenido",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "Ingresa tus credenciales",
                    fontSize = 14.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // ─── Email ───
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(Icons.Rounded.Email, null, tint = Teal, modifier = Modifier.size(20.dp))
                    },
                    placeholder = { Text("usuario@uam.edu.ni", color = TextMuted.copy(alpha = 0.5f)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = FieldBorder,
                        focusedBorderColor = Teal,
                        unfocusedContainerColor = FieldBg,
                        focusedContainerColor = Color.White,
                        focusedLabelColor = Teal,
                        cursorColor = Teal
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ─── Password ───
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(Icons.Rounded.Lock, null, tint = Teal, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Rounded.VisibilityOff
                                else Icons.Rounded.Visibility,
                                "Mostrar contraseña",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.login(email, password)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = FieldBorder,
                        focusedBorderColor = Teal,
                        unfocusedContainerColor = FieldBg,
                        focusedContainerColor = Color.White,
                        focusedLabelColor = Teal,
                        cursorColor = Teal
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ─── Botón con shimmer ───
                if (authState is AuthState.Loading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Teal,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .drawBehind {
                                // Shimmer que recorre el botón
                                drawRoundRect(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.15f),
                                            Color.Transparent
                                        ),
                                        start = Offset(shimmerX, 0f),
                                        end = Offset(shimmerX + 150f, size.height)
                                    ),
                                    cornerRadius = CornerRadius(16.dp.toPx())
                                )
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Teal
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Rounded.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // ─── Error ───
                AnimatedVisibility(visible = authState is AuthState.Error) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = ErrorColor.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = (authState as? AuthState.Error)?.message ?: "",
                            color = ErrorColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ─── Footer ───
            Row(
                modifier = Modifier
                    .alpha(footerAlpha.value)
                    .padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Teal)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Universidad Americana © 2026",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
    }
}
