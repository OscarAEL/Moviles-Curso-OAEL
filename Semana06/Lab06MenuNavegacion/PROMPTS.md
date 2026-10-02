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
