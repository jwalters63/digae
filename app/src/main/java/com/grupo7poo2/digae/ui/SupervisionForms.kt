package com.grupo7poo2.digae.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo7poo2.digae.modelos.*
import com.grupo7poo2.digae.ui.theme.*

// ─── Bottom Sheet: Nueva / Editar Supervisión ─────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaSupervisionSheet(
    supervisionExistente: Supervision? = null,
    onDismiss: () -> Unit,
    onGuardar: (area: String, tipo: TipoSupervision, estado: EstadoSupervision, supervisor: String) -> Unit
) {
    var area       by remember { mutableStateOf(supervisionExistente?.area ?: "") }
    var supervisor by remember { mutableStateOf(supervisionExistente?.supervisor ?: "") }
    var tipo       by remember { mutableStateOf(supervisionExistente?.tipo ?: TipoSupervision.INFRAESTRUCTURA) }
    var estado     by remember { mutableStateOf(supervisionExistente?.estado ?: EstadoSupervision.PROGRAMADA) }
    var tipoExp    by remember { mutableStateOf(false) }
    var estadoExp  by remember { mutableStateOf(false) }

    val esEdicion = supervisionExistente != null
    val valido    = area.isNotBlank() && supervisor.isNotBlank()

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
                Box(modifier = Modifier.size(40.dp).background(FigmaBlueIconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(if (esEdicion) Icons.Outlined.Edit else Icons.Outlined.AddBox,
                        null, tint = FigmaBluePrimary, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text(if (esEdicion) "Editar Supervisión" else "Nueva Supervisión",
                        fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = FigmaTextPrimary)
                    Text("Módulo de Supervisión en Campo", fontSize = 12.sp, color = FigmaTextSecondary)
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            // Área / Instalación
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Área / Instalación", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                OutlinedTextField(
                    value = area, onValueChange = { area = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej. Laboratorio de Biología Marina", color = FigmaTextLight) },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Outlined.Business, null, tint = FigmaTextLight, modifier = Modifier.size(20.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FigmaBluePrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                )
            }

            // Supervisor
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Supervisor responsable", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                OutlinedTextField(
                    value = supervisor, onValueChange = { supervisor = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nombre completo del supervisor", color = FigmaTextLight) },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Outlined.Person, null, tint = FigmaTextLight, modifier = Modifier.size(20.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FigmaBluePrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                )
            }

            // Tipo de supervisión
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Tipo de supervisión", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                ExposedDropdownMenuBox(expanded = tipoExp, onExpandedChange = { tipoExp = it }) {
                    OutlinedTextField(
                        value = tipo.label, onValueChange = {}, readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FigmaBluePrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                    )
                    ExposedDropdownMenu(expanded = tipoExp, onDismissRequest = { tipoExp = false }, containerColor = Color.White) {
                        TipoSupervision.entries.forEach { op ->
                            DropdownMenuItem(text = { Text(op.label) }, onClick = { tipo = op; tipoExp = false })
                        }
                    }
                }
            }

            // Estado
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Estado", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                ExposedDropdownMenuBox(expanded = estadoExp, onExpandedChange = { estadoExp = it }) {
                    OutlinedTextField(
                        value = estado.label, onValueChange = {}, readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FigmaBluePrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                    )
                    ExposedDropdownMenu(expanded = estadoExp, onDismissRequest = { estadoExp = false }, containerColor = Color.White) {
                        EstadoSupervision.entries.forEach { op ->
                            DropdownMenuItem(text = { Text(op.label) }, onClick = { estado = op; estadoExp = false })
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Botones
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FigmaTextSecondary)) {
                    Text("Cancelar")
                }
                Button(onClick = { if (valido) onGuardar(area, tipo, estado, supervisor) },
                    enabled = valido, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FigmaBluePrimary)) {
                    Icon(Icons.Outlined.Save, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Guardar")
                }
            }
        }
    }
}

// ─── Bottom Sheet: Nuevo / Editar Ítem ───────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoItemSupervisionSheet(
    itemExistente: ItemSupervision? = null,
    onDismiss: () -> Unit,
    onGuardar: (descripcion: String, categoria: CategoriaItem) -> Unit
) {
    var descripcion  by remember { mutableStateOf(itemExistente?.descripcion ?: "") }
    var categoria    by remember { mutableStateOf(itemExistente?.categoria ?: CategoriaItem.INSTALACIONES) }
    var categoriaExp by remember { mutableStateOf(false) }

    val esEdicion = itemExistente != null
    val valido    = descripcion.isNotBlank()

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
                Box(modifier = Modifier.size(40.dp).background(FigmaBlueIconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(if (esEdicion) Icons.Outlined.Edit else Icons.Outlined.Checklist,
                        null, tint = FigmaBluePrimary, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text(if (esEdicion) "Editar Ítem" else "Nuevo Ítem de Verificación",
                        fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = FigmaTextPrimary)
                    Text("Punto de control a evaluar", fontSize = 12.sp, color = FigmaTextSecondary)
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            // Descripción
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Descripción del ítem", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                OutlinedTextField(
                    value = descripcion, onValueChange = { descripcion = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                    placeholder = { Text("Ej. Extintores con fecha de vigencia actualizada", color = FigmaTextLight) },
                    maxLines = 3, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FigmaBluePrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                )
            }

            // Categoría
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Categoría", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = FigmaTextPrimary)
                ExposedDropdownMenuBox(expanded = categoriaExp, onExpandedChange = { categoriaExp = it }) {
                    OutlinedTextField(
                        value = categoria.label, onValueChange = {}, readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FigmaBluePrimary, unfocusedBorderColor = Color(0xFFD0D5D0))
                    )
                    ExposedDropdownMenu(expanded = categoriaExp, onDismissRequest = { categoriaExp = false }, containerColor = Color.White) {
                        CategoriaItem.entries.forEach { op ->
                            DropdownMenuItem(text = { Text(op.label) }, onClick = { categoria = op; categoriaExp = false })
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Botones
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FigmaTextSecondary)) { Text("Cancelar") }
                Button(onClick = { if (valido) onGuardar(descripcion, categoria) },
                    enabled = valido, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FigmaBluePrimary)) {
                    Icon(Icons.Outlined.Save, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Guardar")
                }
            }
        }
    }
}
