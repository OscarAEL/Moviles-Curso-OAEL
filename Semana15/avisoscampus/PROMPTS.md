# Registro de asistencia de IA

Asistencia utilizada: Codex/ChatGPT. Según la autorización del docente comunicada por el estudiante, la IA se permite en ambas fases. Este registro recoge instrucciones de trabajo y decisiones técnicas de la FASE 2; no atribuye respuestas a otras herramientas.

Rama: `mejora-ia-avisoscampus`.

| Prompt o instrucción de trabajo | Propuesta de la IA | Qué adapté o descarté | Por qué |
|---|---|---|---|
| Separar los recordatorios urgentes de los avisos normales manteniendo MVVM + Repository. | Crear Avisos con IMPORTANCE_DEFAULT y Urgentes con IMPORTANCE_HIGH; elegir el canal por el origen del aviso. | Se conserva el canal original y se comprueba el bloqueo del canal elegido antes de notificar. No se mueve NotificationManager al ViewModel. | El usuario controla cada canal por separado y la lógica del ViewModel sigue siendo comprobable sin Android. |

## Criterios de revisión

- Ningún Context, NotificationManager ni BroadcastReceiver en el ViewModel.
- El Repository no recibe Context.
- PendingIntent explícito e inmutable.
- Todo receptor registrado en la pantalla se retira en onDispose.
- Las mejoras se comprueban con pruebas antes de subir los commits.
