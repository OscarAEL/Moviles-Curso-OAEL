## VII. Preguntas de reflexión

### 1. ¿Por qué los modelos, Rutas.kt y AppNavigation.kt se entregaron completos y las pantallas no? ¿Qué tienen en común los archivos que sí se dejaron como esqueleto?

Porque los modelos, las rutas y AppNavigation definen la estructura principal y el flujo de la aplicación. En cambio, las pantallas eran la parte que nosotros debíamos desarrollar para aplicar lo aprendido en Compose. Los archivos que se dejaron como esqueleto tenían en común que requerían implementar principalmente la interfaz y la lógica de interacción del usuario.

### 2. ¿Por qué el Repositorio es un object y no una clase normal? ¿Qué pasaría con las citas si cada pantalla creara su propia lista?

El Repositorio es un object para que exista una sola instancia compartida durante la ejecución de la aplicación. De esta manera, todas las pantallas trabajan con los mismos usuarios, médicos y citas. Si cada pantalla tuviera su propia lista, los datos no serían compartidos y una cita creada en una pantalla podría no aparecer en otra.

### 3. ¿Cómo lograste que la búsqueda de especialidades y los horarios disponibles se actualicen solos, sin que tú “actualices” nada a mano?

Se utilizó estado de Compose con variables como `mutableStateOf`. Cuando cambia el texto de búsqueda o la fecha seleccionada, Compose vuelve a ejecutar la parte correspondiente de la interfaz y recalcula automáticamente la lista filtrada o los horarios disponibles.

### 4. ¿Qué diferencia notaste entre navigate() normal (Especialidades → Médicos) y el que usa popUpTo (Confirmar cita → Cita agendada)? ¿Qué pasa al presionar Atrás en cada caso?

Con `navigate()` normal, la pantalla anterior permanece en el historial de navegación, por eso al presionar Atrás se puede regresar a ella. Con `popUpTo` se eliminan determinadas pantallas del historial. En el flujo de confirmación se utilizó para evitar que el usuario vuelva a las pantallas del proceso de agendamiento después de registrar correctamente una cita.

### 5. ¿Qué tuviste que corregir del código que te generó la IA para el calendario dinámico?

Fue necesario revisar que el calendario mantuviera la lógica existente de la aplicación y que no modificara innecesariamente otras clases. También se verificó que utilizara `LocalDate`, mostrara únicamente días hábiles, impidiera retroceder a fechas pasadas, limpiara la hora seleccionada al cambiar de día y siguiera consultando `Repositorio.horariosDisponibles()`.

Durante otras mejoras con IA también aparecieron pequeños problemas con algunos iconos de Material, por lo que fue necesario revisar y corregir referencias para asegurar que el proyecto continuara compilando.

### 6. Compara el NavigationDrawer del Laboratorio 6 con el NavigationBar de esta tarea: ¿en qué caso usarías cada uno en un proyecto propio?

Usaría un `NavigationBar` cuando la aplicación tenga pocas secciones principales que el usuario necesite consultar frecuentemente, como Inicio, Citas, Resultados y Perfil. Usaría un `NavigationDrawer` cuando exista una mayor cantidad de opciones o secciones secundarias, porque permite organizarlas sin ocupar permanentemente espacio en la pantalla.


## VIII. Observaciones

1. Durante el desarrollo fue importante mantener separados los datos, la navegación y las pantallas. Esto facilitó modificar el diseño de las vistas sin afectar la lógica del repositorio ni las rutas de la aplicación.

2. Uno de los principales obstáculos fue adaptar las vistas faltantes y el calendario dinámico sin romper funcionalidades que ya estaban implementadas. También fue necesario revisar algunos cambios generados por IA, especialmente iconos, formatos de fecha y conservación de la lógica existente.


## Conclusiones

1. Trabajar a partir de una estructura previamente definida permitió concentrarse en implementar y comprender cada pantalla en lugar de comenzar completamente desde cero. Además, ayudó a entender mejor cómo se relacionan modelos, repositorio, rutas y navegación dentro de una aplicación Android.

2. La Fase 1 permitió comprender el funcionamiento principal de la aplicación y su navegación, mientras que la Fase 2 permitió utilizar IA como apoyo para mejorar funcionalidades y diseño. Sin embargo, fue necesario revisar y probar cada cambio generado para asegurarse de que cumpliera los requisitos y no afectara el funcionamiento existente.