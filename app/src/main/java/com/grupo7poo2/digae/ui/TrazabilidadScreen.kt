package com.grupo7poo2.digae.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.grupo7poo2.digae.modelos.*
import com.grupo7poo2.digae.ui.theme.*

// ─── Helpers de color ─────────────────────────────────────────────────────────

private fun estadoBitacoraColor(e: EstadoBitacora) = when (e) {
    EstadoBitacora.BORRADOR    -> Color(0xFF757575)
    EstadoBitacora.EN_PROCESO  -> Color(0xFFF57F17)
    EstadoBitacora.COMPLETADA  -> Color(0xFF2E7D32)
    EstadoBitacora.ENVIADA     -> Color(0xFF1565C0)
}

private fun estadoBitacoraFondo(e: EstadoBitacora) = when (e) {
    EstadoBitacora.BORRADOR    -> Color(0xFFF5F5F5)
    EstadoBitacora.EN_PROCESO  -> Color(0xFFFFF3E0)
    EstadoBitacora.COMPLETADA  -> Color(0xFFE8F5E9)
    EstadoBitacora.ENVIADA     -> Color(0xFFE3F2FD)
}

private fun tipoResiduoColor(t: TipoResiduo) = when {
    t.peligroso                 -> Color(0xFFB71C1C)
    t == TipoResiduo.RECICLABLE -> Color(0xFF1565C0)
    t == TipoResiduo.ORGANICO   -> Color(0xFF2E7D32)
    else                        -> Color(0xFF757575)
}

private fun tipoResiduoFondo(t: TipoResiduo) = when {
    t.peligroso                 -> Color(0xFFFFEBEE)
    t == TipoResiduo.RECICLABLE -> Color(0xFFE3F2FD)
    t == TipoResiduo.ORGANICO   -> Color(0xFFE8F5E9)
    else                        -> Color(0xFFF5F5F5)
}

private fun tipoResiduoIcono(t: TipoResiduo): ImageVector = when (t) {
    TipoResiduo.COMUN         -> Icons.Outlined.Delete
    TipoResiduo.RECICLABLE    -> Icons.Outlined.Recycling
    TipoResiduo.ORGANICO      -> Icons.Outlined.Compost
    TipoResiduo.PELIGROSO     -> Icons.Outlined.Warning
    TipoResiduo.BIOINFECCIOSO -> Icons.Outlined.Biotech
    TipoResiduo.ESPECIAL      -> Icons.Outlined.ElectricalServices
}

