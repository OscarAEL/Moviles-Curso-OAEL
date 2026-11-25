# Prompt para Claude: prototipos del rediseño de MuroCampus

Copia el texto siguiente en Claude. Puedes adjuntar las capturas de `docs/capturas/` como referencia funcional. Después elige una propuesta y comparte sus imágenes con el asistente que implementará el diseño en Kotlin.

---

Actúa como diseñador senior de interfaces Android. Propón un rediseño visual de **MuroCampus**, una app académica de publicaciones, mediante prototipos en imagen. Primero quiero elegir el diseño; la implementación Android se hará después.

## Qué necesito recibir

1. **Tres propuestas visuales diferentes**, todas completas y coherentes:
   - A: académica, limpia y sobria.
   - B: comunidad universitaria, cercana y con más personalidad.
   - C: editorial contemporánea, con jerarquía tipográfica marcada.
2. Para cada propuesta, un tablero con **tres pantallas de teléfono**: lista, detalle y crear publicación. Muéstralas de frente, con texto legible y sin marcos decorativos que oculten contenido. Usa un tamaño de referencia de 390 × 844 y escala suficiente para comparar detalles.
3. Un tablero adicional con los estados de carga, error sin conexión, lista vacía, éxito de publicación y confirmación/éxito de eliminación.
4. **Entrega los prototipos como imágenes generadas, visibles en la conversación y descargables en PNG o JPEG. Solo quiero ver imágenes del rediseño para elegir una propuesta. No entregues código Kotlin, Java, HTML, CSS, JavaScript, SVG ni artefactos de código. No sustituyas las imágenes por descripciones o instrucciones para dibujarlas. Si esta sesión no permite generar imágenes, indícalo claramente antes de continuar, sin reemplazarlas por código.**
5. Puedes acompañar las imágenes con una explicación breve y una ficha de colores hexadecimales, tipografía, espaciados, radios, elevaciones e iconos. El entregable principal son las imágenes, no el texto.
6. Recomienda una de las tres propuestas y explica brevemente tu criterio. La implementación de la app se hará después, con otro asistente, a partir de las imágenes que yo elija.

## Funcionalidad existente que debes conservar

La app consume JSONPlaceholder y usa Jetpack Compose Material 3. Ya está terminado el laboratorio, incluidas sus dos fases. El diseño nuevo debe conservar todos los flujos y estados.

### Lista
- Barra superior con **MuroCampus** sobre fondo **azul**.
- Fila horizontal desplazable de chips: **Todos**, **Usuario 1** hasta **Usuario 10**. El activo se ve relleno.
- Campo **Buscar por título** que filtra localmente los datos descargados.
- Contador de publicaciones: 100 en Todos y 10 al elegir un usuario antes de aplicar búsqueda.
- Tarjetas con etiqueta **Usuario N**, título en negrita y resumen del contenido de máximo dos líneas.
- Botón flotante **+** con propósito claro: crear publicación.
- Mantén el orden barra, filtros, búsqueda y lista.

### Detalle
- Navegación **Atrás** que regresa a la lista.
- Título y contenido completos de la publicación.
- Acción **Eliminar**, con confirmación antes del envío y estado de progreso/error/éxito.
- Debajo, **Comentarios (N)**; normalmente son cinco. Cada comentario muestra el correo en azul y su contenido.
- El texto largo debe desplazarse verticalmente; no lo recortes para que entre artificialmente en una pantalla.

### Crear publicación
- Navegación **Atrás**.
- Campos **Título** y **Contenido**.
- Botón **Publicar**, deshabilitado si algún campo está vacío o solo contiene espacios, y durante el envío.
- Progreso del envío.
- Éxito: **Publicado con id 101**, con los campos limpios.
- En caso de error, se conserva el texto y hay reintento.
- El formulario debe seguir siendo usable cuando se abre el teclado.

### Estados obligatorios
- Carga: indicador circular y **Cargando publicaciones…**
- Error: recuadro con color de error, **Sin conexión a internet. Revisa tu red e inténtalo otra vez.** y botón **Reintentar**.
- Vacío: **Sin publicaciones**.
- Confirmación de eliminación: explicar que el servicio simula el cambio; acciones **Cancelar** y **Eliminar**.
- Éxito al eliminar: **Eliminado (simulación). El servidor no guarda este cambio.**
- Distingue estados y acciones mediante iconos/texto además del color.

## Datos y límites

- Una publicación solo tiene userId, id, title y body.
- Un comentario tiene postId, id, name, email y body.
- Ejemplo de título real: **sunt aut facere repellat provident occaecati excepturi optio reprehenderit**.
- Ejemplo de contenido: **quia et suscipit / suscipit recusandae consequuntur...**
- Ejemplo de correo de comentario: **Eliseo@gardner.biz**.
- Los textos originales son lorem ipsum; no hace falta traducir el contenido de la API.
- No inventes perfiles reales, fotos, likes, chat, categorías, autenticación, archivos adjuntos ni funciones que todavía no existen.
- JSONPlaceholder **no persiste POST ni DELETE**. El diseño debe comunicar esta limitación de forma breve y clara.
- La app no tiene caché persistente; al iniciar sin internet debe mostrar error con reintento. No diseñes un modo sin conexión que sugiera funciones inexistentes.
- El interceptor Accept es interno; no necesita aparecer en la interfaz.

## Criterios visuales

- Debe sentirse como una app Android cuidada y realizable en Compose Material 3.
- Conserva el azul de la barra; puedes proponer una paleta complementaria y estilos diferentes para las tarjetas y estados.
- Prioriza lectura, contraste, tamaños de interacción de al menos 48 dp y adaptación a texto grande.
- Respeta barras del sistema, navegación inferior del dispositivo y teclado.
- Evita fondos recargados y efectos difíciles de trasladar a componentes nativos.
- Mantén la identidad de cada propuesta en sus tres pantallas.
- Para la propuesta recomendada, añade una variante oscura de la lista.

Antes de entregar, revisa que no falte ningún elemento obligatorio. Quiero comparar las imágenes, elegir una propuesta y después pasar las imágenes elegidas al desarrollador para implementarlas sin cambiar MVVM + Repository.
