# Prompt para Claude: rediseño visual completo de MuroCampus

Usa preferentemente una conversación nueva para evitar que las primeras propuestas condicionen el resultado. Adjunta las capturas actuales únicamente como inventario de funciones. Copia el texto siguiente.

---

Actúa como director de arte y diseñador senior de producto móvil. Quiero un **REDISEÑO VISUAL COMPLETO de MuroCampus**, una app Android de publicaciones universitarias.

Las capturas adjuntas documentan lo que la app hace. **No son una referencia de estilo que debas reproducir.** El diseño actual es una implementación académica básica. Replantea su lenguaje visual desde cero.

Las propuestas anteriores cambiaron detalles pequeños. Ahora necesito una transformación evidente de la composición, la jerarquía, la tipografía y los componentes. Si al colocar tu propuesta al lado de la app actual solo se aprecia otro color, algunos bordes o iconos nuevos, la propuesta no cumple el encargo.

## Entregable: imágenes del rediseño

- Genera **imágenes reales de los prototipos**, visibles en la conversación y descargables en PNG o JPEG.
- **No entregues código**, incluyendo Kotlin, Java, HTML, CSS, JavaScript, SVG o artefactos de programación.
- No sustituyas las imágenes por descripciones, instrucciones para dibujar ni prompts para otro generador.
- Si esta sesión no tiene capacidad para generar imágenes, indícalo antes de continuar.
- La implementación de la app se hará posteriormente, por otro asistente, cuando yo elija las imágenes.
- Puedes acompañar las imágenes con una explicación muy breve; las imágenes son el resultado principal.

## Libertad creativa y alcance

Rediseña el aspecto de **todas las pantallas**, no únicamente la lista.

Puedes cambiar radicalmente:
- Composición del encabezado, escala de la marca y relación entre título y acciones.
- Sistema tipográfico, tamaños, pesos y contraste entre títulos, metadatos y contenido.
- Diseño de las tarjetas: anatomía, agrupación, bordes, fondos, radios y jerarquía.
- Aspecto de filtros, búsqueda, contador y botón flotante.
- Composición del detalle, presentación de comentarios y acciones.
- Organización visual del formulario, campos y confirmaciones.
- Paleta completa, superficies, espacios, iconografía y profundidad.
- Tratamiento visual de carga, errores y estados vacíos.
- Elementos gráficos decorativos abstractos que aporten identidad universitaria sin inventar datos.

No es obligatorio parecerse a los componentes predeterminados de Material 3. Debe poder implementarse después en Jetpack Compose, pero con una identidad propia.

Debes conservar las funciones, los datos y las pocas restricciones del laboratorio indicadas abajo. Esas restricciones no obligan a conservar las proporciones, tarjetas, estilos ni espacios de las capturas actuales.

## Tres direcciones artísticas realmente diferentes

Genera un tablero independiente para cada dirección, con la lista, el detalle y la pantalla de crear. Muestra las pantallas de frente, completas y con texto legible; sin perspectivas ni marcos decorativos que reduzcan el área útil. Tamaño de referencia: 390 × 844.

**A. Campus editorial**
- Fondo marfil o blanco cálido, azul tinta y un acento cálido controlado.
- Jerarquía tipográfica editorial, titulares con carácter y composición cuidada.
- Superficies planas, separadores y bloques con una anatomía propia.
- Sensación de publicación universitaria contemporánea.

**B. Comunidad gráfica**
- Azul eléctrico, superficies claras y un acento vivo bien contrastado.
- Tipografía de gran personalidad, bloques expresivos y composición dinámica.
- Etiquetas de usuario y acciones con un sistema visual reconocible.
- Sensación joven, enérgica y cercana, manteniendo la lectura de textos largos.

**C. Campus nocturno**
- Fondo grafito o azul noche, encabezado azul y acentos luminosos moderados.
- Tipografía precisa, capas tonales y componentes de geometría propia.
- Jerarquía y densidad distintas de las otras dos propuestas.
- Diseña este concepto desde el inicio para modo oscuro, no como una recoloración de A o B.

