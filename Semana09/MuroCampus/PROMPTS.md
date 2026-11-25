# Registro de asistencia de IA

IA utilizada: **Codex**. Según la autorización del estudiante, se permite IA en ambas fases. No se utilizó Gemini ni Claude en esta etapa.

Las instrucciones técnicas de la tabla son reformulaciones de trabajo del encargo del estudiante y del contrato del laboratorio; no representan conversaciones adicionales con otras herramientas ni citas textuales del estudiante.

| Prompt / instrucción de trabajo | Respuesta o solución de Codex | Qué se adaptó o descartó | Por qué |
| --- | --- | --- | --- |
| Construir la base desde cero y respetar MVVM + Repository. | Se crearon los componentes, modelos, API, cliente, Repository, estados y pantallas por pasos. | Se conservó el paquete com.eneque.murocampus y el repositorio del curso. | No había proyecto base del docente y el proyecto ya estaba conectado a GitHub. |
| Mantener la traducción de errores fuera de viewmodel. | ErroresRed.kt interpreta IOException, HttpException y SerializationException en data. | Se adaptó la ubicación sugerida por el ejemplo de la guía. | Evitar que el ViewModel importe excepciones de Retrofit. |
| Agregar búsqueda por título sobre publicaciones ya descargadas. | StateFlow de búsqueda combinado con el estado de la lista, sin distinguir mayúsculas y con trim. | Se conserva la lista original y se deriva listaFiltrada; no se llama al Repository por cada carácter. | Evita tráfico innecesario y permite borrar la consulta para recuperar los datos cargados. |
| Agregar DELETE con Response<Unit> sin romper MVVM. | El Repository comprueba isSuccessful y convierte los códigos fallidos en Result.failure; el ViewModel publica el estado. | Se agregó confirmación, bloqueo de envíos repetidos y mensaje explícito de simulación. No se elimina permanentemente de la lista local. | JSONPlaceholder no persiste DELETE; ocultar datos como si el servidor los hubiese borrado sería engañoso. Se probaron respuestas 200 y 500, fallos de red y cancelación. |
| Agregar Accept: application/json a todas las peticiones mediante un interceptor. | AcceptJsonInterceptor se instala antes del interceptor de registro de OkHttp. | Se usa header en vez de addHeader y una prueba termina la cadena en memoria. Se mantiene BODY solo en debug. | Evita duplicar encabezados y permite comprobar el comportamiento sin una dependencia nueva ni acceso a internet. Logcat confirmó Accept en GET y DELETE reales. |

## Criterios de revisión

Se relanza CancellationException; no hay catch vacío, GlobalScope, HTTP sin cifrar ni llamadas de API desde Composables. La UI emite eventos y observa estados. Las pruebas de búsqueda comprueban que la consulta no vuelva a llamar a la red.

El rediseño con Claude está pendiente hasta completar las mejoras funcionales del laboratorio.