// ─── Pantalla 1: Lista de Bitácoras ──────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrazabilidadScreen(
    navController: NavController,
    viewModel: TrazabilidadViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var bitacoraParaEliminar by remember { mutableStateOf<BitacoraResiduos?>(null) }

    if (uiState.mostrarNuevaBitacora) {
        val bit = uiState.bitacoraSeleccionadaId?.let { viewModel.obtenerBitacora(it) }
        NuevaBitacoraSheet(
            bitacoraExistente = bit, onDismiss = viewModel::cerrarFormBitacora,
            onGuardar = { area, empresa, resp, estado -> viewModel.guardarBitacora(area, empresa, resp, estado) }
        )
    }

    bitacoraParaEliminar?.let { bit ->
        ConfirmarEliminarDialog(
            titulo = "Eliminar Bitácora",
            mensaje = "¿Eliminar la bitácora de \"${bit.area}\"? Esta acción no se puede deshacer.",
            onConfirmar = { viewModel.eliminarBitacora(bit.id); bitacoraParaEliminar = null },
            onCancelar = { bitacoraParaEliminar = null }
        )
    }

    Scaffold(
        containerColor = FigmaAppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Trazabilidad de Residuos", fontWeight = FontWeight.SemiBold,
                            color = Color.White, fontSize = 18.sp)
                        Text("Registro y cadena de custodia",
                            color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FigmaTealPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::abrirFormNuevaBitacora,
                containerColor = FigmaTealPrimary, contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Outlined.Add, "Nueva Bitácora")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {
            ResumenTrazabilidadCard(uiState.bitacoras)
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("BITÁCORAS REGISTRADAS", color = FigmaTextSecondary,
                        fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                    Text("${uiState.bitacoras.size} total", color = FigmaTextLight, fontSize = 12.sp)
                }
                if (uiState.bitacoras.isEmpty()) {
                    EstadoVacioBitacoras { viewModel.abrirFormNuevaBitacora() }
                } else {
                    uiState.bitacoras.forEach { bit ->
                        BitacoraCard(bit,
                            onClick = { navController.navigate("trazabilidad/${bit.id}") },
                            onEditar = { viewModel.abrirFormEditarBitacora(bit.id) },
                            onEliminar = { bitacoraParaEliminar = bit })
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

// ─── Resumen ─────────────────────────────────────────────────────────────────

@Composable
private fun ResumenTrazabilidadCard(bitacoras: List<BitacoraResiduos>) {
    val completadas   = bitacoras.count { it.estado == EstadoBitacora.COMPLETADA || it.estado == EstadoBitacora.ENVIADA }
    val conPeligrosos = bitacoras.count { it.tienePeligrosos() }
    val totalPesoKg   = bitacoras.sumOf { it.pesoTotalKg() }

    Box(modifier = Modifier.fillMaxWidth().background(FigmaTealPrimary)
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp)) {
        Column(modifier = Modifier.fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)).background(Color.White)
            .border(1.dp, FigmaTealPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(16.dp)) {
            Text("Resumen de Residuos", fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MetricaTraz(bitacoras.size.toString(), "Bitácoras", FigmaTealPrimary)
                VerticalDivider(modifier = Modifier.height(48.dp), color = Color(0xFFE0E0E0))
                MetricaTraz(completadas.toString(), "Completadas", Color(0xFF2E7D32))
                VerticalDivider(modifier = Modifier.height(48.dp), color = Color(0xFFE0E0E0))
                MetricaTraz(conPeligrosos.toString(), "Peligrosos", Color(0xFFB71C1C))
                VerticalDivider(modifier = Modifier.height(48.dp), color = Color(0xFFE0E0E0))
                MetricaTraz("${"%.0f".format(totalPesoKg)} kg", "Total", FigmaTextPrimary)
            }
        }
    }
    WaveSeparatorColor(FigmaTealPrimary)
}

@Composable
private fun MetricaTraz(valor: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = FigmaTextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
private fun EstadoVacioBitacoras(onNueva: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(72.dp).background(FigmaTealIconBg, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Recycling, null, tint = FigmaTealPrimary, modifier = Modifier.size(40.dp))
        }
        Text("Sin bitácoras registradas", fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 16.sp)
        Text("Registra el primer movimiento de residuos para iniciar la cadena de custodia",
            color = FigmaTextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
        Button(onClick = onNueva, shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FigmaTealPrimary)) {
            Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp)); Text("Nueva Bitácora")
        }
    }
}

// ─── Tarjeta de bitácora ──────────────────────────────────────────────────────

@Composable
private fun BitacoraCard(
    bitacora: BitacoraResiduos,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val maxPeso  = 500.0
    val progreso = (bitacora.pesoTotalKg() / maxPeso).toFloat().coerceIn(0f, 1f)
    val animProg by animateFloatAsState(progreso, animationSpec = tween(800), label = "prog")

    Column(modifier = Modifier.fillMaxWidth()
        .shadow(4.dp, RoundedCornerShape(16.dp))
        .clip(RoundedCornerShape(16.dp)).background(Color.White)
        .border(1.dp, estadoBitacoraColor(bitacora.estado).copy(alpha = 0.18f), RoundedCornerShape(16.dp))
        .clickable(onClick = onClick).padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(42.dp).background(FigmaTealIconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Inventory2, null, tint = FigmaTealPrimary, modifier = Modifier.size(24.dp))
                }
                Column {
                    Text(bitacora.area, fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 14.sp)
                    Text(bitacora.empresa, color = FigmaTextSecondary, fontSize = 12.sp)
                }
            }
            Box(modifier = Modifier
                .background(estadoBitacoraFondo(bitacora.estado), RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text(bitacora.estado.label, fontSize = 10.sp,
                    color = estadoBitacoraColor(bitacora.estado), fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TrazInfoRow(Icons.Outlined.Person, bitacora.responsable)
            TrazInfoRow(Icons.Outlined.CalendarToday, bitacora.fechaFormateada())
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onEditar, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                Icon(Icons.Outlined.Edit, null, tint = FigmaTealPrimary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp)); Text("Editar", fontSize = 12.sp, color = FigmaTealPrimary)
            }
            TextButton(onClick = onEliminar, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                Icon(Icons.Outlined.Delete, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp)); Text("Eliminar", fontSize = 12.sp, color = Color(0xFFB71C1C))
            }
        }

        HorizontalDivider(color = Color(0xFFF0F0F0))
        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TrazChip(Icons.Outlined.Inventory2, "${bitacora.totalResiduos()} residuos", FigmaTealPrimary)
            TrazChip(Icons.Outlined.Scale, "${"%.1f".format(bitacora.pesoTotalKg())} kg", FigmaTextSecondary)
            if (bitacora.tienePeligrosos())
                TrazChip(Icons.Outlined.Warning, "Peligrosos", Color(0xFFB71C1C))
        }

        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Peso total registrado", fontSize = 12.sp, color = FigmaTextSecondary)
            Text("${"%.1f".format(bitacora.pesoTotalKg())} kg",
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = FigmaTealPrimary)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { animProg },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
            color = FigmaTealPrimary, trackColor = FigmaTealLight, strokeCap = StrokeCap.Round
        )
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Ver residuos", fontSize = 12.sp, color = FigmaTealPrimary, fontWeight = FontWeight.Medium)
            Icon(Icons.Outlined.ChevronRight, null, tint = FigmaTealPrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun TrazInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = FigmaTextLight, modifier = Modifier.size(12.dp))
        Text(text, fontSize = 11.sp, color = FigmaTextSecondary)
    }
}

