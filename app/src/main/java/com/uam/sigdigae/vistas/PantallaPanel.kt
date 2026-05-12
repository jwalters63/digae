package com.uam.sigdigae.vistas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PantallaPanel(controladorNavegacion: NavController, rol: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Panel de Gestión", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Perfil: $rol", color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable { controladorNavegacion.navigate("bitacora") },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Trazabilidad de Residuos", style = MaterialTheme.typography.titleMedium)
                Text("Registro de bitácoras y firmas digitales", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (rol == "ADMINISTRADOR") {
            Card(
                modifier = Modifier.fillMaxWidth().clickable { controladorNavegacion.navigate("supervision") },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Supervisión de Campo", style = MaterialTheme.typography.titleMedium)
                    Text("Auditoría de infraestructura y energía", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable { controladorNavegacion.navigate("historial") },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Consultar Historial", style = MaterialTheme.typography.titleMedium)
                Text("Ver registros anteriores y auditorías", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}