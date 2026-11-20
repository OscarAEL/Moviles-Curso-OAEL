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
- Paso 4: cancelación con Job y protección contra doble inicio.
- Paso 5: cálculo de primos en segundo plano, tiempo y cancelación.
- Paso 6: comparación de cargas secuenciales y en paralelo con async.
- Pasos 7 y 8: pendientes.

Las tres tarjetas ya tienen su lógica principal. El error simulado y el reintento
se incorporarán en el paso 7.

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

## Paso 4: cancelación con Job

El ViewModel guarda la corrutina en `trabajo: Job?` y expone
`corriendo: StateFlow<Boolean>`. Si ya está corriendo, `tareaLarga()` no
inicia otra tarea. El bloque `finally` restablece corriendo a false tanto
al completar como al cancelar.

`cancelar()` cancela el Job y publica `Cancelado`. La suspensión `delay`
coopera con la cancelación y se conserva el progreso alcanzado. No se captura
ni se consume la excepción de cancelación.

**Iniciar** se deshabilita mientras corre la tarea y **Cancelar** (OutlinedButton)
solo se habilita durante la ejecución.

### Prueba manual del Paso 4

1. Al abrir, comprobar que Iniciar está habilitado y Cancelar deshabilitado.
2. Pulsar Iniciar: Iniciar debe deshabilitarse y Cancelar habilitarse.
3. Intentar pulsar Iniciar nuevamente: no debe reiniciarse ni duplicarse la tarea.
4. Pulsar Cancelar a mitad: debe aparecer Cancelado y detenerse la barra.
5. Esperar más de cuatro segundos: el progreso debe seguir detenido.
6. Pulsar Iniciar otra vez: la barra debe empezar de cero.
7. Dejar que termine: debe mostrar Terminó y 100 %, con Iniciar habilitado.
8. Comprobar que Tócame responde durante la tarea y que girar la pantalla no la reinicia.

## Paso 5: contar primos

El Repository cuenta los números primos entre 2 y N en
`withContext(Dispatchers.Default)`, con `ensureActive()` en cada vuelta.
La función privada esPrimo prueba divisores hasta la raíz del número, usando
`d.toLong() * d <= n` para evitar desbordes.

El ViewModel mide el tiempo con `System.currentTimeMillis()` y expone los
estados Inicial, Calculando, Listo y Cancelado. Al capturar
`CancellationException`, publica Cancelado y vuelve a lanzar la excepción.
El cálculo tiene su propio Job, independiente de la tarea con progreso.

La pantalla muestra el resultado y los milisegundos en un recuadro verde.
Contar se deshabilita con entradas no enteras, fuera del rango Int o menores
que 2; también mientras hay un cálculo. Cancelar solo se habilita al calcular.

### Prueba manual del Paso 5

1. Probar N = 100, 1000 y 10000: deben salir 25, 168 y 1229 primos.
2. Probar N = 100000 y 500000: deben salir 9592 y 41538.
3. Con un N grande, comprobar que se muestra Calculando y que se puede cancelar.
4. Comprobar que el estado pasa a Cancelado y se permite otro cálculo.
5. Comprobar que el contador Tócame sigue respondiendo durante el cálculo.
6. Vaciar el campo o usar 0, 1 o un número fuera del rango Int:
   Contar debe quedar deshabilitado.
7. Girar la pantalla durante un cálculo: debe continuar sin reiniciarse.

Se mantiene N = 500000 como valor inicial: en Small Phone API 31 PLAY se
obtuvieron 41538 primos en 2153 ms en la medición inicial. El tiempo varía
según el dispositivo y su carga.

## Paso 6: cargas una por una y en paralelo

**Una por una** carga el perfil (2 s) y luego los pedidos (3 s), por lo que tarda
aproximadamente 5 s. **A la vez** lanza ambas llamadas con `async` dentro de
`coroutineScope` y espera los resultados con `await()`: tarda aproximadamente
3 s, el tiempo de la carga más lenta.

El ViewModel publica Cargando antes de iniciar y rechaza otra carga mientras
está en ese estado. Los dos botones se deshabilitan durante la espera y se
muestra un indicador circular. Al finalizar, un recuadro verde muestra el modo,
Ana Torres (ficticia), 3 pedidos y el tiempo medido en milisegundos.

Las llamadas suspend del Repository usan delay, así que no necesitan bloquear
el hilo principal ni simular red real.

### Prueba manual del Paso 6

1. Pulsar Una por una: comprobar el indicador y los botones deshabilitados.
2. Al finalizar, comprobar perfil, 3 pedidos y un tiempo próximo a 5000 ms.
3. Pulsar A la vez: comprobar los mismos datos y un tiempo próximo a 3000 ms.
4. Intentar pulsar de nuevo mientras carga: no debe iniciar otra carga.
5. Durante ambas cargas, comprobar que se puede desplazar la pantalla y que
   Tócame sigue sumando.
6. Girar la pantalla durante una carga: debe continuar sin reiniciarse.

La estructura de paquetes, el diagrama MVVM, las capturas y las respuestas finales
se completarán en el paso 8.
