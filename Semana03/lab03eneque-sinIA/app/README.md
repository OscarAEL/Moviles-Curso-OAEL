## Jectpack Compose
Estudiante: Oscar Eneque


Descripcion: App que permite registrar un nuevo producto por su nombre, precio en S/ y cantidad. Luego tiene su botn de "Agregar Producto" y saldra un resumen de lo que relleno el usuario y con un mensaje final "Se agrego el producto corerctamente". 


Capturas:
![img.png](img.png)
![img_1.png](img_1.png)

¿qué pasaría si declaras las variables de los campos SIN remember?


Sin remember, mutableStateOf crea una variable de estado nueva en cada recomposición, por lo que el valor se reinicia constantemente y no se puede escribir en el campo (se ve como si estuviera vacío todo el tiempo). Con remember, Compose conserva ese estado entre recomposiciones mientras la actividad esté viva, permitiendo que el texto ingresado se logre observar.