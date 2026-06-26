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

private fun estadoSupColor(e: EstadoSupervision) = when (e) {
    EstadoSupervision.PROGRAMADA   -> Color(0xFF757575)
    EstadoSupervision.EN_PROGRESO  -> Color(0xFF1565C0)
    EstadoSupervision.COMPLETADA   -> Color(0xFF2E7D32)
    EstadoSupervision.CANCELADA    -> Color(0xFFB71C1C)
}

private fun estadoSupFondo(e: EstadoSupervision) = when (e) {
    EstadoSupervision.PROGRAMADA   -> Color(0xFFF5F5F5)
    EstadoSupervision.EN_PROGRESO  -> Color(0xFFE3F2FD)
    EstadoSupervision.COMPLETADA   -> Color(0xFFE8F5E9)
    EstadoSupervision.CANCELADA    -> Color(0xFFFFEBEE)
}

private fun resultadoColor(r: ResultadoSupervision) = when (r) {
    ResultadoSupervision.CUMPLE     -> Color(0xFF2E7D32)
    ResultadoSupervision.NO_CUMPLE  -> Color(0xFFB71C1C)
    ResultadoSupervision.NO_APLICA  -> Color(0xFF757575)
}

private fun resultadoFondo(r: ResultadoSupervision) = when (r) {
    ResultadoSupervision.CUMPLE     -> Color(0xFFE8F5E9)
    ResultadoSupervision.NO_CUMPLE  -> Color(0xFFFFEBEE)
    ResultadoSupervision.NO_APLICA  -> Color(0xFFF5F5F5)
}

private fun tipoSupIcono(t: TipoSupervision): ImageVector = when (t) {
    TipoSupervision.INFRAESTRUCTURA -> Icons.Outlined.Domain
    TipoSupervision.ENERGIA          -> Icons.Outlined.ElectricBolt
    TipoSupervision.AGUA             -> Icons.Outlined.Water
    TipoSupervision.RESIDUOS         -> Icons.Outlined.Recycling
    TipoSupervision.SEGURIDAD        -> Icons.Outlined.Security
}

