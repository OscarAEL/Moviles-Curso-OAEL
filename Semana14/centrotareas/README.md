# CentroTareas

Laboratorio 14 de Programación en Móviles: hilos en Android.

Aplicación en Kotlin con Jetpack Compose, Material 3 y arquitectura MVVM + Repository.
Proyecto ubicado en `Semana14/centrotareas` del repositorio Moviles-Curso-OAEL.
La IA está autorizada por el docente en ambas fases.

## Avance

- Paso 0: proyecto base compilado y probado.
- Paso 1: demostración del bloqueo del hilo principal.
- Pasos 2 a 8: pendientes.

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

Este bloqueo es intencional y temporal, exclusivamente para el paso 1.
En el paso 2 se eliminará `Thread.sleep` y se usará una corrutina.

### Prueba manual del Paso 1

1. Pulsar Tócame y comprobar que suma.
2. Pulsar Tarea larga (MAL) e inmediatamente intentar pulsar Tócame.
3. Comprobar que la pantalla no responde durante unos cuatro segundos.
4. Al finalizar, comprobar que aparece Terminó y el contador vuelve a responder.
5. Comprobar que Trabajando... no se muestra durante la espera.

La estructura de paquetes, el diagrama MVVM, las capturas y las respuestas finales
se completarán en el paso 8.