@Composable
private fun TrazChip(icon: ImageVector, texto: String, color: Color) {
    Row(modifier = Modifier.background(color.copy(alpha = 0.09f), RoundedCornerShape(8.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(11.dp))
        Text(texto, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

// ─── Pantalla 2: Detalle / Residuos ──────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrazabilidadDetalleScreen(
    bitacoraId: String,
    navController: NavController,
    viewModel: TrazabilidadViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bitacora = uiState.bitacoras.find { it.id == bitacoraId }
    var residuoParaEliminar by remember { mutableStateOf<Residuo?>(null) }

    if (uiState.mostrarNuevoResiduo && uiState.bitacoraSeleccionadaId == bitacoraId) {
        val res = uiState.residuoEditandoId?.let { viewModel.obtenerResiduo(bitacoraId, it) }
        NuevoResiduoSheet(
            residuoExistente = res, onDismiss = viewModel::cerrarFormResiduo,
            onGuardar = { desc, tipo, peso, unidad, obs ->
                viewModel.guardarResiduo(bitacoraId, desc, tipo, peso, unidad, obs)
            }
        )
    }

    residuoParaEliminar?.let { res ->
        ConfirmarEliminarDialog(
            titulo = "Eliminar Residuo",
            mensaje = "¿Eliminar \"${res.descripcion}\"?",
            onConfirmar = { viewModel.eliminarResiduo(bitacoraId, res.id); residuoParaEliminar = null },
            onCancelar = { residuoParaEliminar = null }
        )
    }

    Scaffold(
        containerColor = FigmaAppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Residuos Registrados", fontWeight = FontWeight.SemiBold,
                            color = Color.White, fontSize = 18.sp)
                        Text(bitacora?.area ?: "", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.abrirFormEditarBitacora(bitacoraId) }) {
                        Icon(Icons.Outlined.Edit, "Editar bitácora", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FigmaTealPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.abrirFormNuevoResiduo(bitacoraId) },
                containerColor = FigmaTealPrimary, contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Outlined.Add, "Agregar Residuo")
            }
        }
    ) { innerPadding ->
        if (bitacora == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Bitácora no encontrada", color = FigmaTextSecondary)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {
                InfoBitacoraCard(bitacora)
                WaveSeparatorColor(FigmaTealPrimary)

                if (bitacora.residuos.isNotEmpty()) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text("DISTRIBUCIÓN POR TIPO", color = FigmaTextSecondary, fontSize = 12.sp,
                            fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                        Spacer(Modifier.height(8.dp))
                        ResumenPorTipoCard(bitacora)
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("RESIDUOS REGISTRADOS", color = FigmaTextSecondary, fontSize = 12.sp,
                            fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                        Text("${bitacora.totalResiduos()} registros", color = FigmaTextLight, fontSize = 12.sp)
                    }
                    if (bitacora.residuos.isEmpty()) {
                        EstadoVacioResiduos { viewModel.abrirFormNuevoResiduo(bitacoraId) }
                    } else {
                        bitacora.residuos.forEach { res ->
                            ResiduoCard(res,
                                onEditar = { viewModel.abrirFormEditarResiduo(bitacoraId, res.id) },
                                onEliminar = { residuoParaEliminar = res })
                        }
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoBitacoraCard(bitacora: BitacoraResiduos) {
    Box(modifier = Modifier.fillMaxWidth().background(FigmaTealPrimary)
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp)) {
        Column(modifier = Modifier.fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)).background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("ID: ${bitacora.id}", fontSize = 12.sp, color = FigmaTextLight)
                Box(modifier = Modifier
                    .background(estadoBitacoraFondo(bitacora.estado), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(bitacora.estado.label, fontSize = 11.sp,
                        color = estadoBitacoraColor(bitacora.estado), fontWeight = FontWeight.Medium)
                }
            }
            Text(bitacora.empresa, fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 15.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TrazInfoRow(Icons.Outlined.Person, bitacora.responsable)
                TrazInfoRow(Icons.Outlined.CalendarToday, bitacora.fechaFormateada())
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Peso total: ${"%.2f".format(bitacora.pesoTotalKg())} kg",
                    fontWeight = FontWeight.SemiBold, color = FigmaTealPrimary, fontSize = 14.sp)
                if (bitacora.tienePeligrosos()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.Warning, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(14.dp))
                        Text("Contiene peligrosos", fontSize = 11.sp, color = Color(0xFFB71C1C), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenPorTipoCard(bitacora: BitacoraResiduos) {
    val porTipo = bitacora.residuosPorTipo()
    Column(modifier = Modifier.fillMaxWidth()
        .shadow(2.dp, RoundedCornerShape(12.dp))
        .clip(RoundedCornerShape(12.dp)).background(Color.White)
        .padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        porTipo.entries.forEach { (tipo, count) ->
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(tipoResiduoIcono(tipo), null, tint = tipoResiduoColor(tipo), modifier = Modifier.size(16.dp))
                    Text(tipo.label, fontSize = 12.sp, color = FigmaTextPrimary)
                }
                Box(modifier = Modifier.background(tipoResiduoFondo(tipo), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 2.dp)) {
                    Text("$count registro${if (count > 1) "s" else ""}",
                        fontSize = 11.sp, color = tipoResiduoColor(tipo), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun EstadoVacioResiduos(onNuevo: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(64.dp).background(FigmaTealIconBg, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Recycling, null, tint = FigmaTealPrimary, modifier = Modifier.size(36.dp))
        }
        Text("Sin residuos en esta bitácora", fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 15.sp)
        Text("Registra los residuos entregados para completar la cadena de custodia",
            color = FigmaTextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
        Button(onClick = onNuevo, shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FigmaTealPrimary)) {
            Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp)); Text("Registrar Residuo")
        }
    }
}

@Composable
private fun ResiduoCard(residuo: Residuo, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()
        .shadow(3.dp, RoundedCornerShape(14.dp))
        .clip(RoundedCornerShape(14.dp)).background(Color.White)
        .border(1.dp, tipoResiduoColor(residuo.tipo).copy(alpha = 0.15f), RoundedCornerShape(14.dp))
        .padding(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(36.dp).background(tipoResiduoFondo(residuo.tipo), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(tipoResiduoIcono(residuo.tipo), null, tint = tipoResiduoColor(residuo.tipo), modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(residuo.tipo.label, fontSize = 11.sp, color = tipoResiduoColor(residuo.tipo), fontWeight = FontWeight.Medium)
                    Text("ID: ${residuo.id}", fontSize = 10.sp, color = FigmaTextLight)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${residuo.peso} ${residuo.unidad.label}",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FigmaTealPrimary)
                if (residuo.unidad != UnidadPeso.KG)
                    Text("${"%.2f".format(residuo.pesoEnKg())} kg", fontSize = 10.sp, color = FigmaTextLight)
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(residuo.descripcion, fontWeight = FontWeight.Medium, color = FigmaTextPrimary, fontSize = 13.sp)

        if (!residuo.observacion.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Outlined.Info, null, tint = FigmaTextLight, modifier = Modifier.size(13.dp))
                Text(residuo.observacion!!, fontSize = 11.sp, color = FigmaTextSecondary)
            }
        }

        if (residuo.tipo.peligroso) {
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth()
                .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Warning, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(13.dp))
                Text("Requiere manifiesto de residuos peligrosos", fontSize = 11.sp, color = Color(0xFFB71C1C))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onEditar, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                Icon(Icons.Outlined.Edit, null, tint = FigmaTealPrimary, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(3.dp)); Text("Editar", fontSize = 11.sp, color = FigmaTealPrimary)
            }
            TextButton(onClick = onEliminar, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                Icon(Icons.Outlined.Delete, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(3.dp)); Text("Eliminar", fontSize = 11.sp, color = Color(0xFFB71C1C))
            }
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun TrazabilidadScreenPreview() {
    DIGAETheme { TrazabilidadScreen(navController = rememberNavController()) }
}

@Preview(showBackground = true)
@Composable
private fun TrazabilidadDetallePreview() {
    DIGAETheme { TrazabilidadDetalleScreen(bitacoraId = "BIT-001", navController = rememberNavController()) }
}
