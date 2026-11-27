# Registro de asistencia de IA

Asistencia utilizada: Codex/ChatGPT. Según la autorización del docente comunicada por el estudiante, la IA se permite en ambas fases. Este registro recoge instrucciones de trabajo y decisiones técnicas de la FASE 2; no atribuye respuestas a otras herramientas.

Rama: `mejora-ia-avisoscampus`.

| Prompt o instrucción de trabajo | Propuesta de la IA | Qué adapté o descarté | Por qué |
|---|---|---|---|
| Separar los recordatorios urgentes de los avisos normales manteniendo MVVM + Repository. | Crear Avisos con IMPORTANCE_DEFAULT y Urgentes con IMPORTANCE_HIGH; elegir el canal por el origen del aviso. | Se conserva el canal original y se comprueba el bloqueo del canal elegido antes de notificar. No se mueve NotificationManager al ViewModel. | El usuario controla cada canal por separado y la lógica del ViewModel sigue siendo comprobable sin Android. |
| Mostrar textos largos completos al expandir una notificación. | Añadir NotificationCompat.BigTextStyle y un texto ficticio extenso para demostrarlo. | El estilo se configura solamente en Notificador; el ViewModel decide el contenido del aviso. | La presentación permanece separada de la lógica y el texto completo se conserva en el historial y en la notificación expandida. |
| Conservar el historial al cerrar la app con DataStore, manteniendo la interface del Repository. | Inyectar una única instancia de DataStore desde Application y hacer registrar suspend para guardar antes de emitir el evento. | Se añade un Serializer local versionado, IDs calculados dentro de updateData y manejo de IOException en el ViewModel; se mantiene el Repository en memoria para pruebas. | Las escrituras no bloquean el hilo principal, son atómicas y el aviso solo se emite después de guardarse. Ni Repository ni ViewModel reciben Context. |
| Comprobar que la persistencia y los errores no rompen las funciones originales. | Probar serialización, reapertura del archivo, escrituras concurrentes y fallo de guardado con un Repository falso. | Se distingue el error de guardado del error de permiso: Abrir ajustes solo aparece cuando puede resolver el problema de notificaciones. | Cambiar un permiso no arregla un fallo de almacenamiento y no debe ocultar ese error. |

## Criterios de revisión

- Ningún Context, NotificationManager ni BroadcastReceiver en el ViewModel.
- El Repository no recibe Context.
- PendingIntent explícito e inmutable.
- Todo receptor registrado en la pantalla se retira en onDispose.
- Las mejoras se comprueban con pruebas antes de subir los commits.

## Resultado de la revisión

Compilación y Lint correctos; 10 pruebas locales y 9 instrumentadas correctas. La persistencia se comprobó también deteniendo y volviendo a abrir el proceso de la app. Los resultados están en [docs/PRUEBAS.md](docs/PRUEBAS.md).

Referencias oficiales consultadas: [DataStore](https://developer.android.com/topic/libraries/architecture/datastore), [canales](https://developer.android.com/develop/ui/views/notifications/channels) y [notificaciones expandidas](https://developer.android.com/develop/ui/views/notifications/expanded).
