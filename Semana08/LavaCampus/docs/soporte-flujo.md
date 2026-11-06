# Soporte y Otros Servicios

## Cómo demostrarlo

En Login selecciona **Soporte** y pulsa **Registrar cuenta de soporte**. Ingresa nombre, apellido, código de seis dígitos, correo y clave con mayúscula, minúscula y número. Al crear la cuenta vuelve al login con Soporte seleccionado. Para ingresar introduce correo, clave y confirmación de clave. Se abre Centro de soporte, con los datos de la cuenta y los servicios disponibles.

Otros Servicios aparece en la lista actual de servicios. La tarifa inicial de práctica es S/ 5.00 por unidad y la entrega 24 horas; Administración puede editarlas.

## Dónde comienza y con quién se conecta

**Registro:** RegistroScreen recoge los campos y llama a SesionViewModel.registrarSoporte. El ViewModel llama a LavaRepository.registrarSoporte. LavaRepositoryRoom valida e inserta mediante UsuarioDao, dentro de una transacción Room. Se guarda el rol SOPORTE y el nombre completo en la columna nombre existente. El ViewModel publica registroCompletado y correoRegistrado; la vista observa esos estados y vuelve al login. No inicia sesión automáticamente.

**Ingreso:** LoginScreen envía correo, clave, rol y confirmación a SesionViewModel.login. Para Soporte se comprueba que clave y confirmación coincidan. El repositorio consulta UsuarioDao.login. Si la cuenta corresponde al rol, el ViewModel guarda la sesión en DataStore y emite usuarioActual. NavGraph observa usuarioActual y navega al destino soporte.

**Pantalla propia:** SoporteViewModel recibe LavaRepository, observa observarServiciosActivos y lo convierte en StateFlow. SoporteScreen recibe el usuario, observa la lista de servicios y el error, y muestra los datos. La vista no consulta Room ni usa DAOs. El botón Cerrar sesión llama a SesionViewModel.cerrarSesion.

**Otros Servicios:** DatosIniciales comprueba dentro de la transacción si ya existe antes de insertarlo. ServicioDao emite los cambios como Flow; Repository y ViewModels los entregan a Servicios, Tarifas, Nuevo pedido y Soporte. No se vuelven a insertar las cuentas ni los pedidos existentes.

La vista recibe estado para pintar la interfaz; las acciones del usuario viajan al ViewModel y los datos persistentes se consultan o guardan a través del Repository y los DAOs.
