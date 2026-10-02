# Prompts utilizados - Fase 2

## Prompt 1 - Reorganización de la estructura

### Prompt
Estoy trabajando en un proyecto Android con Kotlin y Jetpack Compose.

Quiero reorganizar la estructura de mi proyecto sin cambiar su comportamiento actual.

Actualmente gran parte del código está concentrado en MainActivity.kt.

Quiero separar el código en estos archivos:

- TarjetaProducto.kt:
  debe contener la función TarjetaProducto, el botón de tres puntos y su DropdownMenu con las opciones Favoritos, Compartir y Reportar.

- AppDrawer.kt:
  debe contener el contenido del NavigationDrawer usando ModalDrawerSheet.
  Actualmente el Drawer tiene:
    - Inicio
    - Mis pedidos
    - Favoritos
    - Perfil
      También tiene un encabezado con:
    - Iniciales: OE
    - Nombre: Oscar Eneque
    - Texto: Estudiante TECSUP
      El ítem activo se resalta visualmente.

- AppNavegacion.kt:
  debe contener el ModalNavigationDrawer, el estado de la sección actual y la lógica principal para cambiar entre Inicio, Mis pedidos, Favoritos y Perfil.
  También debe envolver el Scaffold principal.

Actualmente la aplicación ya funciona correctamente y tiene:
- Una pantalla principal con productos.
- Registro de nombre, precio y cantidad.
- Lista de productos.
- Cálculo de subtotal, IGV y total.
- Eliminación de productos.
- TarjetaProducto con botón de tres puntos.
- DropdownMenu con Favoritos, Compartir y Reportar.
- NavigationDrawer con las 4 secciones indicadas.
- Navegación entre las secciones.
- Ítem activo resaltado.
- Encabezado personalizado del usuario.

Requisitos importantes:
- Revisa primero el código actual del proyecto.
- Mantén todas las funcionalidades existentes.
- No cambies innecesariamente nombres de funciones o variables.
- No cambies la lógica actual.
- No agregues arquitectura avanzada.
- No uses ViewModel.
- No uses Room.
- No uses Firebase.
- No uses base de datos.
- Mantén una solución sencilla y entendible para un estudiante que está aprendiendo Jetpack Compose.
- Crea únicamente los archivos necesarios para separar las responsabilidades.
- Deja MainActivity.kt lo más limpio posible.
- Evita duplicar código.
- Corrige los imports necesarios después de mover las funciones.
- Asegúrate de que el proyecto siga compilando.

Por ahora SOLO reorganiza la estructura del código.

NO implementes todavía:
- contador de favoritos
- badge de favoritos
- nuevas funcionalidades
- cambios visuales adicionales

Al terminar, indícame:
1. Qué archivos creaste.
2. Qué funciones moviste a cada archivo.
3. Qué quedó finalmente en MainActivity.kt.
4. Si tuviste que cambiar algún import o parámetro.


## Prompt 2 - Estado compartido de favoritos

### Prompt
Estoy trabajando en el mismo proyecto Android con Kotlin y Jetpack Compose que ya reorganizaste.

La estructura actual quedó separada así:
- TarjetaProducto.kt
- AppDrawer.kt
- AppNavegacion.kt
- MainActivity.kt

Ahora quiero implementar SOLO la lógica compartida de favoritos.

Objetivo:
Cuando el usuario pulse la opción "Favoritos" dentro del DropdownMenu de una tarjeta de producto, ese producto debe quedar registrado como favorito y la aplicación debe poder conocer cuántos productos favoritos existen desde un nivel superior.

Por ahora NO agregues todavía ningún badge ni contador visual en el NavigationDrawer.

Requisitos:
- Usa estado de Jetpack Compose.
- Mantén una solución sencilla y entendible para estudiante.
- No uses ViewModel.
- No uses Room.
- No uses Firebase.
- No uses base de datos.
- No cambies innecesariamente la estructura actual.
- No rompas ninguna funcionalidad existente.
- El mismo producto no debe contarse varias veces si el usuario pulsa Favoritos repetidamente.
- La lógica de favoritos debe quedar accesible desde AppNavegacion.kt para que en el siguiente paso pueda enviarse el total al Drawer.
- TarjetaProducto.kt debe recibir lo necesario por parámetros o callbacks, sin manejar por sí sola el estado global.
- Mantén funcionando el botón eliminar y las opciones Compartir y Reportar.
- Revisa primero el código actual antes de modificarlo.
- Asegúrate de que el proyecto siga compilando.

Por ahora SOLO implementa:
1. El estado compartido de favoritos.
2. La acción para agregar un producto a favoritos desde TarjetaProducto.
3. La prevención de duplicados.
4. La posibilidad de conocer el total de favoritos desde AppNavegacion.kt.

