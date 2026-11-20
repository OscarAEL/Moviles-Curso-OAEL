# CentroTareas

Laboratorio 14 de Programación en Móviles: hilos en Android.

Aplicación en Kotlin con Jetpack Compose, Material 3 y arquitectura MVVM + Repository.
Proyecto ubicado en `Semana14/centrotareas` del repositorio Moviles-Curso-OAEL.
La IA está autorizada por el docente en ambas fases.

## Avance

- Paso 0: proyecto base compilado y probado.
- Paso 1: demostración del bloqueo del hilo principal.
- Paso 2: tarea con corrutinas y registros del hilo en Logcat.
- Paso 3: barra y porcentaje de progreso con StateFlow.
- Pasos 4 a 8: pendientes.

Los botones de primos y cargas permanecen deshabilitados hasta implementar sus pasos.

## Ejecutar

Abrir esta carpeta en Android Studio, sincronizar Gradle y ejecutar el módulo app.
Android mínimo: API 26. Emulador usado para las pruebas: Small Phone API 31 PLAY.
Los datos son ficticios; las cargas del Repository se simulan con delay.

## Observación del Paso 1

Al pulsar **Tarea larga (MAL)**, el ViewModel cambia el mensaje a
`Trabajando...`, bloquea el hilo principal con `Thread.sleep(4000)` y finalmente
lo cambia a `Terminó`.

Durante esos cuatro segundos, el hilo principal no puede dibujar los cambios ni
atender los toques. Por eso `Trabajando...` no llega a verse: cuando la pantalla
puede actualizarse, el estado ya contiene `Terminó`. El contador **Tócame** no se
actualiza durante la espera; los eventos pueden procesarse después del bloqueo.

Si la espera fuera de diez segundos y hubiera eventos de entrada sin atender,
Android podría detectar un ANR y mostrar el diálogo de aplicación que no responde.
La aparición exacta del diálogo depende del sistema y de las condiciones de ejecución.

Este bloqueo fue intencional y temporal, exclusivamente para el paso 1.
En el paso 2 se eliminó `Thread.sleep` del código Kotlin y se reemplazó por una corrutina.
La demostración original se conserva en el historial de Git.

### Prueba manual del Paso 1

1. Pulsar Tócame y comprobar que suma.
2. Pulsar Tarea larga (MAL) e inmediatamente intentar pulsar Tócame.
3. Comprobar que la pantalla no responde durante unos cuatro segundos.
4. Al finalizar, comprobar que aparece Terminó y el contador vuelve a responder.
5. Comprobar que Trabajando... no se muestra durante la espera.

## Paso 2: tarea sin bloquear la pantalla

El botón **Tarea larga** llama a `tareaLarga()`. El ViewModel lanza una corrutina
con `viewModelScope.launch`, publica `Trabajando...`, cambia a
`Dispatchers.Default` con `withContext` y espera cuatro segundos con `delay`.
Al regresar al hilo principal publica `Terminó`.

`delay` suspende la corrutina sin bloquear un hilo. Se usa dentro de
`withContext(Dispatchers.Default)` para observar el cambio de hilo que pide el
laboratorio; una espera simulada con delay por sí sola no requiere ese dispatcher.

### Prueba manual del Paso 2

1. Pulsar Tarea larga: debe aparecer Trabajando...
2. Pulsar Tócame varias veces durante la espera: el contador debe seguir sumando.
3. Al cabo de unos cuatro segundos debe aparecer Terminó.
4. Filtrar Logcat por HILO: debe mostrar Antes: main, Dentro:
   DefaultDispatcher-worker-... y Despues: main.

## Paso 3: progreso con StateFlow

El ViewModel expone `progreso: StateFlow<Float>` de solo lectura. Al iniciar,
reinicia el progreso a 0 y muestra `Trabajando...`. Dentro de
`withContext(Dispatchers.Default)`, realiza 100 iteraciones con `delay(40)`
y publica `i / 100f` en cada una. Al finalizar muestra `Terminó`.

La pantalla recoge el estado y lo muestra con `LinearProgressIndicator` y el
porcentaje debajo. El botón ahora se llama **Iniciar**.

### Prueba manual del Paso 3

1. Pulsar Iniciar: la barra debe comenzar en 0 %.
2. Comprobar que avanza hasta 100 % en aproximadamente cuatro segundos.
3. Pulsar Tócame durante el avance: el contador debe seguir sumando.
4. Al terminar, comprobar que aparece Terminó y 100 %.
5. Pulsar Iniciar otra vez: la barra debe empezar de cero.
6. Girar el dispositivo durante la tarea: el progreso debe continuar.

El control de doble inicio y la cancelación se incorporarán en el paso 4.

La estructura de paquetes, el diagrama MVVM, las capturas y las respuestas finales
se completarán en el paso 8.
