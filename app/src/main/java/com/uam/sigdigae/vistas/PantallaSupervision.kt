package com.uam.sigdigae.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.uam.sigdigae.modelo.AuditoriaItem
import com.uam.sigdigae.modelo.RepositorioProyectos
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ItemEvaluacion(
    val id: Int,
    val categoria: String,
    val descripcion: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSupervision(controladorNavegacion: NavController) {
    val contexto = LocalContext.current
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fechaHoraActual = formatoFecha.format(Date())

    val itemsSupervision = listOf(
        ItemEvaluacion(1, "Infraestructura", "Estado óptimo de las instalaciones y contenedores."),
        ItemEvaluacion(2, "Manejo de Residuos", "Clasificación correcta en los depósitos correspondientes."),
        ItemEvaluacion(3, "Control de Fugas", "Ausencia de fugas en sistemas de agua e instalaciones hidráulicas."),
        ItemEvaluacion(4, "Eficiencia Energética", "Luces y equipos de climatización apagados en áreas sin uso.")
    )

    val resultados = remember { mutableStateMapOf<Int, String>() }
    var area by remember { mutableStateOf("") }
    var supervisor by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Auditoría de Campo", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Fecha: $fechaHoraActual", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = area,
            onValueChange = { area = it },
            label = { Text("Área o Facultad a supervisar") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = supervisor,
            onValueChange = { supervisor = it },
            label = { Text("Nombre del Supervisor") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Criterios de Evaluación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(itemsSupervision) { item ->
                TarjetaEvaluacion(
                    item = item,
                    resultadoActual = resultados[item.id] ?: "",
                    alSeleccionar = { seleccion -> resultados[item.id] = seleccion }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = { controladorNavegacion.popBackStack() }) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val nuevaAuditoria = AuditoriaItem(
                        id = System.currentTimeMillis().toString(),
                        fecha = fechaHoraActual,
                        area = area,
                        supervisor = supervisor,
                        resultados = resultados.toMap()
                    )
                    RepositorioProyectos.guardarAuditoria(contexto, nuevaAuditoria)
                    controladorNavegacion.popBackStack()
                },
                enabled = area.isNotEmpty() && supervisor.isNotEmpty() && resultados.size == itemsSupervision.size
            ) {
                Text("Finalizar Auditoría")
            }
        }
    }
}

@Composable
fun TarjetaEvaluacion(
    item: ItemEvaluacion,
    resultadoActual: String,
    alSeleccionar: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = item.categoria, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(text = item.descripcion, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonOpcion("Cumple", resultadoActual == "Cumple", Color(0xFF2E7D32)) { alSeleccionar("Cumple") }
                BotonOpcion("No Cumple", resultadoActual == "No Cumple", Color(0xFFC62828)) { alSeleccionar("No Cumple") }
                BotonOpcion("N/A", resultadoActual == "N/A", Color.Gray) { alSeleccionar("N/A") }
            }
        }
    }
}

@Composable
fun RowScope.BotonOpcion(
    texto: String,
    seleccionado: Boolean,
    colorActivo: Color,
    alHacerClick: () -> Unit
) {
    Button(
        onClick = alHacerClick,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) colorActivo else Color.Transparent,
            contentColor = if (seleccionado) Color.White else colorActivo
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (!seleccionado) androidx.compose.foundation.BorderStroke(1.dp, colorActivo) else null,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = texto, style = MaterialTheme.typography.labelSmall)
    }
}