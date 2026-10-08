# PROMPTS - Fase 2 SaludPlusCitas

## Prompt 1 - Calendario dinámico con LocalDate

### Prompt utilizado
Estoy trabajando en una app Android con Kotlin y Jetpack Compose llamada SaludPlusCitas.

Quiero modificar únicamente la pantalla FechaHoraScreen.kt para implementar un calendario dinámico.

Actualmente la pantalla recibe:

medicoId: Int
onContinuarClick: (String, String) -> Unit
onAtrasClick: () -> Unit

Actualmente utiliza una lista fija de fechas String y obtiene los horarios con:

Repositorio.horariosDisponibles(
medicoId = medicoId,
fecha = fechaSeleccionada
)

Debes mantener esa lógica de horarios disponibles y no romper el resto de la navegación.

Requisitos:

1. Utiliza java.time.LocalDate.
2. Muestra los próximos 5 días hábiles a partir de la fecha actual.
3. No mostrar sábados ni domingos.
4. Nunca mostrar días pasados.
5. Agrega una flecha "<" y una flecha ">" para retroceder o avanzar una semana.
6. No permitir retroceder a una semana anterior a la semana actual.
7. Mostrar dinámicamente el nombre del mes y año según la semana visible, por ejemplo:
   Octubre 2026
8. Cuando el usuario cambie de día:
   - actualizar fechaSeleccionada;
   - limpiar horaSeleccionada;
   - recalcular automáticamente los horarios disponibles.
9. Los horarios deben seguir obteniéndose mediante Repositorio.horariosDisponibles(), para que un horario ya reservado para el mismo médico y fecha no vuelva a aparecer.
10. El botón Continuar debe seguir deshabilitado hasta que exista fecha y hora seleccionadas.
11. Mantén el EncabezadoConAtras existente.
12. No cambies Rutas.kt, AppNavigation.kt, Repositorio.kt ni los modelos.
13. Mantén la fecha enviada a la siguiente pantalla en un formato que después pueda convertirse nuevamente a LocalDate.

Haz los cambios únicamente necesarios y explícame brevemente qué modificaste.

### Respuesta resumida
Gemini modificó FechaHoraScreen.kt para reemplazar las fechas fijas por un calendario dinámico usando java.time.LocalDate.

Se implementaron:
- próximos 5 días hábiles;
- exclusión de sábados y domingos;
- navegación por semanas con flechas;
- bloqueo para no retroceder antes de la semana actual;
- mes y año dinámicos;
- reinicio de la hora seleccionada al cambiar de día;
- recálculo de horarios disponibles usando Repositorio.horariosDisponibles().

### Correcciones realizadas
Se probó manualmente el funcionamiento del calendario, navegación por semanas, exclusión de fines de semana y bloqueo de horarios ya reservados.

No fue necesario modificar otros archivos.


## Prompt 2 - Fecha completa en español al confirmar cita

### Prompt utilizado
Estoy trabajando en una app Android con Kotlin y Jetpack Compose llamada SaludPlusCitas.

Ya existe una pantalla llamada ConfirmarCitaScreen.kt que recibe estos parámetros:

medicoId: Int
fecha: String
hora: String
onCitaConfirmada: (Int) -> Unit
onAtrasClick: () -> Unit

La fecha que recibe actualmente viene desde FechaHoraScreen.kt en formato ISO:

YYYY-MM-DD

Por ejemplo:

2026-10-16

Quiero modificar únicamente ConfirmarCitaScreen.kt para mejorar cómo se muestra esa fecha al usuario.

REQUISITOS:

1. No cambies la firma de ConfirmarCitaScreen.
2. No modifiques Rutas.kt.
3. No modifiques AppNavigation.kt.
4. No modifiques Repositorio.kt.
5. No modifiques los modelos.
6. Mantén toda la lógica actual para obtener el médico, usuario, crear la cita y llamar Repositorio.agendarCita().
7. Mantén EncabezadoConAtras y el botón "Confirmar cita".
8. La fecha original debe seguir guardándose en la Cita en formato YYYY-MM-DD. Solo quiero cambiar cómo se muestra visualmente en esta pantalla.
9. Convierte el String recibido en un LocalDate usando java.time.LocalDate.
10. Muestra la fecha en español con este formato:

Martes 16 de setiembre 2026

