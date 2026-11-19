# Evidencias de CampusGPS

Las capturas actuales para preparar el rediseño están en [actuales](actuales/README.md). Utiliza esos siete PNG como referencias para Claude.

Los cinco PNG de esta carpeta corresponden a la evidencia histórica de la Fase 1.

Capturas de la app en el emulador Small Phone API 31 Play (Android 12).
Se guardan desde ADB, sin editar su contenido.

- `01-permiso.png`: diálogo real del permiso de ubicación.
- `02-ubicacion.png`: coordenadas y marcador rojo en el punto base de Lima.
- `03-marcadores.png`: lugares del campus y nombre/categoría al tocar un marcador.
- `04-lista-ordenada.png`: lista con distancias y lugar más cercano.
- `05-error-gps.png`: error con la ubicación del dispositivo apagada.

La simulación se realiza exclusivamente en el emulador mediante ADB. No se agregan coordenadas de prueba al código de producción.
