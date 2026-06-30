package com.grupo7poo2.digae.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.grupo7poo2.digae.modelos.*
import com.grupo7poo2.digae.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaMatrizSheet(
    matrizExistente: MatrizAspectos? = null,
    onDismiss: () -> Unit,
    onGuardar: (instalacionId: String, actividad: String, estado: EstadoMatriz) -> Unit
) {
    var instalacionId by remember { mutableStateOf(matrizExistente?.instalacionId ?: "") }
    var actividad by remember { mutableStateOf(matrizExistente?.actividad ?: "") }
    var estado by remember { mutableStateOf(matrizExistente?.estado ?: EstadoMatriz.BORRADOR) }
    var estadoExpanded by remember { mutableStateOf(false) }
    var instalacionExpanded by remember { mutableStateOf(false) }
    val instalaciones by InstalacionRepository.instalaciones.collectAsStateWithLifecycle()

    val esEdicion = matrizExistente != null
    val titulo = if (esEdicion) "Editar Matriz" else "Nueva Matriz"

    val instalacionNombre = instalaciones.find { it.id == instalacionId }?.nombre ?: ""
    val areaValida = instalacionId.isNotBlank()
    val actividadValida = actividad.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(40.dp).background(FigmaGreenIconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (esEdicion) Icons.Outlined.Edit else Icons.Outlined.AddBox,
                        contentDescription = null, tint = FigmaGreenPrimary, modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = FigmaTextPrimary)
                    Text("Módulo de Criticidad Ambiental", fontSize = 12.sp, color = FigmaTextSecondary)
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Área / Instalación", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                ExposedDropdownMenuBox(
                    expanded = instalacionExpanded,
                    onExpandedChange = { instalacionExpanded = it }
                ) {
                    OutlinedTextField(
                        value = instalacionNombre,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        placeholder = { Text("Selecciona una instalación", color = FigmaTextLight) },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = instalacionExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FigmaGreenPrimary,
                            unfocusedBorderColor = Color(0xFFD0D5D0)
                        ),
                        leadingIcon = { Icon(Icons.Outlined.Business, contentDescription = null, tint = FigmaTextLight, modifier = Modifier.size(20.dp)) },
                        isError = instalacionId.isEmpty() && !areaValida
                    )
                    ExposedDropdownMenu(
                        expanded = instalacionExpanded,
                        onDismissRequest = { instalacionExpanded = false },
                        containerColor = Color.White
                    ) {
                        if (instalaciones.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No hay instalaciones registradas", color = FigmaTextLight) },
                                onClick = { instalacionExpanded = false }
                            )
                        } else {
                            instalaciones.forEach { inst ->
                                DropdownMenuItem(
                                    text = { Text(inst.nombre) },
                                    onClick = { instalacionId = inst.id ?: ""; instalacionExpanded = false }
                                )
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Actividad principal", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                OutlinedTextField(
                    value = actividad,
                    onValueChange = { actividad = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                    placeholder = { Text("Describe la actividad que genera impactos", color = FigmaTextLight) },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FigmaGreenPrimary,
                        unfocusedBorderColor = Color(0xFFD0D5D0)
                    ),
                    leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = null, tint = FigmaTextLight, modifier = Modifier.size(20.dp)) }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Estado de la matriz", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                ExposedDropdownMenuBox(
                    expanded = estadoExpanded,
                    onExpandedChange = { estadoExpanded = it }
                ) {
                    OutlinedTextField(
                        value = estado.label,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FigmaGreenPrimary,
                            unfocusedBorderColor = Color(0xFFD0D5D0)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = estadoExpanded,
                        onDismissRequest = { estadoExpanded = false },
                        containerColor = Color.White
                    ) {
                        EstadoMatriz.entries.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion.label) },
                                onClick = { estado = opcion; estadoExpanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FigmaTextSecondary),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                ) { Text("Cancelar") }

                Button(
                    onClick = {
                        if (areaValida && actividadValida) {
                            onGuardar(instalacionId, actividad, estado)
                        }
                    },
                    enabled = areaValida && actividadValida,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FigmaGreenPrimary)
                ) {
                    Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Guardar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoAspectoSheet(
    aspectoExistente: AspectAmbiental? = null,
    onDismiss: () -> Unit,
    onGuardar: (descripcion: String, impacto: String, tipo: TipoAspecto, g: Int, s: Int, p: Int) -> Unit
) {
    var descripcion by remember { mutableStateOf(aspectoExistente?.descripcion ?: "") }
    var impacto by remember { mutableStateOf(aspectoExistente?.descripcionImpacto ?: "") }
    var tipo by remember { mutableStateOf(aspectoExistente?.tipoAspecto ?: TipoAspecto.AIRE) }
    var tipoExpanded by remember { mutableStateOf(false) }
    var gravedad by remember { mutableFloatStateOf((aspectoExistente?.gravedad ?: 3).toFloat()) }
    var severidad by remember { mutableFloatStateOf((aspectoExistente?.severidad ?: 3).toFloat()) }
    var probabilidad by remember { mutableFloatStateOf((aspectoExistente?.probabilidad ?: 3).toFloat()) }

    val score = gravedad.toInt() * severidad.toInt() * probabilidad.toInt()
    val nivel = NivelCriticidad.desde(score.toDouble())
    val nivelColor = when (nivel) {
        NivelCriticidad.BAJA    -> Color(0xFF2E7D32)
        NivelCriticidad.MEDIA   -> Color(0xFFF57F17)
        NivelCriticidad.ALTA    -> Color(0xFFE65100)
        NivelCriticidad.CRITICA -> Color(0xFFB71C1C)
    }
    val nivelFondo = when (nivel) {
        NivelCriticidad.BAJA    -> Color(0xFFE8F5E9)
        NivelCriticidad.MEDIA   -> Color(0xFFFFF9C4)
        NivelCriticidad.ALTA    -> Color(0xFFFBE9E7)
        NivelCriticidad.CRITICA -> Color(0xFFFFEBEE)
    }

    val esEdicion = aspectoExistente != null
    val descripcionValida = descripcion.isNotBlank()
    val impactoValido = impacto.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(40.dp).background(FigmaGreenIconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Air, contentDescription = null, tint = FigmaGreenPrimary, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text(if (esEdicion) "Editar Aspecto" else "Nuevo Aspecto Ambiental",
                        fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = FigmaTextPrimary)
                    Text("Evaluación de impacto", fontSize = 12.sp, color = FigmaTextSecondary)
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Aspecto ambiental", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                OutlinedTextField(
                    value = descripcion, onValueChange = { descripcion = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej. Emisión de vapores químicos", color = FigmaTextLight) },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FigmaGreenPrimary, unfocusedBorderColor = Color(0xFFD0D5D0)
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Impacto generado", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                OutlinedTextField(
                    value = impacto, onValueChange = { impacto = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 70.dp),
                    placeholder = { Text("Describe el impacto ambiental resultante", color = FigmaTextLight) },
                    maxLines = 3, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FigmaGreenPrimary, unfocusedBorderColor = Color(0xFFD0D5D0)
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Tipo de recurso afectado", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                ExposedDropdownMenuBox(
                    expanded = tipoExpanded,
                    onExpandedChange = { tipoExpanded = it }
                ) {
                    OutlinedTextField(
                        value = tipo.label, onValueChange = {}, readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FigmaGreenPrimary, unfocusedBorderColor = Color(0xFFD0D5D0)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = tipoExpanded,
                        onDismissRequest = { tipoExpanded = false },
                        containerColor = Color.White
                    ) {
                        TipoAspecto.entries.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion.label) },
                                onClick = { tipo = opcion; tipoExpanded = false }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            Text("Valores de evaluación (1–5)",
                fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)

            GspSlider("Gravedad", gravedad, "Magnitud del daño ambiental") { gravedad = it }
            GspSlider("Severidad", severidad, "Alcance temporal y espacial") { severidad = it }
            GspSlider("Probabilidad", probabilidad, "Frecuencia de ocurrencia") { probabilidad = it }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(nivelFondo, RoundedCornerShape(12.dp))
                    .border(1.dp, nivelColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Score G × S × P", fontSize = 12.sp, color = FigmaTextSecondary)
                    Text("${"%.0f".format(gravedad)} × ${"%.0f".format(severidad)} × ${"%.0f".format(probabilidad)}",
                        fontSize = 11.sp, color = FigmaTextLight)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(score.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = nivelColor)
                    Box(
                        modifier = Modifier
                            .background(nivelColor.copy(alpha = 0.12f), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(nivel.label, fontSize = 11.sp, color = nivelColor, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FigmaTextSecondary)
                ) { Text("Cancelar") }

                Button(
                    onClick = {
                        if (descripcionValida && impactoValido)
                            onGuardar(descripcion, impacto, tipo, gravedad.toInt(), severidad.toInt(), probabilidad.toInt())
                    },
                    enabled = descripcionValida && impactoValido,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FigmaGreenPrimary)
                ) {
                    Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Guardar")
                }
            }
        }
    }
}

@Composable
private fun GspSlider(label: String, value: Float, descripcion: String, onValueChange: (Float) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                Text(descripcion, fontSize = 11.sp, color = FigmaTextLight)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(FigmaGreenPrimary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("${value.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = FigmaGreenPrimary,
                activeTrackColor = FigmaGreenPrimary,
                inactiveTrackColor = FigmaGreenLight
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Bajo (1)", fontSize = 10.sp, color = FigmaTextLight)
            Text("Alto (5)", fontSize = 10.sp, color = FigmaTextLight)
        }
    }
}

@Composable
fun ConfirmarEliminarDialog(
    titulo: String,
    mensaje: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color(0xFFB71C1C), modifier = Modifier.size(26.dp))
            }
        },
        title = { Text(titulo, fontWeight = FontWeight.SemiBold, color = FigmaTextPrimary) },
        text = { Text(mensaje, fontSize = 14.sp, color = FigmaTextSecondary) },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Eliminar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancelar, shape = RoundedCornerShape(10.dp)) { Text("Cancelar") }
        }
    )
}
