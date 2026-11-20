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

## Mejora 2: progreso de primos

| Prompt o instrucción | Qué respondió la IA | Qué se cambió o descartó | Por qué |
| --- | --- | --- | --- |
| Estudiante: «primero terminemos con lo q falta», y dejar el rediseño para un traspaso posterior a Gemini y Claude. | Codex continuó las mejoras funcionales pendientes, conservando la revisión antes de commit y push. | El rediseño no se implementa todavía; el prompt de traspaso se preparará después. | Completar primero los requisitos funcionales y mantener un punto de partida verificable para el rediseño. |
| Instrucción técnica derivada del PDF: mostrar el porcentaje del cálculo de primos sin bloquear ni romper su cancelación. | El Repository recibe un callback de progreso y el ViewModel expone Calculando(progreso). La UI dibuja una barra y porcentaje. | Se limita la emisión a cambios de porcentaje entero y se mantiene ensureActive; no se añaden esperas artificiales ni dependencias nuevas. | Evitar cientos de miles de actualizaciones de UI, conservar el rendimiento y poder cancelar. |

## Mejora 3: comparación de tiempos

| Prompt o instrucción | Qué respondió la IA | Qué se cambió o descartó | Por qué |
| --- | --- | --- | --- |
| Continuación de «primero terminemos con lo q falta» e instrucción derivada del PDF: acumular tiempos de Una por una y A la vez en una tabla. | Codex añadió TiempoCarga, una lista inmutable mediante StateFlow y una tabla dentro de Dos cargas. | Solo se registran cargas exitosas; el reintento exitoso también cuenta. Se conserva en ViewModel y no se añade persistencia ni dependencias. | Comparar mediciones reales de la sesión sin mezclar errores con resultados ni introducir una cuarta mejora de almacenamiento. |

## Revisión de la primera mejora

- Implementación preparada en la rama de fase 2.
- assembleDebug y lintDebug: correctos.
- Small Phone API 31 PLAY: contador activo durante el avance; Iniciar
  deshabilitado; cancelación conservada al 45 % después de esperar.
- Reinicio desde cero, rotación durante la tarea y finalización al 100 %:
  comprobados en el emulador.
- Logcat: main antes y después; DefaultDispatcher-worker-1 dentro del
  productor del Repository. La ejecución completa duró unos cuatro segundos.
- Commit y push autorizados por el estudiante.

## Revisión de las mejoras 2 y 3

- Compilación y Lint correctos con las tres mejoras.
- Primos: porcentaje observado al 82 %, resultado 96469 con el límite inicial,
  cancelación de un cálculo grande y nuevo resultado de 168 para N = 1000.
- Tabla: filas secuencial 5003 ms y paralela 3005 ms; el error no agrega filas;
  el reintento exitoso agrega una tercera de 3002 ms.
- Las tres filas permanecen después de rotar y regresar a vertical.
- Se guardaron capturas en docs/capturas-fase2.
- Mejoras verificadas y autorizadas por el estudiante. Se completan los tres
  commits separados en la rama `mejora-ia-centrotareas` y se realiza el push a GitHub.
  El rediseño y su prompt de traspaso se coordinarán en la siguiente etapa.
