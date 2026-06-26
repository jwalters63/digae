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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grupo7poo2.digae.modelos.ActividadReciente
import com.grupo7poo2.digae.modelos.ActividadRepository
import com.grupo7poo2.digae.modelos.ModuloApp

@Composable
fun WaveSeparator() {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(28.dp)
        .background(FigmaGreenPrimary)) {
        val path = Path().apply {
            moveTo(0f, 0f)
            quadraticTo(size.width * 0.25f, size.height, size.width * 0.5f, size.height * 0.5f)
            quadraticTo(size.width * 0.75f, 0f, size.width, size.height)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path = path, color = FigmaAppBackground)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    sessionManager: com.grupo7poo2.digae.network.auth.SessionManager,
    matricesCount: Int = 0,
    auditoriasCount: Int = 0,
    bitacorasCount: Int = 0,
    onModulo1Click: () -> Unit = {},
    onModulo2Click: () -> Unit = {},
    onModulo3Click: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToAlertas: () -> Unit = {},
    onNavigateToInstalaciones: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var activeNav by remember { mutableStateOf(0) }
    val actividades by ActividadRepository.actividades.collectAsStateWithLifecycle()

    val userName = sessionManager.fetchUserName() ?: "Usuario"
    val userEmail = sessionManager.fetchUserEmail() ?: ""
    val userRole = sessionManager.fetchUserRole() ?: "USUARIO"
    val userInitials = if (userName.isNotBlank()) userName.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").take(2) else "US"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                modifier = Modifier.width(280.dp)
            ) {
                DrawerContent(
                    userName = userName,
                    userEmail = userEmail,
                    userInitials = userInitials,
                    onClose = { scope.launch { drawerState.close() } },
                    onNavigateToInstalaciones = {
                        scope.launch { drawerState.close() }
                        onNavigateToInstalaciones()
                    },
                    onLogout = {
                        sessionManager.clearSession()
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = FigmaAppBackground,
            bottomBar = {
                BottomNavBar(activeNav) { index ->
                    if (index == 1) onNavigateToSearch()
                    else if (index == 2) onNavigateToAlertas()
                    else activeNav = index
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FigmaGreenPrimary)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { 
                        scope.launch { 
                            try {
                                drawerState.open() 
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } 
                    }) {
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
                                Text(userInitials, color = Color(0xFF1B5E20), fontWeight = FontWeight.Medium, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Bienvenido(a),", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                                Text(userName, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
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
                                Text(userRole, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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

                WaveSeparator()

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

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ModuleCard(
                            modifier = Modifier.weight(1f),
                            title = "Criticidad Ambiental",
                            subtitle = "Matrices de aspectos e impactos",
                            badge = "$matricesCount matrices activas",
                            icon = Icons.Outlined.Assessment,
                            iconColor = FigmaGreenPrimary,
                            iconBg = FigmaGreenIconBg,
                            badgeBg = FigmaGreenLight,
                            badgeColor = FigmaGreenBadge,
                            onClick = onModulo1Click
                        )
                        ModuleCard(
                            modifier = Modifier.weight(1f),
                            title = "Supervisión en Campo",
                            subtitle = "Auditorías de infraestructura y energía",
                            badge = "$auditoriasCount auditorías en curso",
                            icon = Icons.AutoMirrored.Outlined.FactCheck,
                            iconColor = FigmaBluePrimary,
                            iconBg = FigmaBlueIconBg,
                            badgeBg = FigmaBlueLight,
                            badgeColor = FigmaBlueBadge,
                            onClick = onModulo2Click
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    FullWidthModuleCard(
                        title = "Trazabilidad de Residuos",
                        subtitle = "Registro y cadena de custodia",
                        badge = "$bitacorasCount bitácoras registradas",
                        icon = Icons.Outlined.Recycling,
                        iconColor = FigmaTealPrimary,
                        iconBg = FigmaTealIconBg,
                        badgeBg = FigmaTealLight,
                        badgeColor = FigmaTealBadge,
                        onClick = onModulo3Click
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ACTIVIDAD RECIENTE", color = FigmaTextSecondary, fontSize = 13.sp,
                            fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                        if (actividades.isNotEmpty()) {
                            Text("${actividades.size} eventos", color = FigmaTextLight, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (actividades.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(1.dp, FigmaGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.EventNote, null, tint = FigmaTextLight, modifier = Modifier.size(32.dp))
                            Text("Sin actividad reciente", fontSize = 13.sp, color = FigmaTextSecondary)
                            Text("Las acciones en los módulos aparecerán aquí", fontSize = 11.sp, color = FigmaTextLight)
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(1.dp, FigmaGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        ) {
                            val recent = actividades.take(5)
                            recent.forEachIndexed { index, act ->
                                ActivityItem(
                                    actividad = act,
                                    showDivider = index < recent.size - 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun ModuleCard(modifier: Modifier = Modifier, title: String, subtitle: String, badge: String, icon: ImageVector, iconColor: Color, iconBg: Color, badgeBg: Color, badgeColor: Color, onClick: () -> Unit = {}) {
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
fun FullWidthModuleCard(title: String, subtitle: String, badge: String, icon: ImageVector, iconColor: Color, iconBg: Color, badgeBg: Color, badgeColor: Color, onClick: () -> Unit = {}) {
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
fun ActivityItem(actividad: ActividadReciente, showDivider: Boolean) {
    val dotColor = when (actividad.tipoAccion) {
        com.grupo7poo2.digae.modelos.TipoAccion.CREAR -> FigmaGreenPrimary
        com.grupo7poo2.digae.modelos.TipoAccion.EDITAR -> Color(0xFFF57C00) 
        com.grupo7poo2.digae.modelos.TipoAccion.ELIMINAR -> Color(0xFFD32F2F) 
    }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).background(dotColor, CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actividad.titulo,
                    color = FigmaTextPrimary, fontSize = 13.sp, maxLines = 2
                )
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(dotColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(actividad.modulo.label, fontSize = 9.sp, color = dotColor, fontWeight = FontWeight.Medium)
                    }
                    Text("·", fontSize = 10.sp, color = FigmaTextLight)
                    Text(actividad.autor, fontSize = 10.sp, color = FigmaTextSecondary)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(actividad.tiempoRelativo(), color = FigmaTextLight, fontSize = 10.sp)
        }
        if (showDivider) {
            HorizontalDivider(color = FigmaGreenPrimary.copy(alpha = 0.08f), thickness = 1.dp)
        }
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
fun DrawerContent(
    userName: String,
    userEmail: String,
    userInitials: String,
    onClose: () -> Unit,
    onNavigateToInstalaciones: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
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
                    Text(userInitials, color = Color(0xFF1B5E20), fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(userName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(userEmail, color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
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
                        .clickable {
                            if (index == 2) onNavigateToInstalaciones()
                        }
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
        val context = androidx.compose.ui.platform.LocalContext.current
        HomeDashboardScreen(sessionManager = com.grupo7poo2.digae.network.auth.SessionManager(context))
    }
}
