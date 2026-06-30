package com.grupo7poo2.digae.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.grupo7poo2.digae.modelos.Instalacion
import com.grupo7poo2.digae.modelos.InstalacionRepository
import com.grupo7poo2.digae.modelos.TipoInstalacion
import com.grupo7poo2.digae.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstalacionesScreen(
    onBack: () -> Unit
) {
    val instalaciones by InstalacionRepository.instalaciones.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    var instalacionToDelete by remember { mutableStateOf<Instalacion?>(null) }

    Scaffold(
        containerColor = FigmaAppBackground,
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Instalaciones", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FigmaGreenPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = FigmaGreenPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Outlined.Add, "Agregar Instalación")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            WaveSeparatorColor(FigmaGreenPrimary)

            if (instalaciones.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay instalaciones registradas", color = FigmaTextSecondary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    instalaciones.forEach { instalacion ->
                        InstalacionCard(instalacion) {
                            instalacionToDelete = instalacion
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        NuevaInstalacionDialog(
            onDismiss = { showAddDialog = false },
            onSave = { nombre, tipo, ubicacion ->
                InstalacionRepository.agregar(Instalacion(nombre = nombre, tipo = tipo, ubicacion = ubicacion))
                showAddDialog = false
            }
        )
    }

    instalacionToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { instalacionToDelete = null },
            title = { Text("¿Eliminar Instalación?") },
            text = { Text("¿Estás seguro de que deseas eliminar '${target.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { 
                    InstalacionRepository.eliminar(target.id ?: "")
                    instalacionToDelete = null 
                }) { Text("Eliminar", color = Red40) }
            },
            dismissButton = {
                TextButton(onClick = { instalacionToDelete = null }) { Text("Cancelar", color = FigmaTextSecondary) }
            }
        )
    }
}

@Composable
private fun InstalacionCard(instalacion: Instalacion, onDeleteRequest: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(FigmaGreenIconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val icon = when (instalacion.tipo) {
                TipoInstalacion.FACULTAD -> Icons.Outlined.School
                TipoInstalacion.LABORATORIO -> Icons.Outlined.Science
                TipoInstalacion.BIBLIOTECA -> Icons.Outlined.MenuBook
                TipoInstalacion.ADMINISTRACION -> Icons.Outlined.Business
                TipoInstalacion.EXTERIOR -> Icons.Outlined.Park
            }
            Icon(icon, null, tint = FigmaGreenPrimary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(instalacion.nombre, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = FigmaTextPrimary)
            Text(instalacion.ubicacion, fontSize = 13.sp, color = FigmaTextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(instalacion.tipo.label, fontSize = 11.sp, color = FigmaGreenPrimary, fontWeight = FontWeight.Medium)
        }
        IconButton(onClick = onDeleteRequest) {
            Icon(Icons.Outlined.Delete, "Eliminar", tint = Red40)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NuevaInstalacionDialog(
    onDismiss: () -> Unit,
    onSave: (String, TipoInstalacion, String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoInstalacion.FACULTAD) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Instalación", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    placeholder = { Text("Ej. Facultad de Ingeniería") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = tipoSeleccionado.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Instalación") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        TipoInstalacion.entries.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo.label) },
                                onClick = {
                                    tipoSeleccionado = tipo
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it },
                    label = { Text("Ubicación Física") },
                    placeholder = { Text("Ej. Bloque A, Campus Central") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(nombre, tipoSeleccionado, ubicacion) },
                enabled = nombre.isNotBlank() && ubicacion.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = FigmaGreenPrimary)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = FigmaTextSecondary) }
        }
    )
}
