package com.grupo7poo2.digae.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grupo7poo2.digae.modelos.BitacoraResiduos
import com.grupo7poo2.digae.modelos.MatrizAspectos
import com.grupo7poo2.digae.modelos.Supervision
import com.grupo7poo2.digae.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaScreen(
    criticidadViewModel: CriticidadViewModel,
    supervisionViewModel: SupervisionViewModel,
    trazabilidadViewModel: TrazabilidadViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToAlertas: () -> Unit,
    onMatrizClick: (String) -> Unit,
    onSupervisionClick: (String) -> Unit,
    onBitacoraClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val q = query.trim()

    val criticidadState by criticidadViewModel.uiState.collectAsStateWithLifecycle()
    val supervisionState by supervisionViewModel.uiState.collectAsStateWithLifecycle()
    val trazabilidadState by trazabilidadViewModel.uiState.collectAsStateWithLifecycle()

    val matricesFiltradas = remember(q, criticidadState.matrices) {
        if (q.isBlank()) emptyList()
        else criticidadState.matrices.filter {
            it.instalacionId.contains(q, ignoreCase = true) ||
            it.actividad.contains(q, ignoreCase = true) ||
            it.id.contains(q, ignoreCase = true)
        }
    }

    val supervisionesFiltradas = remember(q, supervisionState.supervisiones) {
        if (q.isBlank()) emptyList()
        else supervisionState.supervisiones.filter {
            it.instalacionId.contains(q, ignoreCase = true) ||
            it.supervisor.contains(q, ignoreCase = true) ||
            it.tipo.label.contains(q, ignoreCase = true) ||
            it.id.contains(q, ignoreCase = true)
        }
    }

    val bitacorasFiltradas = remember(q, trazabilidadState.bitacoras) {
        if (q.isBlank()) emptyList()
        else trazabilidadState.bitacoras.filter {
            it.instalacionId.contains(q, ignoreCase = true) ||
            it.empresa.contains(q, ignoreCase = true) ||
            it.responsable.contains(q, ignoreCase = true) ||
            it.id.contains(q, ignoreCase = true)
        }
    }

    val hayResultados = matricesFiltradas.isNotEmpty() || supervisionesFiltradas.isNotEmpty() || bitacorasFiltradas.isNotEmpty()

    Scaffold(
        containerColor = FigmaAppBackground,
        bottomBar = {
            BottomNavBar(activeNav = 1) { index ->
                if (index == 0) onNavigateToHome()
                else if (index == 2) onNavigateToAlertas()
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            
            // Header: Barra de búsqueda
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FigmaGreenPrimary)
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 20.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar instalaciones, auditorías...", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = { Icon(Icons.Outlined.Search, null, tint = Color.White) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Outlined.Close, null, tint = Color.White)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )
            }
            WaveSeparatorColor(FigmaGreenPrimary)

            if (q.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Outlined.Search, null, tint = FigmaTextLight, modifier = Modifier.size(48.dp))
                        Text("Búsqueda Global", color = FigmaTextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text("Encuentra registros en todos los módulos", color = FigmaTextLight, fontSize = 13.sp)
                    }
                }
            } else if (!hayResultados) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron resultados para \"$q\"", color = FigmaTextSecondary, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (matricesFiltradas.isNotEmpty()) {
                        item { TituloSeccion("CRITICIDAD AMBIENTAL", FigmaGreenPrimary) }
                        items(matricesFiltradas) { matriz ->
                            ResultadoItemCard(
                                titulo = matriz.instalacionId,
                                subtitulo = matriz.actividad,
                                badge = "Matriz de Impactos",
                                badgeColor = FigmaGreenPrimary,
                                onClick = { onMatrizClick(matriz.id) }
                            )
                        }
                    }

                    if (supervisionesFiltradas.isNotEmpty()) {
                        item { TituloSeccion("SUPERVISIÓN EN CAMPO", FigmaBluePrimary) }
                        items(supervisionesFiltradas) { sup ->
                            ResultadoItemCard(
                                titulo = sup.instalacionId,
                                subtitulo = sup.supervisor,
                                badge = sup.tipo.label,
                                badgeColor = FigmaBluePrimary,
                                onClick = { onSupervisionClick(sup.id) }
                            )
                        }
                    }

                    if (bitacorasFiltradas.isNotEmpty()) {
                        item { TituloSeccion("TRAZABILIDAD DE RESIDUOS", FigmaTealPrimary) }
                        items(bitacorasFiltradas) { bit ->
                            ResultadoItemCard(
                                titulo = bit.instalacionId,
                                subtitulo = bit.empresa,
                                badge = "Bitácora",
                                badgeColor = FigmaTealPrimary,
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
private fun ResultadoItemCard(titulo: String, subtitulo: String, badge: String, badgeColor: Color, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color.White)
        .clickable(onClick = onClick)
        .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Text(subtitulo, color = FigmaTextSecondary, fontSize = 13.sp)
            }
            Box(
                modifier = Modifier
                    .background(badgeColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(badge, color = badgeColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
