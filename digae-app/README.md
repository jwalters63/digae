# Sistema DIGAE

Sistema de control y trazabilidad desarrollado para la Dirección de Gestión de Acreditación y Evaluación (DIGAE). Esta aplicación Android está construida con los estándares más modernos, enfocándose fuertemente en un cliente frontend sumamente ligero, dejando la lógica de negocio al backend, pero contando con una robusta arquitectura Offline-First para operaciones de lectura.

## 🏗️ Arquitectura y Diseño

El proyecto sigue una arquitectura **MVVM (Model-View-ViewModel)** altamente descentralizada y respeta la premisa de "Cliente Tonto". La aplicación se comunica exclusivamente a través de flujos reactivos hacia una capa de datos que orquesta las llamadas remotas y la caché.

### 1. Interfaz de Usuario (UI Layer - Jetpack Compose)
La capa visual está construida enteramente en **Jetpack Compose**, permitiendo interfaces dinámicas, animadas y modulares:
- **Flujos de Navegación:** Uso de `Navigation3` para Compose, posibilitando animaciones fluidas (como el `BarTransitionOverlay`) sin pérdida de frames.
- **Componentes Reutilizables:** Se modularizó fuertemente (por ejemplo: `ModuleCard`, `FullWidthModuleCard`, `BarTransitionOverlay`) en el paquete `ui.components`.
- **Patrón de Estado Inmutable:** Las pantallas como `DashboardScreen` no contienen lógica, solo observan un flujo de estados reactivos (`uiState.collectAsState()`) proporcionado por su ViewModel.

### 2. Capa Lógica (Domain / ViewModel Layer)
Los `ViewModels` (`AuthViewModel`, `DashboardViewModel`) exponen estados de UI y lanzan corrutinas delegadas a los repositorios.
- No procesan datos pesados.
- Gestionan la inyección de repositorios mediante el patrón **Factory** (`ViewModelFactory`), asegurando la persistencia de los repositorios durante todo el ciclo de vida de la app.

### 3. Capa de Datos (Data Layer)
Esta capa es el núcleo funcional del cliente y usa el patrón **Single Source of Truth** (Única fuente de la verdad):
- **Remoto (Supabase PostgREST & Auth):** Provee de los datos crudos a través del cliente asíncrono de Ktor y la SDK de Supabase para Kotlin. Los perfiles son generados automáticamente en backend gracias a **Triggers SQL**.
- **Caché Local (Room Database):** Mantiene una base de datos SQLite reactiva (`DigaeDatabase`). Se usa de manera **Offline-First**.

### 🔄 Flujo Offline-First (Ejemplo: UserRepository)
Para obtener el perfil del usuario respetando el modelo Offline-First:
1. El `DashboardViewModel` observa un flujo (`Flow`) proveniente del DAO de Room (Caché local).
2. Si el dispositivo está sin red, el `Flow` de Room renderizará instantáneamente el último perfil guardado.
3. Simultáneamente, el `DashboardViewModel` lanza una corrutina a `UserRepository.refreshUserProfile()`.
4. El repositorio consulta Supabase por medio de PostgREST y, si obtiene éxito, **sobreescribe** la base de datos local (Room).
5. Como Room emite datos automáticamente cuando detecta un cambio en la tabla, el UI se actualiza reactivamente.

## 🛡️ Cierre de Sesión Seguro
La acción de cerrar sesión en `AuthRepository` no solo remueve los tokens del cliente de Supabase, sino que destruye toda la caché (tablas) guardadas en la base de datos local de Room invocando `database.clearAllTables()`. Esto asegura que bajo ninguna circunstancia los datos offline del usuario "A" sean legibles por el usuario "B" en un mismo dispositivo.

## 📦 Tecnologías

- **Kotlin 2.0+**
- **Jetpack Compose** (Material 3, Icons Extended)
- **Supabase** (Auth y PostgREST-Kt)
- **Room** (SQLite Object Mapping + Coroutines Flow)
- **KSP** (Kotlin Symbol Processing para el compilador de Room)
- **Ktor** (Cliente de red por debajo de Supabase)
