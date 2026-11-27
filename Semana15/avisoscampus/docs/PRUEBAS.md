# Validación del laboratorio

Fecha: 27 de noviembre de 2026.

## Entorno

- Kotlin + Compose + Material 3, minSdk 26 y targetSdk 37.
- Emulador Android 11/API 30: arranque inicial, canal y primer aviso comprobados.
- Emulador AvisosCampus_API_33, Android 13: pruebas de aceptación del permiso y del sistema.
- Los comandos se ejecutan desde `Semana15/avisoscampus`.

## Comprobaciones de compilación de la FASE 1

- `:app:assembleDebug`: correcto.
- `:app:assembleDebugAndroidTest`: correcto.
- `:app:testDebugUnitTest`: seis pruebas correctas, ninguna omitida ni fallida.
- `:app:lintDebug`: tarea completada correctamente.
- Búsqueda de Context, NotificationManager y BroadcastReceiver en viewmodel/: sin coincidencias.

Las pruebas locales comprueban el recordatorio de diez segundos, bloqueo de duplicados, cancelación y reinicio, IDs distintos y orden del historial, recuperación del permiso y filtrado del modo avión. Una prueba adicional es la comprobación aritmética de la plantilla.

## FASE 2: resultado final

Rama `mejora-ia-avisoscampus`:

- `:app:assembleDebug`, `:app:assembleDebugAndroidTest`, `:app:testDebugUnitTest` y `:app:lintDebug`: correctos.
- **10 pruebas locales correctas**, sin omisiones ni fallos.
- **9 pruebas instrumentadas correctas** en Android 13: SinPermisoTest (1), AceptacionTest (3), AperturaCerradaTest (1), HistorialUiTest (2) y MejorasNotificacionTest (2).
- Las pruebas adicionales verifican el canal Urgentes y su uso exclusivo por los recordatorios, el texto completo en BigTextStyle, el formato de almacenamiento, la reapertura de DataStore, IDs sin duplicados con escrituras concurrentes y el manejo de un fallo de guardado.
- Se repitió toda la aceptación de la FASE 1 con el Repository persistente.
- Comprobación adicional en la app: `am force-stop`, nueva apertura y revisión del historial. Los tres avisos previos conservaron título, cuerpo, origen y hora; luego se pudo generar otro aviso.
- Revisión de arquitectura: no hay Context en data/ ni Context, NotificationManager o BroadcastReceiver en viewmodel/.
- Las pruebas instrumentadas restablecen únicamente el historial ficticio del emulador antes de cada caso; no se deben ejecutar sobre datos que se quieran conservar.

Evidencias adicionales de esta rama: [texto expandido](capturas/06-texto-expandido.png) e [historial después de reiniciar el proceso](capturas/07-historial-persistente.png).

Para reproducir todas las pruebas instrumentadas de la FASE 2, añadir `com.eneque.avisoscampus.MejorasNotificacionTest` a la segunda invocación de instrumentación indicada más abajo.

## Aceptación de la FASE 1 en Android 13

Siete pruebas instrumentadas correctas, ejecutadas en grupos según el estado del permiso: SinPermisoTest (1), AceptacionTest (3), AperturaCerradaTest (1) e HistorialUiTest (2).

| Prueba de aceptación | Resultado |
|---|---|
| Canal Avisos con importancia predeterminada | Correcto |
| Diálogo nativo y respuesta denegada | Correcto |
| Aviso inmediato con título y texto | Correcto |
| Dos notificaciones con IDs distintos | Correcto |
| Tocar una notificación abre la app desde segundo plano y la retira | Correcto |
| Tocar una notificación con la Activity cerrada crea otra y la retira | Correcto |
| Cuenta regresiva de diez segundos | Correcto |
| Minimizar durante la cuenta: llega el aviso | Correcto |
| Cancelar no registra ni emite un aviso | Correcto |
| Modo avión activado y desactivado con escucha habilitada | Correcto |
| Modo avión sin escucha: no registra avisos | Correcto |
| Permiso denegado: historial, mensaje y acceso a Ajustes | Correcto |
| Conceder en Ajustes: mensaje eliminado al regresar | Correcto |
| Rotación: conserva ViewModel, cuenta, historial y escucha | Correcto |
| Historial vacío y límite de 20 filas | Correcto |
| Receptor retirado en onDispose y ViewModel sin APIs de Android | Correcto |

Las capturas reales del emulador se guardaron en `docs/capturas` y se revisaron visualmente. Las pruebas distinguen los avisos individuales del resumen de grupo que Android puede generar automáticamente.

## Reproducir las pruebas instrumentadas

Compilar los dos APK, instalarlos en un emulador API 33+ y ejecutar:

```powershell
adb shell pm revoke com.eneque.avisoscampus android.permission.POST_NOTIFICATIONS
adb shell pm clear-permission-flags com.eneque.avisoscampus android.permission.POST_NOTIFICATIONS user-set
adb shell pm clear-permission-flags com.eneque.avisoscampus android.permission.POST_NOTIFICATIONS user-fixed
adb shell am instrument -w -r -e class com.eneque.avisoscampus.SinPermisoTest com.eneque.avisoscampus.test/androidx.test.runner.AndroidJUnitRunner
adb shell am instrument -w -r -e class com.eneque.avisoscampus.AceptacionTest,com.eneque.avisoscampus.AperturaCerradaTest,com.eneque.avisoscampus.HistorialUiTest com.eneque.avisoscampus.test/androidx.test.runner.AndroidJUnitRunner
```

SinPermisoTest concede el permiso al final para comprobar la recuperación y preparar las otras pruebas. No revocar el permiso durante una instrumentación: Android puede terminar el proceso. Las pruebas manipulan únicamente datos ficticios y los ajustes del emulador de prueba; al finalizar dejan el modo avión desactivado.
