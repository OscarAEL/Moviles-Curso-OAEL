# Registro de IA - fase 2

Proyecto: CentroTareas. Rama: `mejora-ia-centrotareas`.
Asistente de implementación: Codex. El docente autorizó IA en ambas fases.
Los prototipos del rediseño se trabajarán con Claude y se registrarán cuando
se soliciten los prompts correspondientes.

Este registro distingue los mensajes reales del estudiante de las
instrucciones técnicas derivadas por el asistente a partir del laboratorio.
No se atribuyen respuestas a Gemini.

| Prompt o instrucción | Qué respondió la IA | Qué se cambió o descartó | Por qué |
| --- | --- | --- | --- |
| Estudiante: «listo continuemos con la fase 2» | Codex propuso progreso desde el Repository con Flow, progreso de primos y tabla de tiempos; comenzó solo la primera mejora. | Las otras dos mejoras y el rediseño quedan para pasos posteriores. Se conserva la revisión del estudiante antes de cada commit y push. | Cumplir el mínimo de tres mejoras funcionales y mantener cada cambio verificable por separado. |
| Instrucción técnica derivada por Codex del PDF: mover la tarea simulada al Repository y exponer progreso con flow, emit y flowOn(Dispatchers.Default), conservando MVVM y cancelación. | Se añadió progresoTarea(): Flow<Float> a la interfaz; el Repository emite de 0 a 100 % y el ViewModel recoge el flujo con su Job existente. | Se retiraron del ViewModel el ciclo de espera y el cambio manual de dispatcher. Se mantuvieron StateFlow, finally, control de doble inicio y registros HILO. No se crean scopes independientes ni se consume CancellationException. | El Repository produce los datos de progreso; el ViewModel coordina el ciclo de vida y los estados. Cancelar el Job detiene toda la tarea mediante concurrencia estructurada. |

## Revisión de la primera mejora

- Implementación preparada en la rama de fase 2.
- assembleDebug y lintDebug: correctos.
- Small Phone API 31 PLAY: contador activo durante el avance; Iniciar
  deshabilitado; cancelación conservada al 45 % después de esperar.
- Reinicio desde cero, rotación durante la tarea y finalización al 100 %:
  comprobados en el emulador.
- Logcat: main antes y después; DefaultDispatcher-worker-1 dentro del
  productor del Repository. La ejecución completa duró unos cuatro segundos.
- Commit y push pendientes de esa confirmación.
