package com.grupo7poo2.digae.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grupo7poo2.digae.modelos.NivelCriticidad
import com.grupo7poo2.digae.modelos.ResultadoSupervision
import com.grupo7poo2.digae.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    criticidadViewModel: CriticidadViewModel,
    supervisionViewModel: SupervisionViewModel,
    trazabilidadViewModel: TrazabilidadViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onMatrizClick: (String) -> Unit,
    onSupervisionClick: (String) -> Unit,
    onBitacoraClick: (String) -> Unit
) {
    val criticidadState by criticidadViewModel.uiState.collectAsStateWithLifecycle()
    val supervisionState by supervisionViewModel.uiState.collectAsStateWithLifecycle()
    val trazabilidadState by trazabilidadViewModel.uiState.collectAsStateWithLifecycle()

    val matricesAlerta = remember(criticidadState.matrices) {
        criticidadState.matrices.filter { m ->
            m.aspectosCriticos().isNotEmpty()
        }
    }

    val supervisionesAlerta = remember(supervisionState.supervisiones) {
        supervisionState.supervisiones.filter { s ->
            s.items.any { i -> i.resultado == ResultadoSupervision.NO_CUMPLE }
        }
    }

    val bitacorasAlerta = remember(trazabilidadState.bitacoras) {
        trazabilidadState.bitacoras.filter { b -> b.tienePeligrosos() }
    }

    val hayResultados = matricesAlerta.isNotEmpty() || supervisionesAlerta.isNotEmpty() || bitacorasAlerta.isNotEmpty()

    Scaffold(
        containerColor = FigmaAppBackground,
        bottomBar = {
            BottomNavBar(activeNav = 2) { index ->
                if (index == 0) onNavigateToHome()
                else if (index == 1) onNavigateToSearch()
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB71C1C)) 
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    Column {
                        Text("Alertas del Sistema", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        Text("Registros de alto riesgo que requieren atención", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                    }
                }
            }
            SpikesSeparatorColor(Color(0xFFB71C1C))

            if (!hayResultados) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Outlined.CheckCircle, null, tint = FigmaGreenPrimary, modifier = Modifier.size(48.dp))
                        Text("Todo en orden", color = FigmaTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text("No se detectaron registros críticos ni incumplimientos.", color = FigmaTextSecondary, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (matricesAlerta.isNotEmpty()) {
                        item { TituloSeccion("MATRICES CRÍTICAS", Color(0xFFC62828)) }
                        items(matricesAlerta) { matriz ->
                            AlertaItemCard(
                                titulo = matriz.instalacionId,
                                subtitulo = matriz.actividad,
                                badge = "Impacto Crítico/Alto detectado",
                                icon = Icons.Outlined.Assessment,
                                badgeColor = Color(0xFFC62828),
                                onClick = { onMatrizClick(matriz.id) }
                            )
                        }
                    }

                    if (supervisionesAlerta.isNotEmpty()) {
                        item { TituloSeccion("AUDITORÍAS CON INCUMPLIMIENTOS", Color(0xFFE65100)) } 
                        items(supervisionesAlerta) { sup ->
                            AlertaItemCard(
                                titulo = sup.instalacionId,
                                subtitulo = "Supervisor: ${sup.supervisor}",
                                badge = "Ítems evaluados como NO CUMPLE",
                                icon = Icons.Outlined.FactCheck,
                                badgeColor = Color(0xFFE65100),
                                onClick = { onSupervisionClick(sup.id) }
                            )
                        }
                    }

                    if (bitacorasAlerta.isNotEmpty()) {
                        item { TituloSeccion("RESIDUOS PELIGROSOS", Color(0xFFB71C1C)) }
                        items(bitacorasAlerta) { bit ->
                            AlertaItemCard(
                                titulo = bit.instalacionId,
                                subtitulo = bit.empresa,
                                badge = "Contiene cargas peligrosas",
                                icon = Icons.Outlined.Warning,
                                badgeColor = Color(0xFFB71C1C),
                                onClick = { onBitacoraClick(bit.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TituloSeccion(titulo: String, color: Color) {
    Text(
        text = titulo,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun AlertaItemCard(
    titulo: String,
    subtitulo: String,
    badge: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color.White)
        .clickable(onClick = onClick)
        .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(badgeColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = badgeColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Text(subtitulo, color = FigmaTextSecondary, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .background(badgeColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Outlined.PriorityHigh, null, tint = badgeColor, modifier = Modifier.size(14.dp))
                Text(badge, color = badgeColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun SpikesSeparatorColor(color: Color) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.fillMaxWidth().height(28.dp)
            .background(color)
    ) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, 0f)
            val spikeCount = 12
            val spikeWidth = size.width / spikeCount
            for (i in 0 until spikeCount) {
                lineTo(spikeWidth * i + spikeWidth / 2f, size.height * 0.8f) 
                lineTo(spikeWidth * (i + 1), 0f)
            }
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, FigmaAppBackground)
    }
}
