# Capturas actuales para el rediseño

Capturas reales, sin edición, de CampusGPS en Small Phone API 31 Play (Android 12), resolución 720 × 1280.

Referencia: rama `mejora-ia-campusgps`, código del commit `0eea715`, 19 de noviembre de 2026.

El punto base de Lima se simula únicamente en el emulador mediante ADB; no se cambia el código de la app.

Adjunta estos siete PNG a Claude junto con el prompt del rediseño:

| Imagen | Contenido |
| --- | --- |
| [01-sin-ubicacion.png](01-sin-ubicacion.png) | Sin ubicación: coordenadas vacías y distancias sin calcular |
| [02-ubicacion-radio-100.png](02-ubicacion-radio-100.png) | Ubicación obtenida, círculo de 100 m y lugar más cercano |
| [03-menu-radio.png](03-menu-radio.png) | Opciones del radio: 100, 200 y 500 m |
| [04-filtro-comida.png](04-filtro-comida.png) | Filtro Comida, distancia y estado Fuera del radio |
| [05-lugar-seleccionado.png](05-lugar-seleccionado.png) | Lugar seleccionado: nombre, categoría y tarjeta resaltada |
| [06-lista-desplazada.png](06-lista-desplazada.png) | Lista desplazada con más tarjetas y distancias |
| [07-error-gps.png](07-error-gps.png) | GPS apagado: mensaje de error y Reintentar |

Las imágenes se obtuvieron con screencap y pull de ADB, sin edición. El mapa y la lista se movieron mediante la UI; la ventana seleccionada se encuadró arrastrando el mapa para que su nombre quedara dentro del recuadro.

Los recortes y el espacio disponible son los reales de la app. No se recrearon estados transitorios de búsqueda ni diálogos de permiso. La captura de GPS muestra un fallo real provocado al desactivar temporalmente la ubicación; se restauró la ubicación activa y se verificó la recuperación después.

Las capturas antiguas de la carpeta superior pertenecen a la Fase 1. Para el rediseño utiliza esta carpeta.

[Ver la galería en el README del proyecto](../../../README.md).
