package com.grupo7poo2.digae.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
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

fun nivelColor(nivel: NivelCriticidad): Color = when (nivel) {
    NivelCriticidad.BAJA    -> Color(0xFF2E7D32)
    NivelCriticidad.MEDIA   -> Color(0xFFF57F17)
    NivelCriticidad.ALTA    -> Color(0xFFE65100)
    NivelCriticidad.CRITICA -> Color(0xFFB71C1C)
}

fun nivelColorFondo(nivel: NivelCriticidad): Color = when (nivel) {
    NivelCriticidad.BAJA    -> Color(0xFFE8F5E9)
    NivelCriticidad.MEDIA   -> Color(0xFFFFF9C4)
    NivelCriticidad.ALTA    -> Color(0xFFFBE9E7)
    NivelCriticidad.CRITICA -> Color(0xFFFFEBEE)
}

fun tipoAspectoIcono(tipo: TipoAspecto): ImageVector = when (tipo) {
    TipoAspecto.AIRE          -> Icons.Outlined.Air
    TipoAspecto.AGUA          -> Icons.Outlined.Water
    TipoAspecto.SUELO         -> Icons.Outlined.Landscape
    TipoAspecto.RESIDUOS      -> Icons.Outlined.Recycling
    TipoAspecto.ENERGIA       -> Icons.Outlined.ElectricBolt
    TipoAspecto.BIODIVERSIDAD -> Icons.Outlined.Park
}

fun estadoMatrizColor(estado: EstadoMatriz): Color = when (estado) {
    EstadoMatriz.BORRADOR    -> Color(0xFF757575)
    EstadoMatriz.EN_REVISION -> Color(0xFFF57F17)
    EstadoMatriz.APROBADA    -> Color(0xFF2E7D32)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriticidadScreen(
    navController: NavController,
    viewModel: CriticidadViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var matrizParaEliminar by remember { mutableStateOf<MatrizAspectos?>(null) }

    if (uiState.mostrarNuevaMatriz) {
        val matrizExistente = uiState.matrizSeleccionadaId?.let { viewModel.obtenerMatriz(it) }
        NuevaMatrizSheet(
            matrizExistente = matrizExistente,
            onDismiss = viewModel::cerrarFormMatriz,
            onGuardar = { instalacionId, actividad, estado ->
                viewModel.guardarMatriz(instalacionId, actividad, estado)
            }
        )
    }

    matrizParaEliminar?.let { matriz ->
        ConfirmarEliminarDialog(
            titulo = "Eliminar Matriz",
            mensaje = "¿Eliminar la matriz de la instalación seleccionada? Esta acción no se puede deshacer.",
            onConfirmar = {
                viewModel.eliminarMatriz(matriz.id)
                matrizParaEliminar = null
            },
            onCancelar = { matrizParaEliminar = null }
        )
    }

    Scaffold(
        containerColor = FigmaAppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Criticidad Ambiental", fontWeight = FontWeight.SemiBold,
                            color = Color.White, fontSize = 18.sp)
                        Text("Matrices de aspectos e impactos",
                            color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FigmaGreenPrimary)
            )
        },
        floatingActionButton = {
            if (viewModel.userRole == "ADMINISTRADOR") {
                FloatingActionButton(
                    onClick = viewModel::abrirFormNuevaMatriz,
                    containerColor = FigmaGreenPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.Add, "Nueva Matriz")
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FigmaGreenPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                ResumenCriticidadCard(matrices = uiState.matrices)

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("MATRICES REGISTRADAS", color = FigmaTextSecondary,
                            fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                        Text("${uiState.matrices.size} total", color = FigmaTextLight, fontSize = 12.sp)
                    }

                    if (uiState.matrices.isEmpty()) {
                        EstadoVacioMatrices { viewModel.abrirFormNuevaMatriz() }
                    } else {
                        uiState.matrices.forEach { matriz ->
                            MatrizCard(
                                matriz = matriz,
                                isAdmin = viewModel.userRole == "ADMINISTRADOR",
                                onClick = { navController.navigate("criticidad/${matriz.id}") },
                                onEditar = { viewModel.abrirFormEditarMatriz(matriz.id) },
                                onEliminar = { matrizParaEliminar = matriz }
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
private fun ResumenCriticidadCard(matrices: List<MatrizAspectos>) {
    val totalAspectos = matrices.sumOf { it.aspectos.size }
    val aspectosCriticos = matrices.sumOf { it.aspectosCriticos().size }
    val pendientes = matrices.count { it.estado != EstadoMatriz.APROBADA }

    Box(modifier = Modifier.fillMaxWidth().background(FigmaGreenPrimary)
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, FigmaGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text("Resumen del Sistema", fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MetricaItem(matrices.size.toString(), "Matrices", FigmaGreenPrimary)
                VerticalDivider(modifier = Modifier.height(48.dp), color = Color(0xFFE0E0E0))
                MetricaItem(totalAspectos.toString(), "Aspectos", FigmaBluePrimary)
                VerticalDivider(modifier = Modifier.height(48.dp), color = Color(0xFFE0E0E0))
                MetricaItem(aspectosCriticos.toString(), "Críticos", Color(0xFFB71C1C))
                VerticalDivider(modifier = Modifier.height(48.dp), color = Color(0xFFE0E0E0))
                MetricaItem(pendientes.toString(), "Pendientes", Color(0xFFF57F17))
            }
        }
    }
    WaveSeparator()
}

@Composable
private fun MetricaItem(valor: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = FigmaTextSecondary)
    }
}

@Composable
private fun EstadoVacioMatrices(onNueva: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(72.dp).background(FigmaGreenIconBg, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Assessment, null, tint = FigmaGreenPrimary, modifier = Modifier.size(40.dp))
        }
        Text("Sin matrices registradas", fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 16.sp)
        Text("Crea tu primera matriz de aspectos e impactos ambientales",
            color = FigmaTextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
        Button(onClick = onNueva, shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FigmaGreenPrimary)) {
            Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Nueva Matriz")
        }
    }
}

