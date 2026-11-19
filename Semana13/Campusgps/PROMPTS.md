# Bitácora técnica de IA - CampusGPS

Proyecto: Semana13/Campusgps

Rama: `mejora-ia-campusgps`

Herramienta utilizada: Codex

Los prompts de referencia están redactados a partir de los requisitos técnicos de cada mejora. Son formulaciones normalizadas, no transcripciones literales. Las respuestas, decisiones y resultados describen el trabajo realizado.

## Mejora 1: filtro por categoría

| Prompt técnico de referencia | Resumen de la respuesta de la IA | Qué se implementó, adaptó o descartó | Por qué |
| --- | --- | --- | --- |
| Implementa un filtro por categoría en CampusGPS con chips Todos, Estudio, Comida, Eventos y Servicios. Centraliza el estado y el filtrado en MapaViewModel, mantén el orden por distancia y entrega la misma lista al mapa de MapLibre y a las tarjetas. Conserva los permisos y la ubicación. Añade pruebas de filtrado, selección y recálculo. | Conservar la lista completa como fuente, combinarla con la categoría seleccionada mediante StateFlow y exponer una lista filtrada. Dibujar los chips en un Composable que solo recibe valores y callbacks. Derivar el lugar más cercano de los lugares visibles. | Se añadieron categoriaSeleccionada, categorias, lugaresFiltrados y filtrarPorCategoria. PantallaMapa usa lugaresFiltrados tanto para mapa como para lista. FiltrosCategorias presenta los chips. Al ocultar un lugar seleccionado se limpia su selección y la ventana del marcador oculto. | Mantiene MVVM, evita duplicar filtros en los Composables y permite cambiar categorías sin consultar ubicación ni perder distancias. El botón del más cercano sigue apuntando a un lugar visible. |
| Revisa la coherencia entre el filtro, el lugar más cercano y la selección. ¿Cómo evitar que una categoría mantenga una selección que ya no aparece o que borrar el filtro pierda los datos calculados? | Filtrar una vista derivada de los datos, conservar la fuente completa y limpiar solo las selecciones que dejan de pertenecer al conjunto visible. | Se conserva lugares sin filtrar para los cálculos y las pruebas de la Fase 1. Se valida la categoría recibida; se descartan valores desconocidos. Todos restaura la lista completa sin una nueva consulta a Fused Location. | Cambiar la presentación no debe provocar consultas al GPS ni modificar los datos originales. Mantener la selección oculta produciría una pantalla incoherente. |

### Criterios de implementación

- ViewModel sin Context, Composables sin acceso a repositorios y sin cálculos de distancia.
- Categorías obtenidas de los datos locales, con un chip Todos para quitar el filtro.
- El más cercano se interpreta dentro de la categoría activa; al quitar el filtro vuelve a considerar los cinco lugares.
- Los chips comparten el área de la lista para no restar altura adicional al mapa de Small Phone.
- Se mantienen MapLibre, OpenFreeMap, el marcador rojo, las comprobaciones de permisos y los errores existentes.
- Las posiciones controladas se usan exclusivamente en androidTest.

### Validación

- `:app:assembleDebug`, pruebas unitarias y APK de pruebas: correctos.
- Ocho pruebas instrumentadas en Small Phone API 31 Play: correctas, sin fallos ni ignoradas (tres de filtros y cinco de distancias/errores).
- Comprobación visual sin ubicación: Todos muestra los cinco marcadores iniciales; Comida deja únicamente Cafetería Campus en el mapa y la lista, con Sin calcular y sin solicitar permiso.
- Revisión manual con ubicación, selección y cambio de categorías: confirmada por el usuario en API 31 Play.

## Mejora 2: radio de cercanía