private fun categoriaIcono(c: CategoriaItem): ImageVector = when (c) {
    CategoriaItem.INSTALACIONES  -> Icons.Outlined.Domain
    CategoriaItem.EQUIPOS        -> Icons.Outlined.Build
    CategoriaItem.DOCUMENTACION  -> Icons.Outlined.Description
    CategoriaItem.PERSONAL       -> Icons.Outlined.Group
    CategoriaItem.PROCEDIMIENTOS -> Icons.Outlined.AccountTree
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisionScreen(
    navController: NavController,
    viewModel: SupervisionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var supParaEliminar by remember { mutableStateOf<Supervision?>(null) }

    if (uiState.mostrarNuevaSupervision) {
        val sup = uiState.supervisionSeleccionadaId?.let { viewModel.obtenerSupervision(it) }
        NuevaSupervisionSheet(
            supervisionExistente = sup,
            onDismiss = viewModel::cerrarFormSupervision,
            onGuardar = { instalacionId, tipo, estado, supervisor ->
                viewModel.guardarSupervision(instalacionId, tipo, estado, supervisor)
            }
        )
    }

    supParaEliminar?.let { sup ->
        ConfirmarEliminarDialog(
            titulo = "Eliminar Supervisión",
            mensaje = "¿Eliminar la supervisión de la instalación seleccionada? Esta acción no se puede deshacer.",
            onConfirmar = { viewModel.eliminarSupervision(sup.id); supParaEliminar = null },
            onCancelar = { supParaEliminar = null }
        )
    }

    Scaffold(
        containerColor = FigmaAppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Supervisión en Campo", fontWeight = FontWeight.SemiBold,
                            color = Color.White, fontSize = 18.sp)
                        Text("Auditorías de infraestructura y energía",
                            color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FigmaBluePrimary)
            )
        },
        floatingActionButton = {
            if (viewModel.userRole == "ADMINISTRADOR") {
                FloatingActionButton(
                    onClick = viewModel::abrirFormNuevaSupervision,
                    containerColor = FigmaBluePrimary, contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) { Icon(Icons.Outlined.Add, "Nueva Supervisión") }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ResumenSupervisionCard(uiState.supervisiones)

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("SUPERVISIONES REGISTRADAS", color = FigmaTextSecondary,
                        fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                    Text("${uiState.supervisiones.size} total", color = FigmaTextLight, fontSize = 12.sp)
                }

                if (uiState.supervisiones.isEmpty()) {
                    EstadoVacioSupervisiones { viewModel.abrirFormNuevaSupervision() }
                } else {
                    uiState.supervisiones.forEach { sup ->
                        SupervisionCard(
                            supervision = sup,
                            isAdmin = viewModel.userRole == "ADMINISTRADOR",
                            onClick = { navController.navigate("supervision/${sup.id}") },
                            onEditar = { viewModel.abrirFormEditarSupervision(sup.id) },
                            onEliminar = { supParaEliminar = sup }
                        )
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ResumenSupervisionCard(supervisiones: List<Supervision>) {
    val completadas  = supervisiones.count { it.estado == EstadoSupervision.COMPLETADA }
    val enProgreso   = supervisiones.count { it.estado == EstadoSupervision.EN_PROGRESO }
    val cumplGlobal  = if (supervisiones.isEmpty()) 0.0
                       else supervisiones.filter { it.totalItems() > 0 }
                           .let { if (it.isEmpty()) 0.0 else it.map { s -> s.porcentajeCumplimiento() }.average() }

    Box(modifier = Modifier.fillMaxWidth().background(FigmaBluePrimary)
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp)) {
        Column(modifier = Modifier.fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, FigmaBluePrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(16.dp)
        ) {
            Text("Resumen de Supervisiones", fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MetricaSupItem(supervisiones.size.toString(), "Total", FigmaBluePrimary)
                VerticalDivider(modifier = Modifier.height(48.dp), color = Color(0xFFE0E0E0))
                MetricaSupItem(completadas.toString(), "Completadas", Color(0xFF2E7D32))
                VerticalDivider(modifier = Modifier.height(48.dp), color = Color(0xFFE0E0E0))
                MetricaSupItem(enProgreso.toString(), "En progreso", Color(0xFF1565C0))
                VerticalDivider(modifier = Modifier.height(48.dp), color = Color(0xFFE0E0E0))
                MetricaSupItem("${"%.0f".format(cumplGlobal)}%", "Cumplim.", Color(0xFF2E7D32))
            }
        }
    }

    WaveSeparatorColor(FigmaBluePrimary)
}

@Composable
private fun MetricaSupItem(valor: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = FigmaTextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
fun WaveSeparatorColor(color: Color) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.fillMaxWidth().height(28.dp)
            .background(color)
    ) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, 0f)
            quadraticTo(size.width * 0.25f, size.height, size.width * 0.5f, size.height * 0.5f)
            quadraticTo(size.width * 0.75f, 0f, size.width, size.height)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, FigmaAppBackground)
    }
}

@Composable
private fun EstadoVacioSupervisiones(onNueva: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(72.dp).background(FigmaBlueIconBg, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Checklist, null, tint = FigmaBluePrimary, modifier = Modifier.size(40.dp))
        }
        Text("Sin supervisiones registradas", fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 16.sp)
        Text("Registra tu primera supervisión de campo para comenzar las auditorías",
            color = FigmaTextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
        Button(onClick = onNueva, shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FigmaBluePrimary)) {
            Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp)); Text("Nueva Supervisión")
        }
    }
}

