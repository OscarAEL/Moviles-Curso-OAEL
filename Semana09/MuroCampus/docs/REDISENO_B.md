# Rediseño B: Comunidad gráfica

Se implementa en Jetpack Compose la dirección visual **B**, elegida por el estudiante entre los prototipos de Claude. La referencia original está en [referencias](referencias/README.md).

## Identidad

- Azul eléctrico, acentos lima y rosa, superficies claras y sombras sólidas.
- Archivo Black para titulares; DM Sans para lectura, campos y acciones. Los archivos se incluyen en la app con sus [licencias](fuentes/README.md).
- Marca Muro / Campus en dos líneas, contador inclinado, tarjetas con borde oscuro y etiqueta de usuario derivada del userId real.
- Logo original de Tecsup en una placa clara, arriba a la derecha, con su color y proporción originales.
- Detalle con título y texto completos, acción Eliminar y comentarios alternados con el correo en azul.
- Formulario con etiquetas flotantes lima y rosa y botón Publicar.

## Ajustes para el uso real

El botón + tiene una zona inferior reservada para que las tarjetas puedan desplazarse completamente por encima de él. Esa zona se oculta al abrir el teclado.

Los tres encabezados se desplazan con su pantalla. Se conserva una protección azul para los iconos de la barra de estado. Las listas reciben los insets del Scaffold y el formulario dispone de desplazamiento con teclado abierto.

Se respeta la escala de fuente del sistema; con texto ampliado, el contador pasa debajo de la marca para evitar superposiciones. El tema oscuro conserva la misma identidad B con superficies oscuras.

El teclado que aparece es el del dispositivo, no una imagen dibujada. Los nombres de usuario, cantidades y contenidos siguen viniendo de los datos reales de la API.

## Funciones

Se conserva MVVM + Repository y las funciones del laboratorio: lista, filtros, búsqueda local, detalle con cinco comentarios, creación simulada, eliminación con confirmación, carga, errores, reintento y estado vacío.

El rediseño no modifica PostApi, PostRepository ni PostsViewModel.

## Verificación

- `:app:build` completo: debug, release con R8, pruebas locales y Android Lint.
- Ocho pruebas instrumentadas aprobadas desde Android Studio.
- Revisión visual de la lista, el detalle y el formulario en el emulador.
- Edición de ambos campos con teclado abierto y validación del formulario vacío.
- Publicación desde la nueva pantalla: id 101, campos limpios y aviso visible.
- Error sin internet, Reintentar y recuperación del filtro de Usuario 2 con diez publicaciones.
- Cinco capturas nuevas conservadas junto a la evidencia original del laboratorio.
