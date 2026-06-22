package com.grupo7poo2.digae.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo7poo2.digae.ui.theme.*
import kotlinx.coroutines.launch

// --- FORMA DE OLA (WAVE) ---
@Composable
fun WaveSeparator() {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(28.dp)
        .background(FigmaGreenPrimary)) {
        val path = Path().apply {
            moveTo(0f, 0f)
            quadraticBezierTo(size.width * 0.25f, size.height, size.width * 0.5f, size.height * 0.5f)
            quadraticBezierTo(size.width * 0.75f, 0f, size.width, size.height)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path = path, color = FigmaAppBackground)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeDashboardScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var activeNav by remember { mutableStateOf(0) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                modifier = Modifier.width(280.dp)
            ) {
                DrawerContent {
                    scope.launch { drawerState.close() }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = FigmaAppBackground,
            bottomBar = {
                BottomNavBar(activeNav) { activeNav = it }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Bar Custom (Figma Style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FigmaGreenPrimary)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.EnergySavingsLeaf, contentDescription = null, tint = Color(0xFFA5D6A7), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SIG DIGAE",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            letterSpacing = 1.2.sp
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.AccountCircle, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }

                // Header Profile Card
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(FigmaGreenPrimary)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
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
                                Text("AV", color = Color(0xFF1B5E20), fontWeight = FontWeight.Medium, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Bienvenida,", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                                Text("Andrea Vargas", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            // Role Badge
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
                                Text("ADMINISTRADORA", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                            // Area Badge
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

                WaveSeparator()

                // Content
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("MÓDULOS DEL SISTEMA", color = FigmaTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Ver todos", color = FigmaGreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = FigmaGreenPrimary, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid: 2 Columns
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ModuleCard(
                            modifier = Modifier.weight(1f),
                            title = "Criticidad Ambiental",
                            subtitle = "Matrices de aspectos e impactos",
                            badge = "12 activos en campus",
                            icon = Icons.Outlined.Assessment,
                            iconColor = FigmaGreenPrimary,
                            iconBg = FigmaGreenIconBg,
                            badgeBg = FigmaGreenLight,
                            badgeColor = FigmaGreenBadge
                        )
                        ModuleCard(
                            modifier = Modifier.weight(1f),
                            title = "Supervisión en Campo",
                            subtitle = "Auditorías de infraestructura y energía",
                            badge = "3 auditorías globales",
                            icon = Icons.AutoMirrored.Outlined.FactCheck,
                            iconColor = FigmaBluePrimary,
                            iconBg = FigmaBlueIconBg,
                            badgeBg = FigmaBlueLight,
                            badgeColor = FigmaBlueBadge
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Full Width Card
                    FullWidthModuleCard(
                        title = "Trazabilidad de Residuos",
                        subtitle = "Registro y cadena de custodia",
                        badge = "7 sedes registradas",
                        icon = Icons.Outlined.Recycling,
                        iconColor = FigmaTealPrimary,
                        iconBg = FigmaTealIconBg,
                        badgeBg = FigmaTealLight,
                        badgeColor = FigmaTealBadge
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("ACTIVIDAD RECIENTE", color = FigmaTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, FigmaGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    ) {
                        ActivityItem("Matriz de impactos actualizada — Facultad de Ingeniería", "Hace 2 min", FigmaGreenPrimary, true)
                        ActivityItem("Auditoría energética programada — Facultad de Medicina", "Hace 1 h", FigmaBluePrimary, true)
                        ActivityItem("Registro de residuos peligrosos enviado — Edificio", "Hace 3 h", FigmaTealPrimary, false)
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun ModuleCard(modifier: Modifier = Modifier, title: String, subtitle: String, badge: String, icon: ImageVector, iconColor: Color, iconBg: Color, badgeBg: Color, badgeColor: Color) {
    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, FigmaGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable { }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .size(56.dp)
            .background(iconBg, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(title, color = FigmaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, lineHeight = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(subtitle, color = FigmaTextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier
            .background(badgeBg, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 2.dp)) {
            Text(badge, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun FullWidthModuleCard(title: String, subtitle: String, badge: String, icon: ImageVector, iconColor: Color, iconBg: Color, badgeBg: Color, badgeColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, FigmaGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .size(56.dp)
            .background(iconBg, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = FigmaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = FigmaTextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier
                .background(badgeBg, RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 2.dp)) {
                Text(badge, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = FigmaTextLight)
    }
}

@Composable
fun ActivityItem(text: String, time: String, dotColor: Color, showDivider: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .size(8.dp)
            .background(dotColor, CircleShape))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = FigmaTextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f), maxLines = 2)
        Spacer(modifier = Modifier.width(8.dp))
        Text(time, color = FigmaTextLight, fontSize = 11.sp)
    }
    if (showDivider) {
        HorizontalDivider(color = FigmaGreenPrimary.copy(alpha = 0.08f), thickness = 1.dp)
    }
}

@Composable
fun BottomNavBar(activeNav: Int, onNavSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, spotColor = Color.Black.copy(alpha = 0.15f))
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val navItems = listOf(
            Triple(Icons.Outlined.Home, "Inicio", 0),
            Triple(Icons.Outlined.Search, "Buscar", 1),
            Triple(Icons.Outlined.Notifications, "Alertas", 2),
            Triple(Icons.Outlined.Settings, "Ajustes", 3)
        )
        navItems.forEach { (icon, label, index) ->
            val isActive = activeNav == index
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavSelected(index) }
                    .background(if (isActive) FigmaGreenLight else Color.Transparent)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, contentDescription = null, tint = if (isActive) FigmaGreenPrimary else FigmaTextLight, modifier = Modifier.size(24.dp))
                Text(label, fontSize = 10.sp, color = if (isActive) FigmaGreenPrimary else FigmaTextLight, fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal)
            }
        }
    }
}

@Composable
fun DrawerContent(onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .background(FigmaGreenPrimary)
            .padding(start = 20.dp, top = 48.dp, end = 20.dp, bottom = 20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.EnergySavingsLeaf, contentDescription = null, tint = Color(0xFFA5D6A7), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIG DIGAE", color = Color.White, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFA5D6A7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("AV", color = Color(0xFF1B5E20), fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Andrea Vargas", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("a.vargas@digae.edu.co", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                }
            }
        }
        
        Column(modifier = Modifier.padding(vertical = 8.dp).weight(1f)) {
            val items = listOf(
                Pair(Icons.Outlined.Home, "Tablero"),
                Pair(Icons.Outlined.Assessment, "Reportes"),
                Pair(Icons.Outlined.Business, "Instalaciones"),
                Pair(Icons.Outlined.Description, "Documentación"),
                Pair(Icons.Outlined.Notifications, "Notificaciones"),
                Pair(Icons.Outlined.Settings, "Configuración")
            )
            items.forEachIndexed { index, (icon, label) ->
                val isActive = index == 0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isActive) FigmaGreenLight else Color.Transparent)
                        .clickable { }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, tint = if (isActive) FigmaGreenPrimary else FigmaTextSecondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(label, color = if (isActive) FigmaGreenPrimary else FigmaTextPrimary, fontSize = 14.sp, fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal)
                }
            }
        }
        
        HorizontalDivider(color = FigmaGreenPrimary.copy(alpha = 0.12f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, tint = Red40)
            Spacer(modifier = Modifier.width(16.dp))
            Text("Cerrar sesión", color = Red40, fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeDashboardPreview() {
    DIGAETheme {
        HomeDashboardScreen()
    }
}