@Composable
private fun MatrizCard(
    matriz: MatrizAspectos,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val nivel = matriz.nivelGlobal()
    val criticidadGlobal = matriz.calcularCriticidadGlobal()
    val maxPosible = 125.0 * matriz.aspectos.size.coerceAtLeast(1)
    val progreso = (criticidadGlobal / maxPosible).toFloat().coerceIn(0f, 1f)
    val animProgress by animateFloatAsState(progreso, animationSpec = tween(800), label = "progress")

    Column(
        modifier = Modifier.fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, nivelColor(nivel).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(matriz.instalacionId, fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 14.sp)
                Spacer(Modifier.height(2.dp))
                Text(matriz.actividad, color = FigmaTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {

                Box(modifier = Modifier
                    .background(estadoMatrizColor(matriz.estado).copy(alpha = 0.12f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(matriz.estado.label, fontSize = 10.sp,
                        color = estadoMatrizColor(matriz.estado), fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        if (isAdmin) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = onEditar,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Outlined.Edit, null, tint = FigmaGreenPrimary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Editar", fontSize = 12.sp, color = FigmaGreenPrimary)
                }
                TextButton(
                    onClick = onEliminar,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Outlined.Delete, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar", fontSize = 12.sp, color = Color(0xFFB71C1C))
                }
            }
        }

        HorizontalDivider(color = Color(0xFFF0F0F0))
        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoChip(Icons.AutoMirrored.Outlined.List, "${matriz.aspectos.size} aspectos", FigmaBluePrimary)
            InfoChip(Icons.Outlined.Warning, "${matriz.aspectosCriticos().size} críticos",
                if (matriz.aspectosCriticos().isEmpty()) FigmaTextLight else Color(0xFFE65100))
        }

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Criticidad global", fontSize = 12.sp, color = FigmaTextSecondary)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("%.0f pts".format(criticidadGlobal), fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold, color = nivelColor(nivel))
                Box(modifier = Modifier
                    .background(nivelColorFondo(nivel), RoundedCornerShape(50))
                    .border(1.dp, nivelColor(nivel).copy(alpha = 0.3f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 3.dp)) {
                    Text(nivel.label, fontSize = 11.sp, color = nivelColor(nivel), fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { animProgress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
            color = nivelColor(nivel), trackColor = nivelColorFondo(nivel), strokeCap = StrokeCap.Round
        )

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            Text("Ver aspectos", fontSize = 12.sp, color = FigmaGreenPrimary, fontWeight = FontWeight.Medium)
            Icon(Icons.Outlined.ChevronRight, null, tint = FigmaGreenPrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun InfoChip(icon: ImageVector, texto: String, color: Color) {
    Row(
        modifier = Modifier.background(color.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(12.dp))
        Text(texto, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriticidadDetalleScreen(
    matrizId: String,
    navController: NavController,
    viewModel: CriticidadViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val matriz = uiState.matrices.find { it.id == matrizId }
    var aspectoParaEliminar by remember { mutableStateOf<AspectAmbiental?>(null) }

    if (uiState.mostrarNuevoAspecto && uiState.matrizSeleccionadaId == matrizId) {
        val aspectoExistente = uiState.aspectoEditandoId?.let { viewModel.obtenerAspecto(matrizId, it) }
        NuevoAspectoSheet(
            aspectoExistente = aspectoExistente,
            onDismiss = viewModel::cerrarFormAspecto,
            onGuardar = { desc, impacto, tipo, g, s, p ->
                viewModel.guardarAspecto(matrizId, desc, impacto, tipo, g, s, p)
            }
        )
    }

    aspectoParaEliminar?.let { aspecto ->
        ConfirmarEliminarDialog(
            titulo = "Eliminar Aspecto",
            mensaje = "¿Eliminar el aspecto \"${aspecto.descripcion}\"?",
            onConfirmar = {
                viewModel.eliminarAspecto(matrizId, aspecto.id)
                aspectoParaEliminar = null
            },
            onCancelar = { aspectoParaEliminar = null }
        )
    }

    Scaffold(
        containerColor = FigmaAppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Aspectos Ambientales", fontWeight = FontWeight.SemiBold,
                            color = Color.White, fontSize = 18.sp)
                        Text("Instalación ID: ${matriz?.instalacionId ?: ""}", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Regresar", tint = Color.White)
                    }
                },
                actions = {

                    if (viewModel.userRole == "ADMINISTRADOR") {
                        IconButton(onClick = { viewModel.abrirFormEditarMatriz(matrizId) }) {
                            Icon(Icons.Outlined.Edit, "Editar matriz", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FigmaGreenPrimary)
            )
        },
        floatingActionButton = {
            if (viewModel.userRole == "ADMINISTRADOR") {
                FloatingActionButton(
                    onClick = { viewModel.abrirFormNuevoAspecto(matrizId) },
                    containerColor = FigmaGreenPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.Add, "Agregar Aspecto")
                }
            }
        }
    ) { innerPadding ->
        if (matriz == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Matriz no encontrada", color = FigmaTextSecondary)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                InfoMatrizCard(
                    matriz = matriz,
                    onEditar = { viewModel.abrirFormEditarMatriz(matrizId) }
                )
                WaveSeparator()

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("ASPECTOS E IMPACTOS", color = FigmaTextSecondary, fontSize = 12.sp,
                            fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                        Text("${matriz.aspectos.size} registros", color = FigmaTextLight, fontSize = 12.sp)
                    }

                    if (matriz.aspectos.isEmpty()) {
                        EstadoVacioAspectos { viewModel.abrirFormNuevoAspecto(matrizId) }
                    } else {
                        matriz.aspectos.forEach { aspecto ->
                            AspectCard(
                                aspecto = aspecto,
                                isAdmin = viewModel.userRole == "ADMINISTRADOR",
                                onEditar = { viewModel.abrirFormEditarAspecto(matrizId, aspecto.id) },
                                onEliminar = { aspectoParaEliminar = aspecto }
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
private fun InfoMatrizCard(matriz: MatrizAspectos, onEditar: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().background(FigmaGreenPrimary)
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("ID: ${matriz.id}", fontSize = 12.sp, color = FigmaTextLight)
                Box(modifier = Modifier
                    .background(estadoMatrizColor(matriz.estado).copy(alpha = 0.12f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(matriz.estado.label, fontSize = 11.sp,
                        color = estadoMatrizColor(matriz.estado), fontWeight = FontWeight.Medium)
                }
            }
            Text(matriz.actividad, fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 15.sp)
            val nivel = matriz.nivelGlobal()
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier
                    .background(nivelColorFondo(nivel), RoundedCornerShape(50))
                    .border(1.dp, nivelColor(nivel).copy(alpha = 0.3f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text("Nivel: ${nivel.label}", fontSize = 12.sp, color = nivelColor(nivel), fontWeight = FontWeight.SemiBold)
                }
                Text("%.0f pts globales".format(matriz.calcularCriticidadGlobal()),
                    fontSize = 12.sp, color = FigmaTextSecondary)
            }
        }
    }
}

@Composable
private fun EstadoVacioAspectos(onNuevo: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(72.dp).background(FigmaGreenIconBg, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Air, null, tint = FigmaGreenPrimary, modifier = Modifier.size(40.dp))
        }
        Text("Sin aspectos registrados", fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 16.sp)
        Text("Añade aspectos ambientales para evaluar la criticidad de esta área",
            color = FigmaTextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center)
        Button(onClick = onNuevo, shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FigmaGreenPrimary)) {
            Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Agregar Aspecto")
        }
    }
}

@Composable
private fun AspectCard(
    aspecto: AspectAmbiental,
    isAdmin: Boolean,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val nivel = aspecto.nivelCriticidad()

    Column(
        modifier = Modifier.fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, nivelColor(nivel).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(36.dp).background(FigmaGreenIconBg, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(tipoAspectoIcono(aspecto.tipoAspecto), null,
                        tint = FigmaGreenPrimary, modifier = Modifier.size(20.dp))
                }
                Text(aspecto.tipoAspecto.label, fontSize = 12.sp, color = FigmaTextSecondary, fontWeight = FontWeight.Medium)
            }
            Box(modifier = Modifier
                .background(nivelColorFondo(nivel), RoundedCornerShape(50))
                .border(1.dp, nivelColor(nivel).copy(alpha = 0.3f), RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 3.dp)) {
                Text(nivel.label, fontSize = 10.sp, color = nivelColor(nivel), fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(aspecto.descripcion, fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary, fontSize = 14.sp)
        Spacer(Modifier.height(2.dp))
        Text(aspecto.descripcionImpacto, fontSize = 12.sp, color = FigmaTextSecondary, lineHeight = 16.sp)

        if (isAdmin) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEditar, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                    Icon(Icons.Outlined.Edit, null, tint = FigmaGreenPrimary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Editar", fontSize = 12.sp, color = FigmaGreenPrimary)
                }
                TextButton(onClick = onEliminar, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                    Icon(Icons.Outlined.Delete, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar", fontSize = 12.sp, color = Color(0xFFB71C1C))
                }
            }
        }

        HorizontalDivider(color = Color(0xFFF0F0F0))
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GspCell("Gravedad", aspecto.gravedad, Modifier.weight(1f))
            GspCell("Severidad", aspecto.severidad, Modifier.weight(1f))
            GspCell("Probabilidad", aspecto.probabilidad, Modifier.weight(1f))
            Column(modifier = Modifier.weight(1f).background(nivelColorFondo(nivel), RoundedCornerShape(10.dp))
                .padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("%.0f".format(aspecto.obtenerNivelCriticidad()), fontSize = 20.sp,
                    fontWeight = FontWeight.Bold, color = nivelColor(nivel))
                Text("Score", fontSize = 10.sp, color = FigmaTextSecondary)
            }
        }

        if (aspecto.controlesAsignados.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            aspecto.controlesAsignados.forEach { control ->
                Row(modifier = Modifier.fillMaxWidth()
                    .background(FigmaGreenLight, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Outlined.Shield, null, tint = FigmaGreenPrimary, modifier = Modifier.size(14.dp))
                    Text(control.descripcion, fontSize = 11.sp, color = FigmaGreenPrimary)
                }
            }
        }
    }
}

@Composable
private fun GspCell(label: String, valor: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(Color(0xFFF4F7F4), RoundedCornerShape(10.dp)).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FigmaTextPrimary)
        Text(label, fontSize = 10.sp, color = FigmaTextSecondary, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
private fun CriticidadScreenPreview() {
    DIGAETheme { CriticidadScreen(navController = rememberNavController()) }
}

@Preview(showBackground = true)
@Composable
private fun CriticidadDetallePreview() {
    DIGAETheme { CriticidadDetalleScreen(matrizId = "MAT-001", navController = rememberNavController()) }
}
