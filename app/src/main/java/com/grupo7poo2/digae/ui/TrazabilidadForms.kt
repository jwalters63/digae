package com.grupo7poo2.digae.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo7poo2.digae.modelos.*
import com.grupo7poo2.digae.ui.theme.*

// ─── Bottom Sheet: Nueva / Editar Bitácora ────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaBitacoraSheet(
    bitacoraExistente: BitacoraResiduos? = null,
    onDismiss: () -> Unit,
    onGuardar: (area: String, empresa: String, responsable: String, estado: EstadoBitacora) -> Unit
) {
    var area        by remember { mutableStateOf(bitacoraExistente?.area ?: "") }
    var empresa     by remember { mutableStateOf(bitacoraExistente?.empresa ?: "") }
    var responsable by remember { mutableStateOf(bitacoraExistente?.responsable ?: "") }
    var estado      by remember { mutableStateOf(bitacoraExistente?.estado ?: EstadoBitacora.BORRADOR) }
    var estadoExp   by remember { mutableStateOf(false) }

    val esEdicion = bitacoraExistente != null
    val valido    = area.isNotBlank() && empresa.isNotBlank() && responsable.isNotBlank()

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
            // Encabezado
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.size(40.dp).background(FigmaTealIconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(if (esEdicion) Icons.Outlined.Edit else Icons.Outlined.AddBox,
                        null, tint = FigmaTealPrimary, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text(if (esEdicion) "Editar Bitácora" else "Nueva Bitácora de Residuos",
                        fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = FigmaTextPrimary)
                    Text("Módulo de Trazabilidad", fontSize = 12.sp, color = FigmaTextSecondary)
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            FormField("Área / Sede generadora", area, { area = it },
                "Ej. Facultad de Ingeniería", Icons.Outlined.Business, FigmaTealPrimary)

            FormField("Empresa recolectora", empresa, { empresa = it },
                "Razón social de la empresa", Icons.Outlined.LocalShipping, FigmaTealPrimary)

            FormField("Responsable de entrega", responsable, { responsable = it },
                "Nombre de quien entrega", Icons.Outlined.Person, FigmaTealPrimary)

            // Estado
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Estado de la bitácora", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                ExposedDropdownMenuBox(expanded = estadoExp, onExpandedChange = { estadoExp = it }) {
                    OutlinedTextField(
                        value = estado.label, onValueChange = {}, readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FigmaTealPrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                    )
                    ExposedDropdownMenu(expanded = estadoExp, onDismissRequest = { estadoExp = false }, containerColor = Color.White) {
                        EstadoBitacora.entries.forEach { op ->
                            DropdownMenuItem(text = { Text(op.label) }, onClick = { estado = op; estadoExp = false })
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FigmaTextSecondary)) { Text("Cancelar") }
                Button(onClick = { if (valido) onGuardar(area, empresa, responsable, estado) },
                    enabled = valido, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FigmaTealPrimary)) {
                    Icon(Icons.Outlined.Save, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp)); Text("Guardar")
                }
            }
        }
    }
}

