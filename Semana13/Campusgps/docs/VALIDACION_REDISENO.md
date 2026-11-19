# Validación del rediseño B

Rama: `mejora-ia-campusgps`. Revisión visual aprobada por el usuario; cierre del rediseño autorizado.

## Alcance

Propuesta visual externa de Claude compartida por el usuario, adaptada a Compose por Codex. Se conserva MVVM, la inyección manual existente, JUnit 4 y las pruebas instrumentadas del proyecto. No se migran repositorios, navegación ni servicios de ubicación.

`ContenidoCampus` presenta estados sin acceder a la plataforma. `PantallaMapa` conserva los permisos y la conexión con el ViewModel. DM Sans y DM Mono se incluyen localmente con sus licencias OFL.

## Comprobaciones ejecutadas

- `:app:assembleDebug`: correcto.
- `:app:testDebugUnitTest`: correcto.
- `:app:connectedDebugAndroidTest`: correcto en Small Phone API 31 Play; se conserva la cobertura de distancias, errores, filtros, radio y persistencia.
- `:app:updateDebugScreenshotTestDefaultTestSuite`: primera generación de 17 referencias. No había referencias anteriores.
- `:app:testDebugScreenshotTestDefaultTestSuite`: comparación de las 17 referencias correcta, BUILD SUCCESSFUL.
- Sincronización de Gradle: correcta.
- `git diff --check`: correcto.

Las tareas de screenshots de AGP 9.5.1 usan el orden **DebugScreenshotTestDefault**, distinto del ejemplo de la guía. Se incluyen recursos Android en pruebas unitarias y se habilita el host test de release para que Layoutlib pueda enumerar sus tareas. Estos ajustes solo afectan a la infraestructura de pruebas.

## Cobertura visual

Referencias en `app/src/screenshotTestDefaultDebug/reference`: anchos 400/610/900 dp combinados con altos 400/500/1000 dp, más inicio, buscando, Comida con 200 m, texto al 130 % y 150 %, tema oscuro y error de permisos en vertical/horizontal.

El mapa de estas pruebas es un área estática de prueba. Permite revisar la distribución sin depender de red ni Vulkan; no certifica el renderer de MapLibre ni sus gestos. Las referencias iniciales requieren revisión visual del usuario antes de aceptarlas como diseño definitivo.

Comparación sin modificar referencias:

```powershell
.\gradlew.bat :app:testDebugScreenshotTestDefaultTestSuite --console=plain
```

## Verificación del mapa real

Antes de la interrupción se comprobó en Small Phone API 31 Play:

- Carga del mapa de OpenFreeMap en vertical.
- Panel horizontal con mapa a la izquierda y zoom y atribución visibles.
- Búsqueda real: -12.046400, -77.042798; Biblioteca Central a 88 m.
- Marcador rojo y círculo de radio visibles.
- Ventana del lugar contenida dentro del mapa.
- Texto al 130 %: pie en dos filas y tarjetas alcanzables desplazando el panel fuera del mapa.

El arrastre dentro del mapa mueve el mapa. Para recorrer el contenido vertical se arrastra sobre la cabecera, categorías o tarjetas.

## Comprobación final después del reinicio

El APK final se instaló mediante ADB en Small Phone API 31 Play. Se comprobó la carga del mapa y la selección restaurada de Biblioteca: ventana, marcador con anillo y tarjeta resaltada visibles. Se repitió el giro a horizontal y el regreso a vertical, conservando cámara y selección; la ventana queda encima del marcador sin ocultarlo. Se dejó el emulador en vertical, con texto normal y rotación automática restaurada.

## Aceptación

El usuario aprobó el resultado y autorizó el commit y push en la rama de mejoras. Las pruebas de presentación no cubren dispositivos físicos ni todos los tamaños posibles.