@Composable
private fun SupervisionCard(
    supervision: Supervision,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val progreso = (supervision.progresoFormulario() / 100.0).toFloat().coerceIn(0f, 1f)
    val animProg by animateFloatAsState(progreso, animationSpec = tween(800), label = "prog")

    Column(modifier = Modifier.fillMaxWidth()
        .shadow(4.dp, RoundedCornerShape(16.dp))
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .border(1.dp, estadoSupColor(supervision.estado).copy(alpha = 0.18f), RoundedCornerShape(16.dp))
        .clickable(onClick = onClick)
        .padding(16.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(FigmaBlueIconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(tipoSupIcono(supervision.tipo), null, tint = FigmaBluePrimary, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text(supervision.instalacionId, fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 14.sp)
                    Text(supervision.tipo.label, color = FigmaTextSecondary, fontSize = 12.sp)
                }
            }
            Box(modifier = Modifier
                .background(estadoSupFondo(supervision.estado), RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text(supervision.estado.label, fontSize = 10.sp,
                    color = estadoSupColor(supervision.estado), fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Outlined.Person, null, tint = FigmaTextLight, modifier = Modifier.size(12.dp))
                Text(supervision.supervisor, fontSize = 11.sp, color = FigmaTextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Outlined.CalendarToday, null, tint = FigmaTextLight, modifier = Modifier.size(12.dp))
                Text(supervision.fechaFormateada(), fontSize = 11.sp, color = FigmaTextSecondary)
            }
        }

        if (isAdmin) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEditar, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                    Icon(Icons.Outlined.Edit, null, tint = FigmaBluePrimary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp)); Text("Editar", fontSize = 12.sp, color = FigmaBluePrimary)
                }
                TextButton(onClick = onEliminar, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                    Icon(Icons.Outlined.Delete, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp)); Text("Eliminar", fontSize = 12.sp, color = Color(0xFFB71C1C))
                }
            }
        }

        HorizontalDivider(color = Color(0xFFF0F0F0))
        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SupInfoChip(Icons.Outlined.Checklist, "${supervision.totalItems()} ítems", FigmaBluePrimary)
            SupInfoChip(Icons.Outlined.Cancel, "${supervision.itemsCriticos().size} no cumplen",
                if (supervision.itemsCriticos().isEmpty()) FigmaTextLight else Color(0xFFB71C1C))
        }

        Spacer(Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Progreso de evaluación", fontSize = 12.sp, color = FigmaTextSecondary)
            Text("${"%.0f".format(supervision.porcentajeCumplimiento())}% cumplimiento",
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = estadoSupColor(supervision.estado))
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { animProg },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
            color = estadoSupColor(supervision.estado),
            trackColor = estadoSupFondo(supervision.estado),
            strokeCap = StrokeCap.Round
        )

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Ver ítems", fontSize = 12.sp, color = FigmaBluePrimary, fontWeight = FontWeight.Medium)
            Icon(Icons.Outlined.ChevronRight, null, tint = FigmaBluePrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun SupInfoChip(icon: ImageVector, texto: String, color: Color) {
    Row(modifier = Modifier.background(color.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(12.dp))
        Text(texto, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisionDetalleScreen(
    supervisionId: String,
    navController: NavController,
    viewModel: SupervisionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val supervision = uiState.supervisiones.find { it.id == supervisionId }
    var itemParaEliminar by remember { mutableStateOf<ItemSupervision?>(null) }
    var tabSeleccionado by remember { mutableIntStateOf(0) }
    val tabs = listOf("Todos", "Cumple", "No cumple", "No aplica")

    if (uiState.mostrarNuevoItem && uiState.supervisionSeleccionadaId == supervisionId) {
        val item = uiState.itemEditandoId?.let { viewModel.obtenerItem(supervisionId, it) }
        NuevoItemSupervisionSheet(
            itemExistente = item,
            onDismiss = viewModel::cerrarFormItem,
            onGuardar = { desc, cat -> viewModel.guardarItem(supervisionId, desc, cat) }
        )
    }

    itemParaEliminar?.let { item ->
        ConfirmarEliminarDialog(
            titulo = "Eliminar Ítem",
            mensaje = "¿Eliminar \"${item.descripcion}\"?",
            onConfirmar = { viewModel.eliminarItem(supervisionId, item.id); itemParaEliminar = null },
            onCancelar = { itemParaEliminar = null }
        )
    }

    Scaffold(
        containerColor = FigmaAppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ítems de Supervisión", fontWeight = FontWeight.SemiBold,
                            color = Color.White, fontSize = 18.sp)
                        Text("Instalación ID: ${supervision?.instalacionId ?: ""}", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    if (viewModel.userRole == "ADMINISTRADOR") {
                        IconButton(onClick = { viewModel.abrirFormEditarSupervision(supervisionId) }) {
                            Icon(Icons.Outlined.Edit, "Editar supervisión", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FigmaBluePrimary)
            )
        },
        floatingActionButton = {
            if (viewModel.userRole == "ADMINISTRADOR") {
                FloatingActionButton(
                    onClick = { viewModel.abrirFormNuevoItem(supervisionId) },
                    containerColor = FigmaBluePrimary, contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) { Icon(Icons.Outlined.Add, "Agregar Ítem") }
            }
        }
    ) { innerPadding ->
        if (supervision == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Supervisión no encontrada", color = FigmaTextSecondary)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {

                InfoSupervisionCard(supervision)
                WaveSeparatorColor(FigmaBluePrimary)

                ScrollableTabRow(
                    selectedTabIndex = tabSeleccionado,
                    containerColor = Color.White,
                    contentColor = FigmaBluePrimary,
                    edgePadding = 16.dp,
                    divider = { HorizontalDivider(color = Color(0xFFF0F0F0)) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(selected = tabSeleccionado == index, onClick = { tabSeleccionado = index },
                            text = { Text(title, fontSize = 13.sp) })
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    val itemsFiltrados = supervision.items.filter { item ->
                        when (tabSeleccionado) {
                            1 -> item.resultado == ResultadoSupervision.CUMPLE
                            2 -> item.resultado == ResultadoSupervision.NO_CUMPLE
                            3 -> item.resultado == ResultadoSupervision.NO_APLICA
                            else -> true
                        }
                    }

                    if (itemsFiltrados.isEmpty()) {
                        EstadoVacioItems(
                            conFiltro = tabSeleccionado != 0,
                            onNuevo = if (tabSeleccionado == 0) ({ viewModel.abrirFormNuevoItem(supervisionId) }) else null
                        )
                    } else {
                        itemsFiltrados.forEach { item ->
                            ItemSupervisionCard(
                                item = item,
                                isAdmin = viewModel.userRole == "ADMINISTRADOR",
                                onEvaluar = { resultado, obs -> viewModel.evaluarItem(supervisionId, item.id, resultado, obs) },
                                onObservacionCambio = { obs -> viewModel.actualizarObservacion(supervisionId, item.id, obs) },
                                onEditar = { viewModel.abrirFormEditarItem(supervisionId, item.id) },
                                onEliminar = { itemParaEliminar = item }
                            )
                        }
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoSupervisionCard(supervision: Supervision) {
    Box(modifier = Modifier.fillMaxWidth().background(FigmaBluePrimary)
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp)) {
        Column(modifier = Modifier.fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("ID: ${supervision.id}", fontSize = 12.sp, color = FigmaTextLight)
                Box(modifier = Modifier
                    .background(estadoSupFondo(supervision.estado), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(supervision.estado.label, fontSize = 11.sp,
                        color = estadoSupColor(supervision.estado), fontWeight = FontWeight.Medium)
                }
            }
            Text(supervision.tipo.label, fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 15.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.Person, null, tint = FigmaTextLight, modifier = Modifier.size(13.dp))
                    Text(supervision.supervisor, fontSize = 12.sp, color = FigmaTextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.CalendarToday, null, tint = FigmaTextLight, modifier = Modifier.size(13.dp))
                    Text(supervision.fechaFormateada(), fontSize = 12.sp, color = FigmaTextSecondary)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Cumplimiento: ${"%.1f".format(supervision.porcentajeCumplimiento())}%",
                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FigmaBluePrimary)
                Text("${supervision.itemsConResultado()} / ${supervision.totalItems()} evaluados",
                    fontSize = 12.sp, color = FigmaTextSecondary)
            }
            val progEval = if (supervision.totalItems() > 0)
                (supervision.itemsConResultado().toFloat() / supervision.totalItems()) else 0f
            LinearProgressIndicator(
                progress = { progEval },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                color = FigmaBluePrimary, trackColor = FigmaBlueLight, strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun EstadoVacioItems(conFiltro: Boolean, onNuevo: (() -> Unit)?) {
    Column(modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(64.dp).background(FigmaBlueIconBg, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Checklist, null, tint = FigmaBluePrimary, modifier = Modifier.size(36.dp))
        }
        Text(if (conFiltro) "Sin ítems con este filtro" else "Sin ítems registrados",
            fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 15.sp)
        if (!conFiltro && onNuevo != null) {
            Text("Agrega los puntos de verificación para comenzar la auditoría",
                color = FigmaTextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
            Button(onClick = onNuevo, shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FigmaBluePrimary)) {
                Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp)); Text("Agregar Ítem")
            }
        }
    }
}

@Composable
private fun ItemSupervisionCard(
    item: ItemSupervision,
    isAdmin: Boolean,
    onEvaluar: (ResultadoSupervision, String?) -> Unit,
    onObservacionCambio: (String) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var observacionLocal by remember(item.id) { mutableStateOf(item.observacion ?: "") }

    Column(modifier = Modifier.fillMaxWidth()
        .shadow(3.dp, RoundedCornerShape(16.dp))
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .border(1.dp, resultadoColor(item.resultado).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
        .padding(14.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(32.dp).background(FigmaBlueIconBg, RoundedCornerShape(9.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(categoriaIcono(item.categoria), null, tint = FigmaBluePrimary, modifier = Modifier.size(17.dp))
                }
                Text(item.categoria.label, fontSize = 11.sp, color = FigmaTextSecondary, fontWeight = FontWeight.Medium)
            }
            if (item.fueEvaluado) {
                Box(modifier = Modifier
                    .background(resultadoFondo(item.resultado), RoundedCornerShape(50))
                    .border(1.dp, resultadoColor(item.resultado).copy(alpha = 0.3f), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text(item.resultado.label, fontSize = 10.sp,
                        color = resultadoColor(item.resultado), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(item.descripcion, fontWeight = FontWeight.Medium, color = FigmaTextPrimary, fontSize = 13.sp)

        if (isAdmin) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEditar, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                    Icon(Icons.Outlined.Edit, null, tint = FigmaBluePrimary, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp)); Text("Editar", fontSize = 11.sp, color = FigmaBluePrimary)
                }
                TextButton(onClick = onEliminar, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                    Icon(Icons.Outlined.Delete, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp)); Text("Eliminar", fontSize = 11.sp, color = Color(0xFFB71C1C))
                }
            }
        }

        HorizontalDivider(color = Color(0xFFF0F0F0))
        Spacer(Modifier.height(10.dp))

        Text("Evaluación en campo", fontSize = 12.sp, color = FigmaTextSecondary, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ResultadoSupervision.entries.forEachIndexed { index, resultado ->
                val isSelected = item.resultado == resultado && item.fueEvaluado
                SegmentedButton(
                    selected = isSelected,
                    onClick = { onEvaluar(resultado, if (resultado == ResultadoSupervision.NO_CUMPLE) observacionLocal.ifBlank { null } else null) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = ResultadoSupervision.entries.size),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = resultadoFondo(resultado),
                        activeContentColor = resultadoColor(resultado),
                        activeBorderColor = resultadoColor(resultado)
                    )
                ) {
                    Text(resultado.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }

        if (item.resultado == ResultadoSupervision.NO_CUMPLE && item.fueEvaluado) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = observacionLocal,
                onValueChange = { observacionLocal = it; onObservacionCambio(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Describe el hallazgo o no conformidad…", color = FigmaTextLight, fontSize = 12.sp) },
                label = { Text("Observación / Hallazgo", fontSize = 12.sp) },
                maxLines = 3,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB71C1C),
                    unfocusedBorderColor = Color(0xFFB71C1C).copy(alpha = 0.4f),
                    focusedLabelColor = Color(0xFFB71C1C)
                )
            )
        }

        if (item.resultado != ResultadoSupervision.NO_CUMPLE && !item.observacion.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Outlined.Info, null, tint = FigmaTextLight, modifier = Modifier.size(13.dp))
                Text(item.observacion!!, fontSize = 11.sp, color = FigmaTextSecondary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SupervisionScreenPreview() {
    DIGAETheme { SupervisionScreen(navController = rememberNavController()) }
}

@Preview(showBackground = true)
@Composable
private fun SupervisionDetallePreview() {
    DIGAETheme { SupervisionDetalleScreen(supervisionId = "SUP-001", navController = rememberNavController()) }
}