| Prompt técnico de referencia | Resumen de la respuesta de la IA | Qué se implementó, adaptó o descartó | Por qué |
| --- | --- | --- | --- |
| Añade un selector de 100, 200 y 500 metros a CampusGPS. Dibuja un círculo alrededor de la ubicación válida del usuario y señala los lugares dentro del radio. Usa MapLibre Compose y OpenFreeMap, conserva el filtro por categoría y mantén los cálculos fuera de los Composables. | Guardar el radio en el ViewModel y combinarlo con las distancias calculadas. Preparar un contorno geográfico y representarlo con una fuente GeoJSON y capas de relleno y borde de MapLibre. | Se añadieron radioMetros, lugaresDentroDelRadio, contornoRadio y cambiarRadio. SelectorRadio ofrece las tres opciones desde la barra superior. Radio.kt genera un anillo cerrado de 64 segmentos y clasifica distancias; las tarjetas presentan Dentro/Fuera del radio junto a la distancia. | El radio está expresado en metros sobre el terreno. Un círculo de tamaño fijo en pantalla no mantendría esa medida al cambiar el zoom. El selector en la barra evita reducir más la altura del mapa o de la lista. |
| Define cómo debe convivir el radio con las categorías y prueba los casos sin ubicación, en el límite del radio y después de cambiar de posición. | El radio solo señala cercanía; el filtro por categoría sigue determinando los lugares visibles. Una distancia desconocida no se marca dentro y cambiar el radio no debe consultar el GPS. | Se mantienen todos los lugares de la categoría, incluidos los que están fuera. La comparación es inclusiva, metros menor o igual al radio. El más cercano continúa siendo el primero de la categoría activa. Se añadieron pruebas unitarias e instrumentadas para los límites, el cambio de radio, el recálculo y la geometría. | Evita confundir un indicador de cercanía con un segundo filtro y conserva el comportamiento de selección, orden y lugar más cercano de la primera mejora. |

### Decisiones y adaptación al proveedor

- Valor inicial: 100 m. Solo se aceptan 100, 200 y 500 m.
- Sin ubicación válida no hay contorno ni clasificación Dentro/Fuera; las tarjetas conservan Sin calcular.
- El ViewModel no recibe Context y los Composables no calculan distancias ni generan el contorno.
- El contorno usa una aproximación esférica con radio terrestre medio de 6 371 008.8 m; la clasificación de lugares usa las distancias de Location.distanceBetween.
- Se comprueba la medida de los vértices con Location.distanceBetween y una tolerancia del 0.6 %, correspondiente a la aproximación esférica.
- La capa se actualiza al cambiar radio o ubicación; el marcador rojo y los marcadores del campus se mantienen.
- Al buscar o producirse un error, el círculo se oculta hasta tener nuevamente una ubicación válida. Las distancias anteriores de la lista se conservan, como en la Fase 1.
- El cambio de radio no modifica la cámara, no solicita permisos y no inicia búsquedas.
- Ajuste de interacción: botones +/− en una columna compacta (+ encima de −) en el lateral superior derecho del mapa, con etiquetas de accesibilidad y objetivos táctiles de 48 dp. Animan el zoom de uno en uno, conservando el centro y limitándolo entre 0 y 22. Se mantienen los gestos; los controles se dibujan sobre los marcadores. El logo se alinea abajo a la izquierda y la atribución expandible abajo a la derecha. Los controles del lateral derecho conservan un margen horizontal de 8 dp y aprovechan los bordes superior e inferior para que no se superpongan en Small Phone. Se descartó MapOverlay.Full tras comprobar que sus controles se superponían en el mapa de poca altura del emulador.
- Latitud y longitud se presentan juntas en una fila centrada, separadas por 12 dp y con seis decimales, para liberar altura al mapa. Es solo formato visual; los cálculos conservan las coordenadas originales.
- Referencia técnica: [fuentes y capas de MapLibre Compose](https://maplibre.org/maplibre-compose/layers/). Se utilizan FillLayer y LineLayer con un polígono; CircleLayer define tamaños de puntos en pantalla.

### Validación

- `:app:assembleDebug`, pruebas unitarias y APK de pruebas: correctos.
- Once pruebas instrumentadas en Small Phone API 31 Play: correctas, sin fallos ni ignoradas (tres de radio, tres de filtros y cinco de distancias/errores).
- Comprobación visual en API 31 Play: el menú ofrece 100, 200 y 500 m; sin ubicación no aparece el círculo. Tras obtener -12.0464, -77.0427983 se ve el círculo azul, que aumenta al elegir 200 m. Biblioteca aparece dentro de 100 m.
- Con Comida seleccionado, Cafetería (109 m) permanece visible y como lugar más cercano: dentro con 200 m y fuera con 100 m, sin nueva búsqueda.
- Ajuste visual posterior: assembleDebug correcto; en API 31 Play se verificaron coordenadas completas en una sola fila, mayor altura del mapa, zoom en el lateral superior derecho y apertura/cierre de la atribución en la esquina inferior derecha. La pantalla conserva círculo, marcador rojo y distancias.
- Revisión manual del usuario: confirmada tras ejecutar la app y revisar la disposición final de las coordenadas, el zoom vertical y la atribución.

## Mejora siguiente acordada

Persistencia del último lugar seleccionado mediante DataStore y un Repository. Todavía no está implementada; se añadirá su registro técnico al desarrollarla.