Es decir:
- nombre completo del día;
- número del día;
- "de";
- nombre del mes en español;
- año.

11. Los nombres de los días y meses deben mostrarse en español.
12. Si el String de fecha no pudiera convertirse correctamente, evita que la app falle y muestra como respaldo el String original recibido.
13. No alteres la navegación ni la lógica de creación de citas.
14. Modifica únicamente ConfirmarCitaScreen.kt.

Al terminar, explícame brevemente:
- qué importaciones agregaste;
- cómo conviertes el String a LocalDate;
- cómo generas la fecha en español;
- qué mecanismo usaste para evitar un error si la fecha fuera inválida.

### Respuesta resumida
Gemini modificó únicamente ConfirmarCitaScreen.kt para convertir visualmente la fecha almacenada en formato ISO YYYY-MM-DD a una fecha larga en español.

Ejemplo:
2026-10-16
se muestra como:
Viernes 16 de octubre 2026.

La fecha sigue guardándose internamente en formato ISO.

### Correcciones realizadas
Se verificó que la fecha se mostrara correctamente en español y que la creación de citas siguiera funcionando sin modificar el formato almacenado.


## Prompt 3 - Fecha en español en Mis Citas y Cita Agendada

### Prompt utilizado
Estoy trabajando en una app Android con Kotlin y Jetpack Compose llamada SaludPlusCitas.

Actualmente las citas guardan la fecha internamente como String en formato ISO:

YYYY-MM-DD

Por ejemplo:

2026-10-06

Ya existe una mejora en ConfirmarCitaScreen.kt que convierte visualmente esa fecha a un formato largo en español, por ejemplo:

Martes 6 de octubre 2026

Quiero aplicar el mismo criterio visual en otras dos pantallas:

1. MisCitasScreen.kt
2. CitaExitosaScreen.kt

REQUISITOS:

1. No cambies el modelo Cita.
2. No cambies cómo se guarda la fecha en Repositorio.
3. No modifiques Rutas.kt.
4. No modifiques AppNavigation.kt.
5. No cambies la firma de MisCitasScreen ni de CitaExitosaScreen.
6. La fecha debe seguir almacenándose internamente como YYYY-MM-DD.
7. Solo cambia la forma en que se muestra al usuario.
8. Convierte el String a LocalDate con java.time.LocalDate.
9. Muéstralo en español con este formato:

Martes 6 de octubre 2026

10. Si la fecha no pudiera convertirse, muestra el String original como respaldo.
11. Mantén intacta toda la navegación y la lógica actual de ambas pantallas.
12. Modifica únicamente:
- MisCitasScreen.kt
- CitaExitosaScreen.kt

Al terminar, explícame brevemente:
- qué archivos modificaste;
- cómo conviertes la fecha;
- cómo evitas que la app falle si el valor fuera inválido.

### Respuesta resumida
Gemini modificó MisCitasScreen.kt y CitaExitosaScreen.kt para mostrar las fechas en formato largo en español.

Se agregó una función privada formatearFecha(fecha: String) usando LocalDate.parse() y DateTimeFormatter.

### Correcciones realizadas
Se verificó manualmente que la fecha aparezca correctamente en ambas pantallas y que la navegación y las reservas continúen funcionando.


## Prompt 4 - Mejorar validaciones del registro

### Prompt enviado
Mejorar únicamente las validaciones de RegistroScreen.kt sin cambiar el diseño, navegación, repositorio, modelos ni otras pantallas.

Validaciones solicitadas:
- Nombre: trim(), mínimo 2 caracteres, solo letras y espacios, aceptar tildes y ñ.
- Teléfono: solo dígitos, máximo 9 caracteres al escribir y exactamente 9 para registrarse.
- Correo: trim(), formato válido usando android.util.Patterns.EMAIL_ADDRESS y mantener validación de correo duplicado.
- Contraseña: mínimo 6 caracteres, al menos una letra y un número.
- No permitir el registro mientras exista algún dato inválido.
- Mantener el flujo de registro exitoso actual.

### Respuesta resumida
Gemini agregó validaciones más completas en RegistroScreen.kt.

- El nombre ahora acepta únicamente letras y espacios, incluyendo tildes y ñ.
- El teléfono se limita a máximo 9 dígitos y debe tener exactamente 9 para registrarse.
- El correo se valida con Patterns.EMAIL_ADDRESS.
- La contraseña requiere mínimo 6 caracteres, al menos una letra y un número.
- Se mantuvo la validación de correo duplicado.
- No se modificó la navegación ni el repositorio.