// ─── Bottom Sheet: Nuevo / Editar Residuo ────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoResiduoSheet(
    residuoExistente: Residuo? = null,
    onDismiss: () -> Unit,
    onGuardar: (descripcion: String, tipo: TipoResiduo, peso: Double, unidad: UnidadPeso, observacion: String?) -> Unit
) {
    var descripcion  by remember { mutableStateOf(residuoExistente?.descripcion ?: "") }
    var tipo         by remember { mutableStateOf(residuoExistente?.tipo ?: TipoResiduo.COMUN) }
    var pesoStr      by remember { mutableStateOf(residuoExistente?.peso?.toString() ?: "") }
    var unidad       by remember { mutableStateOf(residuoExistente?.unidad ?: UnidadPeso.KG) }
    var observacion  by remember { mutableStateOf(residuoExistente?.observacion ?: "") }
    var tipoExp      by remember { mutableStateOf(false) }
    var unidadExp    by remember { mutableStateOf(false) }

    val esEdicion = residuoExistente != null
    val pesoValido = pesoStr.toDoubleOrNull()?.let { it > 0 } ?: false
    val valido = descripcion.isNotBlank() && pesoValido

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
            // Encabezado
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.size(40.dp).background(FigmaTealIconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Recycling, null, tint = FigmaTealPrimary, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text(if (esEdicion) "Editar Residuo" else "Registrar Residuo",
                        fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = FigmaTextPrimary)
                    Text("Cadena de custodia", fontSize = 12.sp, color = FigmaTextSecondary)
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            // Descripción
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Descripción del residuo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                OutlinedTextField(
                    value = descripcion, onValueChange = { descripcion = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 70.dp),
                    placeholder = { Text("Ej. Solventes y reactivos vencidos", color = FigmaTextLight) },
                    maxLines = 3, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FigmaTealPrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                )
            }

            // Tipo de residuo
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Tipo de residuo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                ExposedDropdownMenuBox(expanded = tipoExp, onExpandedChange = { tipoExp = it }) {
                    OutlinedTextField(
                        value = tipo.label, onValueChange = {}, readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FigmaTealPrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                    )
                    ExposedDropdownMenu(expanded = tipoExp, onDismissRequest = { tipoExp = false }, containerColor = Color.White) {
                        TipoResiduo.entries.forEach { op ->
                            DropdownMenuItem(
                                text = {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        if (op.peligroso) Icon(Icons.Outlined.Warning, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(16.dp))
                                        Text(op.label)
                                    }
                                },
                                onClick = { tipo = op; tipoExp = false }
                            )
                        }
                    }
                }
                // Advertencia si es peligroso
                if (tipo.peligroso) {
                    Row(modifier = Modifier.fillMaxWidth()
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Warning, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(14.dp))
                        Text("Requiere manifiesto de residuos peligrosos",
                            fontSize = 11.sp, color = Color(0xFFB71C1C))
                    }
                }
            }

            // Peso + Unidad en la misma fila
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(2f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Peso / Cantidad", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                    OutlinedTextField(
                        value = pesoStr, onValueChange = { pesoStr = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0.0", color = FigmaTextLight) },
                        singleLine = true, shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = pesoStr.isNotEmpty() && !pesoValido,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FigmaTealPrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Unidad", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                    ExposedDropdownMenuBox(expanded = unidadExp, onExpandedChange = { unidadExp = it }) {
                        OutlinedTextField(
                            value = unidad.label, onValueChange = {}, readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unidadExp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FigmaTealPrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                        )
                        ExposedDropdownMenu(expanded = unidadExp, onDismissRequest = { unidadExp = false }, containerColor = Color.White) {
                            UnidadPeso.entries.forEach { op ->
                                DropdownMenuItem(text = { Text(op.label) }, onClick = { unidad = op; unidadExp = false })
                            }
                        }
                    }
                }
            }

            // Peso en kg equivalente
            val pesoKg = pesoStr.toDoubleOrNull()?.let { v ->
                when (unidad) { UnidadPeso.KG -> v; UnidadPeso.TON -> v * 1000; UnidadPeso.LB -> v * 0.453592 }
            }
            if (pesoKg != null && unidad != UnidadPeso.KG) {
                Text("≈ ${"%.2f".format(pesoKg)} kg", fontSize = 12.sp, color = FigmaTealPrimary)
            }

            // Observación
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Observaciones (opcional)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                OutlinedTextField(
                    value = observacion, onValueChange = { observacion = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
                    placeholder = { Text("Notas adicionales de manejo o procedencia", color = FigmaTextLight) },
                    maxLines = 2, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FigmaTealPrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FigmaTextSecondary)) { Text("Cancelar") }
                Button(onClick = {
                    if (valido) onGuardar(descripcion, tipo, pesoStr.toDouble(), unidad, observacion.ifBlank { null })
                }, enabled = valido, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FigmaTealPrimary)) {
                    Icon(Icons.Outlined.Save, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp)); Text("Registrar")
                }
            }
        }
    }
}




// ─── Campo de texto reutilizable ──────────────────────────────────────────────

@Composable
private fun FormField(
    label: String, value: String, onValueChange: (String) -> Unit,
    placeholder: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = FigmaTextLight) },
            singleLine = true, shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(icon, null, tint = FigmaTextLight, modifier = Modifier.size(20.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor, unfocusedBorderColor = Color(0xFFD0D5D0))
        )
    }
}
