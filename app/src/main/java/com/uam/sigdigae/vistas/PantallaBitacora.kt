package com.uam.sigdigae.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.uam.sigdigae.modelo.BitacoraItem
import com.uam.sigdigae.modelo.RepositorioProyectos
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBitacora(controladorNavegacion: NavController) {
    val contexto = LocalContext.current
    val calendario = Calendar.getInstance()

    var dia by remember { mutableStateOf((calendario.get(Calendar.DAY_OF_MONTH)).toString()) }
    var mes by remember { mutableStateOf((calendario.get(Calendar.MONTH) + 1).toString()) }
    var anio by remember { mutableStateOf(calendario.get(Calendar.YEAR).toString()) }

    var expDia by remember { mutableStateOf(false) }
    var expMes by remember { mutableStateOf(false) }
    var expAnio by remember { mutableStateOf(false) }

    val dias = (1..31).map { it.toString() }
    val meses = (1..12).map { it.toString() }
    val anios = (2024..2026).map { it.toString() }

    var tipoResiduo by remember { mutableStateOf("") }
    var expTipo by remember { mutableStateOf(false) }
    val opcionesResiduo = listOf("Común", "Peligroso", "Bioinfeccioso")

    var peso by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var firmaCapturada by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Registro de Salida de Residuos", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Fecha de registro (D/M/A)", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expDia,
                onExpandedChange = { expDia = !expDia },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = dia,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expDia) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expDia, onDismissRequest = { expDia = false }) {
                    dias.forEach { d ->
                        DropdownMenuItem(text = { Text(d) }, onClick = { dia = d; expDia = false })
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expMes,
                onExpandedChange = { expMes = !expMes },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = mes,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expMes) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expMes, onDismissRequest = { expMes = false }) {
                    meses.forEach { m ->
                        DropdownMenuItem(text = { Text(m) }, onClick = { mes = m; expMes = false })
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expAnio,
                onExpandedChange = { expAnio = !expAnio },
                modifier = Modifier.weight(1.2f)
            ) {
                OutlinedTextField(
                    value = anio,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expAnio) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expAnio, onDismissRequest = { expAnio = false }) {
                    anios.forEach { a ->
                        DropdownMenuItem(text = { Text(a) }, onClick = { anio = a; expAnio = false })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expTipo,
            onExpandedChange = { expTipo = !expTipo }
        ) {
            OutlinedTextField(
                value = tipoResiduo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Residuo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expTipo) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expTipo, onDismissRequest = { expTipo = false }) {
                opcionesResiduo.forEach { t ->
                    DropdownMenuItem(text = { Text(t) }, onClick = { tipoResiduo = t; expTipo = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso (lb) o Volumen") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = area,
            onValueChange = { area = it },
            label = { Text("Área Generadora") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = empresa,
            onValueChange = { empresa = it },
            label = { Text("Empresa Recolectora") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(1.dp, Color.Gray)
                .background(if (firmaCapturada) Color(0xFFE8F5E9) else Color(0xFFFAFAFA))
                .clickable { firmaCapturada = !firmaCapturada },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (firmaCapturada) "Firma capturada" else "Toque para firmar (Canvas)",
                color = if (firmaCapturada) Color(0xFF2E7D32) else Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = { controladorNavegacion.popBackStack() }) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val nuevoRegistro = BitacoraItem(
                        id = System.currentTimeMillis().toString(),
                        fecha = "$dia/$mes/$anio",
                        area = area,
                        empresa = empresa,
                        tipo = tipoResiduo,
                        peso = peso
                    )
                    RepositorioProyectos.guardarBitacora(contexto, nuevoRegistro)
                    controladorNavegacion.popBackStack()
                },
                enabled = firmaCapturada && peso.isNotEmpty() && tipoResiduo.isNotEmpty()
            ) {
                Text("Registrar")
            }
        }
    }
}