### Correcciones y verificación
Se probaron casos válidos e inválidos manualmente.

Ejemplos rechazados:
- Nombre: A, 123, Oscar123.
- Teléfono con menos de 9 dígitos.
- Correo con formato incorrecto.
- Contraseña solo con letras o solo números.

El registro con datos correctos continuó funcionando normalmente.


---

## Prompt 5 - Corregir navegación desde CitaExitosa hacia MisCitas

### Prompt enviado
Corregir únicamente el flujo de navegación:

CitaExitosaScreen
→ Ver mis citas
→ MisCitasScreen
→ Atrás
→ HomeScreen

El problema era que al presionar Atrás desde MisCitasScreen se regresaba nuevamente a CitaExitosaScreen.

Se solicitó utilizar popUpTo para eliminar CitaExitosaScreen del back stack, manteniendo intacta la opción "Volver al inicio".

### Respuesta resumida
Gemini modificó AppNavigation.kt.

En onMisCitasClick se utilizó:

popUpTo(Rutas.CITA_EXITOSA) {
inclusive = true
}

De esta manera CitaExitosaScreen se elimina del historial al navegar a MisCitasScreen.

### Correcciones y verificación
Se probaron los dos casos:

1. Cita exitosa → Ver mis citas → Atrás → Inicio.
2. Cita exitosa → Volver al inicio → Inicio.

Ambos flujos funcionaron correctamente.

---

## Prompt 6 - Corregir salto accidental de Especialidades a FechaHora

### Prompt enviado
Investigar un comportamiento intermitente en el flujo:

Especialidades
→ Médicos
→ Fecha y hora

A veces, al seleccionar una especialidad, la aplicación saltaba directamente a FechaHoraScreen sin permitir seleccionar manualmente al médico.

Se solicitó revisar:
- onEspecialidadClick;
- onMedicoClick;
- AppNavigation.kt;
- estados de navegación;
- posibles dobles toques o navegación automática.

### Respuesta resumida
Gemini identificó que un toque rápido o doble toque podía coincidir con la transición hacia MedicosScreen y provocar inmediatamente el clic sobre una tarjeta de médico.

Se agregó una validación con:

Lifecycle.State.RESUMED

en la navegación de MedicosScreen antes de ejecutar onMedicoClick.

### Correcciones y verificación
Inicialmente la protección también se agregó en EspecialidadesScreen.

Esto evitó el salto accidental, pero produjo un efecto secundario: al tocar muy rápido una especialidad, a veces el primer toque no era reconocido.

Se realizó un ajuste posterior para mantener la protección solamente en MedicosScreen.


---

## Prompt 7 - Agregar comentarios de estudio al código

### Prompt enviado
Agregar comentarios de estudio en español a los archivos principales de SaludPlusCitas.

Los comentarios debían ser:
- breves;
- claros;
- sencillos;
- útiles para una sustentación oral;
- sin comentar cada línea;
- sin modificar ninguna parte funcional del proyecto.

Se solicitó priorizar explicaciones sobre:
- Repositorio y colecciones en memoria;
- rutas y navegación;
- NavController, NavHost y popUpTo;
- remember y mutableStateOf;
- validaciones de registro;
- LazyRow, LazyColumn y LazyVerticalGrid;
- filtros y búsquedas;
- calendario dinámico con LocalDate;
- selección de fecha y hora;
- creación y consulta de citas;
- sesión del usuario.

### Respuesta resumida
Gemini agregó comentarios breves en los modelos, Repositorio, navegación, componentes y pantallas principales.

Se dejaron explicadas especialmente:
- la función de object Repositorio;
- las rutas con parámetros;
- el flujo Especialidades → Médicos → FechaHora;
- el uso de popUpTo;
- las validaciones de Registro;
- el calendario dinámico;
- los horarios disponibles;
- la creación y visualización de citas;
- el funcionamiento de las listas Lazy.

### Correcciones y verificación
Gemini confirmó que únicamente agregó comentarios y que no modificó lógica, navegación, diseño, variables ni funciones.

Se revisó el proyecto para comprobar que las funcionalidades existentes continuaran funcionando correctamente.