NO implementes todavía:
- badge visual
- contador mostrado en el Drawer
- cambios de diseño adicionales
- nuevas pantallas

Al terminar, indícame:
1. Qué archivos modificaste.
2. Qué parámetros o callbacks agregaste.
3. Cómo evitaste duplicados.
4. Dónde quedó almacenado el estado compartido.


## Prompt 3 - Badge contador de favoritos

Estoy trabajando en el mismo proyecto Android con Kotlin y Jetpack Compose.

Actualmente ya tengo implementado:
- TarjetaProducto.kt con DropdownMenu.
- AppDrawer.kt con NavigationDrawer.
- AppNavegacion.kt con el estado compartido.
- Una lista de favoritos declarada en AppNavegacion.kt con:
  val favoritos = remember { mutableStateListOf<Producto>() }

Cuando el usuario pulsa "Favoritos" desde el DropdownMenu de un producto:
- el producto se agrega a la lista de favoritos;
- no se permiten duplicados;
- favoritos.size contiene la cantidad actual de productos favoritos.

Ahora quiero implementar SOLO el badge visual del contador en el ítem "Favoritos" del NavigationDrawer.

Objetivo:
Mostrar un contador al lado derecho del ítem "Favoritos" dentro del Drawer.

Ejemplo esperado:
Favoritos     2

Requisitos:
- El valor mostrado debe ser favoritos.size.
- El contador debe actualizarse automáticamente cuando se agrega un nuevo producto a favoritos.
- Si el mismo producto se intenta agregar varias veces, el contador no debe aumentar.
- Mantén el diseño actual del Drawer.
- Mantén el resaltado de la sección activa.
- No cambies las demás opciones del Drawer.
- No uses ViewModel.
- No uses Room.
- No uses Firebase.
- No uses base de datos.
- Mantén una solución sencilla con Jetpack Compose.
- No rompas ninguna funcionalidad existente.

Quiero que:
1. AppNavegacion.kt envíe la cantidad de favoritos a AppDrawer.
2. AppDrawer.kt reciba un parámetro como cantidadFavoritos: Int.
3. El NavigationDrawerItem de "Favoritos" muestre el contador usando el trailingContent correspondiente.
4. El contador se vea como un badge pequeño y claro.
5. Si la cantidad es 0, puedes mostrar 0 o decidir ocultar el badge, pero indícame cuál opción implementaste.
6. No agregues todavía cambios adicionales a la pantalla Favoritos.

Al terminar, indícame:
1. Qué archivos modificaste.
2. Qué parámetro nuevo agregaste.
3. Cómo se actualiza automáticamente el contador.
4. Qué componente de Material 3 usaste para mostrar el badge.



## Prompt 4 - Mantener productos al navegar entre secciones

### Prompt
Estoy trabajando en el mismo proyecto Android con Kotlin y Jetpack Compose.

Actualmente tengo:
- AppNavegacion.kt
- AppDrawer.kt
- TarjetaProducto.kt
- MainActivity.kt
- Estado compartido de favoritos en AppNavegacion.kt.
- Badge contador de favoritos funcionando correctamente en el NavigationDrawer.

Encontré un problema de estado:

En PantallaCarrito actualmente la lista de productos se crea con remember/mutableStateListOf dentro de la propia pantalla.

Por eso ocurre lo siguiente:
1. Agrego productos en Inicio.
2. Navego desde el Drawer a Favoritos, Mis pedidos o Perfil.
3. Regreso a Inicio.
4. Los productos que había agregado desaparecen.

Quiero corregir SOLO este problema.

Objetivo:
La lista de productos debe conservarse mientras la aplicación esté abierta aunque el usuario navegue entre las secciones del NavigationDrawer.

Requisitos:
- Mueve el estado de la lista de productos a AppNavegacion.kt, igual que se hizo con la lista de favoritos.
- PantallaCarrito no debe crear nuevamente su propia lista de productos.
- Pasa la lista de productos y los callbacks necesarios por parámetros.
- Mantén funcionando:
    - agregar productos
    - eliminar productos
    - subtotal
    - IGV
    - total
    - DropdownMenu
    - favoritos
    - prevención de favoritos duplicados
    - badge contador del Drawer
- No uses ViewModel.
- No uses Room.
- No uses Firebase.
- No uses base de datos.
- Mantén una solución sencilla con estado de Jetpack Compose.
- No hagas cambios visuales todavía.
- No agregues nuevas funcionalidades.

Al terminar indícame:
1. Qué archivos modificaste.
2. Dónde quedó almacenada ahora la lista de productos.
3. Qué parámetros nuevos recibe PantallaCarrito.
4. Por qué ahora los productos no desaparecen al cambiar de sección.

