## Jectpack Compose
Estudiante: Oscar Eneque


Descripcion: App que permite registrar un nuevo producto por su nombre, precio en S/ y cantidad. Luego tiene su botn de "Agregar Producto" y saldra un resumen de lo que relleno el usuario y con un mensaje final "Se agrego el producto corerctamente". 


Capturas:
![img.png](img.png)
![img_1.png](img_1.png)

¿qué pasaría si declaras las variables de los campos SIN remember?


Sin remember, mutableStateOf crea una variable de estado nueva en cada recomposición, por lo que el valor se reinicia constantemente y no se puede escribir en el campo (se ve como si estuviera vacío todo el tiempo). Con remember, Compose conserva ese estado entre recomposiciones mientras la actividad esté viva, permitiendo que el texto ingresado se logre observar.



## Mejora con IA


| Prompt que usé | Qué generó Gemini                                                                                    | Qué acepté o corregí (y por qué) |
|---|------------------------------------------------------------------------------------------------------|---|
| *"En mi función @Composable PantallaRegistro de Jetpack Compose, necesito agregar dos cosas: <br/>1. Validación: cuando se presiona el botón 'AGREGAR PRODUCTO', si nombre, precio o cantidad están vacíos, no debe mostrarse la Card de resumen. En su lugar, debe aparecer un Text en color rojo (por ejemplo Color.Red) con un mensaje como 'Completa todos los campos'. Si todos los campos tienen datos, el comportamiento debe seguir siendo el mismo que ya tengo (mostrar la Card con nombre, precio, cantidad e importe). <br/>2. Botón Limpiar: agrega un segundo botón debajo de 'AGREGAR PRODUCTO' que, al presionarlo, vacíe nombre, precio y cantidad, y oculte tanto la Card de resumen como el mensaje de error. <br/>NO modifiques la estructura del Column principal, los OutlinedTextField existentes, ni el cálculo del importe. Solo agrega la lógica de validación y el nuevo botón."* | Validación de campos vacíos con `mostrarError`, botón LIMPIAR con `OutlinedButton`, mensajes en rojo | Acepté toda la lógica de validación y limpieza tal cual, porque funcionaba correctamente. Corregí el `Spacer` de 8.dp a 16.dp para mantener consistencia con el resto del diseño. También cambié el texto del botón de "LIMPIAR" a "LIMPIAR DATOS INGRESADOS" para mayor claridad. |