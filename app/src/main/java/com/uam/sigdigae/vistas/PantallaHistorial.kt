package com.uam.sigdigae.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.uam.sigdigae.modelo.RepositorioProyectos

@Composable
fun PantallaHistorial(controladorNavegacion: NavController) {
    val contexto = LocalContext.current
    var pestañaSeleccionada by remember { mutableStateOf(0) }
    val titulos = listOf("Residuos", "Auditorías")

    val historialResiduos = remember { RepositorioProyectos.obtenerBitacoras(contexto) }
    val historialAuditorias = remember { RepositorioProyectos.obtenerAuditorias(contexto) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pestañaSeleccionada) {
            titulos.forEachIndexed { indice, titulo ->
                Tab(
                    selected = pestañaSeleccionada == indice,
                    onClick = { pestañaSeleccionada = indice },
                    text = { Text(titulo) }
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp).weight(1f)) {
            if (pestañaSeleccionada == 0) {
                ListaResiduos(historialResiduos)
            } else {
                ListaAuditorias(historialAuditorias)
            }
        }

        Button(
            onClick = { controladorNavegacion.popBackStack() },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Volver al Panel")
        }
    }
}

@Composable
fun ListaResiduos(lista: List<com.uam.sigdigae.modelo.BitacoraItem>) {
    if (lista.isEmpty()) Text("No hay registros de residuos.")
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(lista) { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${item.tipo} - ${item.fecha}", fontWeight = FontWeight.Bold)
                    Text("Área: ${item.area} | ${item.peso} lb")
                }
            }
        }
    }
}

@Composable
fun ListaAuditorias(lista: List<com.uam.sigdigae.modelo.AuditoriaItem>) {
    if (lista.isEmpty()) Text("No hay auditorías registradas.")
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(lista) { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Auditoría: ${item.area}", fontWeight = FontWeight.Bold)
                    Text("Fecha: ${item.fecha} | Supervisor: ${item.supervisor}")
                }
            }
        }
    }
}