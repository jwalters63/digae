package com.uam.sigdigae.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uam.sigdigae.vistas.PantallaAcceso
import com.uam.sigdigae.vistas.PantallaPanel
import com.uam.sigdigae.vistas.PantallaBitacora
import com.uam.sigdigae.vistas.PantallaSupervision
import com.uam.sigdigae.vistas.PantallaHistorial

@Composable
fun NavegacionDigae() {
    val controladorNavegacion = rememberNavController()
    NavHost(navController = controladorNavegacion, startDestination = "acceso") {
        composable("acceso") { PantallaAcceso(controladorNavegacion) }
        composable("panel/{rol}") { entrada ->
            val rol = entrada.arguments?.getString("rol") ?: "OPERATIVO"
            PantallaPanel(controladorNavegacion, rol)
        }
        composable("bitacora") { PantallaBitacora(controladorNavegacion) }
        composable("supervision") { PantallaSupervision(controladorNavegacion) }
        composable("historial") { PantallaHistorial(controladorNavegacion) }
    }
}