# Semana 05 - Actividades e Intents

## Laboratorio: Navegación con Jetpack Compose

En este laboratorio se trabajó la navegación entre diferentes pantallas utilizando
Navigation Compose, NavController y NavHost.

## Prompt final utilizado

```text
Estoy trabajando sobre una aplicación Android EXISTENTE desarrollada con Kotlin, Jetpack Compose, Material 3 y Navigation Compose.

La aplicación actual ya funciona correctamente y contiene:

- MainActivity.kt
- navigation/AppNavigation.kt
- navigation/Screen.kt
- screens/HomeScreen.kt
- screens/ListScreen.kt
- screens/DetailScreen.kt
- screens/ProfileScreen.kt

Actualmente:
- HomeScreen muestra "Pantalla Tecsup" y botones para ir a la lista y al perfil.
- ListScreen muestra una lista de elementos.
- DetailScreen recibe un itemId de tipo Int mediante Navigation Compose.
- ProfileScreen muestra información básica del usuario.

NO quiero crear un proyecto nuevo desde cero.
Trabaja directamente sobre este proyecto existente.

==================================================
OBJETIVO GENERAL
==================================================

Transforma la aplicación actual en un Portal Académico moderno para estudiantes.

Mantén:
- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- NavController
- NavHost
- Screen.kt
- AppNavigation.kt
- navegación mediante rutas
- paso de argumentos mediante navegación

La aplicación final debe tener 5 pantallas:

1. Login
2. Bienvenida / Home
3. Directorio de Alumnos
4. Expediente Académico
5. Configuración de Perfil

No implementes backend ni autenticación real.

==================================================
TEMA Y COLORES
==================================================

La aplicación debe utilizar SIEMPRE una apariencia clara.

No dependas de isSystemInDarkTheme() para cambiar el diseño.
No permitas que el modo oscuro del teléfono cambie los colores definidos.
No uses Dynamic Color si modifica la paleta solicitada.

Usa un lightColorScheme fijo.

Paleta principal aproximada:

- Morado principal: #673AB7
- Morado oscuro: #3F1D84
- Lila medio: #8E79C6
- Lila claro: #C9BDE8
- Lila muy claro: #E3D5F8
- Fondo casi blanco: #FAFAFC
- Blanco: #FFFFFF
- Texto principal oscuro: #211D24
- Texto secundario gris: #6F6873
- Rojo para cerrar sesión: aproximadamente #D32F2F
- Fondo rojo muy claro: aproximadamente #FDE8E8

IMPORTANTE:
Cuando utilices degradados, asegúrate de que sean REALMENTE visibles.

Si utilizas Scaffold, Surface u otro contenedor, evita que un containerColor sólido tape el Brush.verticalGradient.

Cuando sea necesario utiliza:

Box(
modifier = Modifier
.fillMaxSize()
.background(
Brush.verticalGradient(...)
)
)

y coloca el contenido dentro.

Si existe Scaffold sobre el degradado, usa containerColor = Color.Transparent cuando corresponda.

==================================================
DATOS DEL USUARIO PRINCIPAL
==================================================

Utiliza estos datos:

Nombre corto:
Oscar Eneque

Nombre completo:
Oscar Eneque Lluen

Correo:
oscar.eneque.l@tecsup.edu.pe

Teléfono:
+51 987 654 321

Carrera:
Diseño y Desarrollo de Software

Ciclo:
IV Ciclo

ID estudiantil:
2024-0001

Facultad:
Tecnología Digital

Biografía:
Estudiante de Tecsup enfocado en el desarrollo de aplicaciones móviles con Jetpack Compose y Kotlin.

==================================================
1. LOGINSCREEN
   ==================================================

Crea una nueva pantalla LoginScreen.

Debe ser la primera pantalla de la aplicación.

Diseño:

- Fondo de pantalla completo con degradado vertical SUAVE y visible:
    - parte superior: #E3D5F8
    - zona central: #FAFAFC
    - parte inferior: #E3D5F8

- En el centro de la pantalla coloca una tarjeta blanca con:
    - esquinas redondeadas
    - sombra suave
    - padding interno uniforme

NO coloques un logo grande o un ícono de graduación sobre el título.

Dentro de la tarjeta colocar:

Título:
"Portal Académico"

- color morado principal
- tamaño destacado
- FontWeight.Bold
- centrado

Subtítulo:
"Accede a tu cuenta"

- gris oscuro
- centrado

Campo:
"Correo Institucional"

- ícono de correo en gris oscuro o negro
- valor inicial:
  oscar.eneque.l@tecsup.edu.pe

Campo:
"Contraseña"

- ícono de candado en gris oscuro o negro
- contraseña oculta
- ícono para mostrar/ocultar contraseña

Después colocar:

[ INICIAR SESIÓN ]

Botón:
- ancho completo
- fondo morado
- texto blanco
- esquinas redondeadas

DEBAJO del botón colocar:

"¿Olvidaste tu contraseña?"

No coloques este texto encima del botón.

Al presionar INICIAR SESIÓN:
navegar a HomeScreen.

No implementar autenticación real.

==================================================
2. HOMESCREEN / BIENVENIDA
   ==================================================

Transforma HomeScreen en la pantalla principal del Portal Académico.

No mostrar fotografía del usuario en esta pantalla.

FONDO:

Utiliza un degradado vertical claramente visible:

- parte superior: #8E79C6
- zona central: #C9BDE8
- parte inferior: #FFFFFF

El morado debe notarse claramente en la parte superior y debe aclararse progresivamente hacia abajo.

CONTENIDO:

El bloque principal debe quedar aproximadamente en la zona central de la pantalla o ligeramente por encima del centro.

Todo centrado horizontalmente.

Mostrar:

Bienvenido,
Oscar Eneque

IMPORTANTE:
"Bienvenido," y "Oscar Eneque" deben tener:
- Color.White
- mismo tamaño o prácticamente el mismo
- FontWeight.Bold
- TextAlign.Center
- gran protagonismo visual

Debajo dejar espacio y mostrar:

"¿Qué deseas gestionar hoy?"

Después colocar dos tarjetas claras/blancas.

TARJETA 1:

Ícono:
grupo de personas

Título:
"Directorio de Alumnos"

Subtítulo:
"Ver y gestionar estudiantes"

Al pulsar:
navegar a ListScreen.

TARJETA 2:

Ícono:
UNA SOLA PERSONA

No utilizar ícono de credencial, portafolio o cartera.

Título:
"Mi Perfil Académico"

Subtítulo:
"Datos personales y progreso"

Al pulsar:
navegar a ProfileScreen.

Las tarjetas deben:
- ser blancas
- tener esquinas redondeadas
- sombra suave
- íconos morados sobre círculo lila claro
- flecha hacia la derecha
- tamaño compacto
- no usar fondo morado oscuro

CERRAR SESIÓN:

Mostrar:

"Cerrar Sesión Segura"

- ícono rojo
- texto rojo
- centrado horizontalmente
- ubicado cerca de la zona inferior

Debe existir bastante espacio entre las tarjetas y esta opción.

NO debe quedar cortado por la barra de navegación del dispositivo.

Utiliza navigationBarsPadding() o safeDrawingPadding().

Deja aproximadamente entre 24.dp y 40.dp de separación respecto a la zona inferior segura.

Al pulsar:
volver al Login.

==================================================
3. DIRECTORIO DE ALUMNOS
   ==================================================

Transforma ListScreen en "Directorio de Alumnos".

PARTE SUPERIOR:

TopAppBar con:
- flecha de volver
- título "Directorio de Alumnos"
- título en morado oscuro

El fondo superior NO debe ser blanco puro.

Usa:
containerColor = Color(0xFFE2D5F8)

Debe verse una zona superior lila suave y luego el contenido casi blanco.

LISTADO:

Crear aproximadamente 6 estudiantes de ejemplo.

Cada estudiante debe mostrarse en una tarjeta:
- blanca
- compacta
- esquinas redondeadas
- sombra suave

Cada tarjeta debe contener:
- avatar/fotografía circular a la izquierda
- nombre
- carrera
- flecha morada hacia la derecha

Estudiantes:

1.
Oscar Eneque Lluen
Diseño y Desarrollo de Software

2.
Ana Lucía Gómez
Diseño y Desarrollo de Software

3.
Carlos Mendoza Ríos
Redes y Comunicaciones

4.
María Fernanda Torres
Big Data y Analítica

5.
Luis Alberto Paredes
Diseño y Desarrollo de Software

6.
Valeria Vargas Castro
Ciberseguridad

IMÁGENES:

Si existen estos recursos dentro de res/drawable:

- oscar_eneque
- ana_gomez
- carlos_mendoza
- maria_torres
- luis_paredes
- valeria_vargas

úsalos para cada estudiante.

Las fotografías deben mostrarse:
- circulares
- usando CircleShape
- con ContentScale.Crop

Si alguno de esos recursos no existe, utiliza temporalmente iniciales dentro de un avatar circular, sin descargar imágenes desde Internet.

Al pulsar un estudiante:
navegar a DetailScreen pasando su ID mediante Navigation Compose.

Mantén el concepto existente de itemId: Int.

==================================================
4. EXPEDIENTE ACADÉMICO
   ==================================================

Transforma DetailScreen en "Expediente Académico".

Mantén la recepción de itemId: Int desde Navigation Compose.

PARTE SUPERIOR:

TopAppBar clara:
- flecha de volver
- título "Expediente Académico"
- título morado

Debajo:
una cabecera morada de altura moderada.

La fotografía circular del alumno debe quedar visualmente entre la cabecera morada y el contenido inferior.

Para Oscar utiliza:
R.drawable.oscar_eneque
si existe.

Debajo de la fotografía mostrar:

Oscar Eneque Lluen

Diseño y Desarrollo de Software

INFORMACIÓN:

Utiliza UNA SOLA tarjeta blanca.

Dentro colocar:

ID ESTUDIANTIL
2024-0001

CORREO ELECTRÓNICO
oscar.eneque.l@tecsup.edu.pe

FACULTAD
Tecnología Digital

IMPORTANTE:
El texto debe ser solamente "FACULTAD".
No usar "FACULTAD / ÁREA".

Después agregar un divisor horizontal.

Dentro de LA MISMA tarjeta colocar:

Biografía

Estudiante de Tecsup enfocado en el desarrollo de aplicaciones móviles con Jetpack Compose y Kotlin.

NO coloques la biografía en otra tarjeta independiente.

La tarjeta debe:
- ser blanca
- compacta
- esquinas redondeadas
- sombra suave
- íconos morados sobre círculos lila claro

==================================================
5. PROFILESCREEN
   ==================================================

Transforma ProfileScreen en:

"Configuración de Perfil"

PARTE SUPERIOR:

TopAppBar clara:
- flecha de volver
- título "Configuración de Perfil"
- título morado

Debajo crear una cabecera morada.

IMPORTANTE:
La fotografía Y el nombre deben quedar DENTRO del rectángulo/cabecera morada.

La fotografía NO debe sobresalir hacia el fondo blanco.

Mostrar:

fotografía circular

Oscar Eneque Lluen

NO mostrar la carrera debajo del nombre dentro de la cabecera.

Después, sobre fondo claro, mostrar:

INFORMACIÓN PERSONAL

Fila 1:
Nombre Completo
Oscar Eneque Lluen

Fila 2:
Correo
oscar.eneque.l@tecsup.edu.pe

Fila 3:
Teléfono
+51 987 654 321

Después:

ACADÉMICO

Fila 1:
Carrera
Diseño y Desarrollo de Software

Fila 2:
Ciclo Actual
IV Ciclo

NO agregar:
Institución: Tecsup

IMPORTANTE:
No envolver estas dos secciones en tarjetas blancas grandes.

Usa filas sencillas directamente sobre el fondo claro:

- ícono pequeño dentro de círculo lila claro
- etiqueta gris
- valor oscuro
- divisores suaves entre filas

CERRAR SESIÓN:

En la parte inferior colocar un botón ancho:

"Cerrar Sesión"

- fondo rojo/rosado muy claro
- texto rojo
- ícono rojo
- esquinas redondeadas

El botón debe estar cerca de la parte inferior de la pantalla, no inmediatamente después de la última fila académica.

Utiliza Spacer(weight = 1f) si es necesario.

Debe quedar completamente visible y respetar la barra de navegación de Android.

Al pulsar:
navegar al Login.

==================================================
MODELO DE DATOS
==================================================

Crea un modelo Student en un paquete data.

Debe permitir almacenar al menos:

- id
- nombre
- carrera
- correo
- facultad
- biografía
- recurso de imagen cuando exista

Crea estudiantes de ejemplo para el directorio.

==================================================
NAVEGACIÓN
==================================================

El flujo final debe ser:

Login
→ Home

Home
→ Directorio de Alumnos

Directorio
→ Expediente del alumno seleccionado

Home
→ Mi Perfil Académico

Cerrar sesión desde Home
→ Login

Cerrar sesión desde Profile
→ Login

Mantén:
- NavController
- NavHost
- Screen.kt
- AppNavigation.kt
- itemId: Int para DetailScreen
- popBackStack() cuando corresponda

Agrega la ruta Login dentro de Screen.kt.

startDestination debe ser Login.

==================================================
ORGANIZACIÓN DEL CÓDIGO
==================================================

MUY IMPORTANTE:

NO coloques toda la implementación dentro de MainActivity.kt.

MainActivity.kt debe ser únicamente el punto de entrada.

Debe hacer algo equivalente a:

setContent {
Lab05NavegacionTheme {
AppNavigation()
}
}

Mantén componentes y pantallas separados.

Organiza aproximadamente así:

com.eneque.lab05navegacion/

MainActivity.kt

data/
Student.kt

components/
AvatarCircle.kt
InfoRow.kt
MenuOptionCard.kt
PurpleHeader.kt
StudentCard.kt

navigation/
AppNavigation.kt
Screen.kt

screens/
LoginScreen.kt
HomeScreen.kt
ListScreen.kt
DetailScreen.kt
ProfileScreen.kt

ui/theme/
Color.kt
Theme.kt
Type.kt

Crea componentes reutilizables cuando corresponda.

NO concentres todas las pantallas o todos los componentes en un solo archivo.

==================================================
RESTRICCIONES FINALES
==================================================

- Trabaja sobre el proyecto actual.
- No crees un proyecto nuevo.
- No uses XML.
- No elimines Navigation Compose.
- No agregues backend.
- No agregues autenticación real.
- No descargues imágenes desde Internet.
- No agregues dependencias innecesarias.
- No concentres el código en MainActivity.kt.
- Mantén código sencillo y entendible.
- Asegúrate de que el proyecto compile correctamente.
- Asegúrate de que las pantallas respeten las barras del sistema usando padding seguro cuando sea necesario.

Realiza directamente todas las modificaciones necesarias en los archivos del proyecto actual.

```