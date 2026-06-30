package com.example.digae.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digae.R
import kotlinx.coroutines.launch

// ─── Colores de diseño del usuario ───
val FigmaGreenPrimary = Color(0xFF2E7D32)
val FigmaGreenLight = Color(0xFFE8F5E9)
val FigmaGreenBadge = Color(0xFF1B5E20)
val FigmaGreenIconBg = Color(0xFFC8E6C9)

val FigmaBluePrimary = Color(0xFF1565C0)
val FigmaBlueLight = Color(0xFFE3F2FD)
val FigmaBlueBadge = Color(0xFF0D47A1)
val FigmaBlueIconBg = Color(0xFFBBDEFB)

val FigmaTealPrimary = Color(0xFF00695C)
val FigmaTealLight = Color(0xFFE0F2F1)
val FigmaTealBadge = Color(0xFF004D40)
val FigmaTealIconBg = Color(0xFFB2DFDB)

val FigmaTextPrimary = Color(0xFF263238)
val FigmaTextSecondary = Color(0xFF546E7A)
val FigmaTextLight = Color(0xFF90A4AE)
val FigmaAppBackground = Color(0xFFF4F7F4)

// Colores antiguos preservados para el drawer/bottom nav
private val Teal = Color(0xFF019AA8)
private val CardColor = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1E293B)

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedBottomItem by remember { mutableStateOf(0) }
    
    var showWelcome by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(400) // Aparece justo cuando el bezier llega
        showWelcome = true
    }

    // ─── MENÚ LATERAL (DRAWER) ───
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CardColor,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                modifier = Modifier.width(320.dp)
            ) {
                // Cabecera del Drawer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FigmaGreenPrimary) // Adaptado al nuevo color
                        .padding(24.dp)
                        .statusBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(uiState.userInitials, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.userName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "juan.perez@uam.edu.ni",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = uiState.userRole,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Items del Drawer
                val drawerItems = listOf(
                    DrawerItem("Matrices", Icons.Rounded.TableChart),
                    DrawerItem("Bitácoras", Icons.Rounded.Book),
                    DrawerItem("Auditorías", Icons.Rounded.FactCheck),
                    DrawerItem("Registrar instalaciones", Icons.Rounded.DomainAdd),
                    DrawerItem("Búsqueda", Icons.Rounded.Search),
                    DrawerItem("Alertas", Icons.Rounded.Notifications)
                )

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null, tint = FigmaGreenPrimary) },
                        label = { Text(item.title, color = TextDark, fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                
                // Botón Cerrar Sesión
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.Logout, contentDescription = null, tint = Color(0xFFEF4444)) },
                    label = { Text("Cerrar sesión", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogoutClick()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )
            }
        }
    ) {
        // ─── PANTALLA PRINCIPAL (SCAFFOLD) ───
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = CardColor,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.Home, contentDescription = "Dashboard") },
                        label = { Text("Inicio") },
                        selected = selectedBottomItem == 0,
                        onClick = { selectedBottomItem = 0 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FigmaGreenPrimary,
                            selectedTextColor = FigmaGreenPrimary,
                            indicatorColor = FigmaGreenLight,
                            unselectedIconColor = FigmaTextLight,
                            unselectedTextColor = FigmaTextLight
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.Search, contentDescription = "Buscar") },
                        label = { Text("Buscar") },
                        selected = selectedBottomItem == 1,
                        onClick = { selectedBottomItem = 1 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FigmaGreenPrimary,
                            selectedTextColor = FigmaGreenPrimary,
                            indicatorColor = FigmaGreenLight,
                            unselectedIconColor = FigmaTextLight,
                            unselectedTextColor = FigmaTextLight
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.Notifications, contentDescription = "Alertas") },
                        label = { Text("Alertas") },
                        selected = selectedBottomItem == 2,
                        onClick = { selectedBottomItem = 2 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FigmaGreenPrimary,
                            selectedTextColor = FigmaGreenPrimary,
                            indicatorColor = FigmaGreenLight,
                            unselectedIconColor = FigmaTextLight,
                            unselectedTextColor = FigmaTextLight
                        )
                    )
                }
            },
            containerColor = FigmaAppBackground
        ) { paddingValues ->
            
            // Usamos LazyColumn para que el contenido fluya y tenga scroll
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    // --- HEADER NUEVO CON FONDO ONDULADO ---
                    Box(modifier = Modifier.fillMaxWidth()) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width, size.height * 0.9f)
                                cubicTo(
                                    size.width * 0.7f, size.height * 0.85f,
                                    size.width * 0.3f, size.height * 1.0f,
                                    0f, size.height * 0.95f
                                )
                                close()
                            }
                            drawPath(path, FigmaGreenPrimary)
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(bottom = 64.dp)
                        ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                            Image(
                                painter = painterResource(id = R.drawable.logo_digae_alt),
                                contentDescription = "Logo DIGAE Alt",
                                modifier = Modifier.height(56.dp),
                                contentScale = ContentScale.Fit
                            )
                            IconButton(onClick = { }) {
                                Icon(Icons.Outlined.AccountCircle, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(26.dp))
                            }
                        }

                        val welcomeAlpha by animateFloatAsState(
                            targetValue = if (showWelcome) 1f else 0f,
                            animationSpec = tween(600)
                        )
                        val welcomeOffsetY by animateDpAsState(
                            targetValue = if (showWelcome) 0.dp else 50.dp,
                            animationSpec = tween(600)
                        )

                        // --- TARJETA DE BIENVENIDA NUEVA ---
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                            .offset(y = welcomeOffsetY)
                            .alpha(welcomeAlpha)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(Color(0xFFA5D6A7), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(uiState.userInitials, color = Color(0xFF1B5E20), fontWeight = FontWeight.Medium, fontSize = 18.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Bienvenido(a),", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                                        Text(uiState.userName, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.horizontalScroll(rememberScrollState())
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .background(FigmaGreenBadge, RoundedCornerShape(50))
                                            .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(50))
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier
                                            .size(6.dp)
                                            .background(Color(0xFFA5D6A7), CircleShape))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(uiState.userRole, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Row(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(50))
                                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50))
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Outlined.Business, contentDescription = null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Dirección DIGAE · Acceso Global", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                } // closes Box
            } // closes item

                item {
                    // --- CONTENIDO PRINCIPAL (MÓDULOS) ---
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("MÓDULOS DEL SISTEMA", color = FigmaTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Cuadrícula de Módulos Superiores
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ModuleCard(
                                modifier = Modifier.weight(1f).height(210.dp),
                                title = "Criticidad Ambiental",
                                subtitle = "Matrices de aspectos e impactos",
                                badge = "${uiState.matricesCount} activas",
                                icon = Icons.Outlined.Assessment,
                                iconColor = FigmaGreenPrimary,
                                iconBg = FigmaGreenIconBg,
                                badgeBg = FigmaGreenLight,
                                badgeColor = FigmaGreenBadge,
                                onClick = { /* TODO */ }
                            )
                            ModuleCard(
                                modifier = Modifier.weight(1f).height(210.dp),
                                title = "Supervisión en Campo",
                                subtitle = "Auditorías de infraestructura",
                                badge = "${uiState.auditoriasCount} en curso",
                                icon = Icons.Outlined.FactCheck,
                                iconColor = FigmaBluePrimary,
                                iconBg = FigmaBlueIconBg,
                                badgeBg = FigmaBlueLight,
                                badgeColor = FigmaBlueBadge,
                                onClick = { /* TODO */ }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Módulo de Ancho Completo
                        FullWidthModuleCard(
                            title = "Trazabilidad de Residuos",
                            subtitle = "Registro y cadena de custodia",
                            badge = "${uiState.bitacorasCount} bitácoras registradas",
                            icon = Icons.Outlined.Recycling,
                            iconColor = FigmaTealPrimary,
                            iconBg = FigmaTealIconBg,
                            badgeBg = FigmaTealLight,
                            badgeColor = FigmaTealBadge,
                            onClick = { /* TODO */ }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                item {
                    // --- ACTIVIDAD RECIENTE ---
                    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 80.dp)) {
                        Text(
                            text = "ACTIVIDAD RECIENTE",
                            color = FigmaTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(1.dp, FigmaGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                .padding(vertical = 32.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.DateRange,
                                    contentDescription = null,
                                    tint = FigmaGreenPrimary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Sin actividad reciente",
                                    color = FigmaTextSecondary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Las acciones en los módulos aparecerán aquí",
                                    color = FigmaTextLight,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    badgeBg: Color,
    badgeColor: Color,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, FigmaGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(iconBg, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, color = FigmaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, lineHeight = 16.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(subtitle, color = FigmaTextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .background(badgeBg, RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Text(badge, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun FullWidthModuleCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    badgeBg: Color,
    badgeColor: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, FigmaGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(iconBg, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = FigmaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = FigmaTextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(badge, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = FigmaTextLight)
    }
}

data class DrawerItem(val title: String, val icon: ImageVector)
