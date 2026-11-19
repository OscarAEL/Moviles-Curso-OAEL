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

## Mejoras siguientes acordadas

1. Radio de cercanía de 100, 200 y 500 m, con círculo en el mapa y señalización en la lista.
2. Persistencia del último lugar seleccionado mediante DataStore y un Repository.

Se añadirán sus prompts técnicos, decisiones y evidencias cuando se implementen. Estas dos mejoras todavía no están realizadas.