Cada dirección debe diferenciarse de las otras en **al menos cinco aspectos**: composición, tipografía, tarjetas, filtros, formulario, espaciado, iconografía o profundidad. No reutilices el mismo wireframe cambiando solamente la paleta.

En cada tablero, las tres pantallas deben compartir la misma identidad. Después recomienda una dirección y entrega un tablero adicional de sus estados: carga, error de conexión, vacío, éxito al crear y confirmación/éxito al eliminar.

## Restricciones funcionales del laboratorio

### Lista
- Encabezado superior azul con el nombre **MuroCampus**. Tienes libertad para elegir el tono de azul, su altura, tipografía y composición.
- Fila horizontal desplazable de chips **Todos**, **Usuario 1** hasta **Usuario 10**. El activo se ve relleno.
- Búsqueda local **Buscar por título**.
- Contador: 100 publicaciones en Todos y 10 por usuario, antes de aplicar búsqueda.
- Cada tarjeta incluye **Usuario N**, título en negrita y resumen del contenido de máximo dos líneas.
- Botón flotante **+** para crear.
- Conserva la secuencia funcional encabezado, filtros/búsqueda y lista. Rediseña cómo se presentan esos bloques.

### Detalle
- Acción **Atrás** que regresa a la lista.
- Título y contenido completos.
- Acción **Eliminar** con confirmación y estados de envío/error/éxito.
- Debajo de la publicación, **Comentarios (N)**; normalmente son cinco.
- Cada comentario muestra el correo en azul y su contenido.
- La pantalla se desplaza; no recortes artificialmente los textos para hacer caber todo en una sola vista.

### Crear
- Acción **Atrás**.
- Campos **Título** y **Contenido**.
- Botón **Publicar**, deshabilitado si algún campo está vacío o solo tiene espacios y durante el envío.
- Progreso al enviar.
- Éxito: **Publicado con id 101**, con los campos limpios.
- Error: conserva lo escrito y permite reintentar.
- Contempla la apertura del teclado.

### Estados
- Carga: indicador circular y **Cargando publicaciones…**
- Error: recuadro de error, **Sin conexión a internet. Revisa tu red e inténtalo otra vez.** y **Reintentar**.
- Vacío: **Sin publicaciones**.
- Confirmación de eliminación: **Cancelar** y **Eliminar**, explicando que el servicio simula el cambio.
- Éxito al eliminar: **Eliminado (simulación). El servidor no guarda este cambio.**

## Datos disponibles

Una publicación tiene userId, id, title y body. Un comentario tiene postId, id, name, email y body.

Para los ejemplos visuales:
- Usuario: **Usuario 1**.
- Título real: **sunt aut facere repellat provident occaecati excepturi optio reprehenderit**.
- Contenido: **quia et suscipit / suscipit recusandae consequuntur...**
- Correo: **Eliseo@gardner.biz**.

El contenido de la API es lorem ipsum. Puedes usarlo para mostrar cómo se comportan títulos y párrafos extensos. Los avatares gráficos, si los propones, deben derivarse de Usuario N; no inventes nombres personales ni fotos de perfil.

No agregues funciones inexistentes: likes, chats, categorías, autenticación, adjuntos o secciones falsas de navegación. La navegación existente es lista, detalle y crear.

JSONPlaceholder no persiste POST ni DELETE; comunica ese límite con brevedad. La app tampoco tiene caché persistente: al iniciar sin internet muestra error. El interceptor de red es interno y no se muestra en la interfaz.

## Revisión antes de entregar

- ¿Se percibe una identidad completamente nueva respecto de las capturas?
- ¿Las tres direcciones tienen composiciones y sistemas visuales distintos?
- ¿El detalle y el formulario también fueron rediseñados por completo?
- ¿Se conservaron todos los elementos funcionales?
- ¿Los textos son legibles, los controles tienen al menos 48 dp y se respetan barras del sistema y teclado?
- ¿Estoy entregando imágenes PNG/JPEG, sin código?

Entrega las imágenes para que yo pueda elegir una dirección. La aplicación en Kotlin se hará después.
