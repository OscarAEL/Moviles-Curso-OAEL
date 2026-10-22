# Prompts utilizados

## Contexto y herramienta

La Fase 2 se desarrolló con **Codex**, después de agotarse la cuota de Gemini de Android Studio. Se trabajó sobre la Fase 1 publicada en `main`, en la rama **mejora-ia-lavacampus**, para no interferir con una rama `mejora-ia` existente. Se implementaron las tres mejoras de los commits A, B y C de las páginas 29–30 del laboratorio.

Los textos siguientes organizan las instrucciones de trabajo aplicadas durante la sesión: son una síntesis reproducible de los requisitos y decisiones, **no una transcripción literal de mensajes enviados a Gemini**. La implementación, las correcciones y los resultados descritos se verificaron en este repositorio.

## Prompt 1 — Pedidos atrasados

**Texto de trabajo para Codex:**

> Agrega una marca roja en la bandeja para pedidos con fechaEntrega anterior a hoy y estado distinto de ENTREGADO y CANCELADO. Muestra su cantidad en el panel. La consulta debe estar en PedidoDao y declararse primero en LavaRepository; impleméntala en LavaRepositoryRoom y expón StateFlow desde los ViewModels. Las pantallas solo presentan el estado. Mantén Compose y Material 3 sin bibliotecas nuevas.

**Qué generó la IA:** consulta reactiva de identificadores atrasados, contrato e implementación del repositorio, estados de BandejaViewModel y PanelViewModel, borde y texto de advertencia en la tarjeta y contador en el panel. La fecha de hoy se vuelve a observar cada minuto para actualizar el resultado al cambiar el día.

**Qué se corrigió y verificó:** se excluyeron explícitamente los cancelados, se mantuvo la comparación estricta anterior a hoy y se comprobó que cancelar un atrasado actualiza bandeja y contador. En la revisión de capturas se detectaron filtros con fondo blanco y texto claro en modo oscuro; se cambiaron por pares de colores del tema. Esta corrección también la ejecutó Codex; no se atribuye a una intervención manual inexistente.

**¿Respeta MVVM?:** Sí. View → ViewModel → LavaRepository → implementación → DAO. Las pantallas no conocen Room ni el repositorio.

## Prompt 2 — Descuento por volumen

**Texto de trabajo para Codex:**

> Si el lavado supera 5 kg, aplica 10 % de descuento, muéstralo en el resumen y guárdalo en el pedido. Usa un cálculo común con redondeo monetario para el resumen y el repositorio. Conserva el precio histórico y migra Room sin borrar datos. Comprueba 5 kg, 5.5 kg, pedidos mixtos y coherencia de los ingresos por servicio.

**Qué generó la IA:** CalculoPedido calcula subtotal, descuento y total. NuevoPedidoViewModel expone los totales y las pantallas Nuevo pedido, Detalle y Gestión presentan el desglose. Pedido conserva subtotal y descuento; DetallePedido conserva el descuento de cada línea. Room sube de versión 2 a 3 mediante MIGRATION_2_3.

**Criterio aplicado:** se suman los kilos de las líneas KG; con más de 5 kg, el 10 % se aplica al importe de lavado. No se descuentan planchado ni servicios por prenda/unidad. Exactamente 5 kg no obtiene descuento. Ejemplo: 5.5 kg × S/ 8 + 5 prendas × S/ 2.50 = subtotal S/ 56.50, descuento S/ 4.40, total S/ 52.10.

**Qué se corrigió y verificó:** se centralizó el cálculo con BigDecimal y redondeo HALF_UP a dos decimales; los gráficos descuentan el importe de cada línea para coincidir con el total neto cobrado. La migración deja el subtotal de pedidos antiguos igual a su total y descuento cero, sin recalcular su historia. El repositorio revalida servicio activo, cantidad y límite de pedidos al confirmar. Pasaron la prueba de migración y las comprobaciones de umbral, persistencia y precios históricos.

**¿Respeta MVVM?:** Sí. El cálculo es un modelo puro compartido por ViewModel y Repository. La entidad se persiste en el repositorio; Compose solo recibe el desglose. La migración registrada conserva también la ruta 1 → 2 → 3.

## Prompt 3 — Repetir un pedido

**Texto de trabajo para Codex:**

> Agrega Pedir de nuevo en el historial. Carga las mismas líneas en Nuevo pedido con precios actuales, nota y entrega recalculada. Permite editar y exige confirmación; no copies estado, pago ni casillero. Comprueba propietario y estado final, avisa si omites servicios inactivos o cantidades incompatibles y conserva las reglas de recepción y máximo tres activos.

**Qué generó la IA:** acción en PedidoCard e Historial, argumento opcional de navegación y carga inicial en NuevoPedidoViewModel. El formulario reutiliza servicios activos y tarifas actuales, sin crear el pedido hasta confirmar. El pedido anterior permanece sin cambios.

**Qué se corrigió y verificó:** la carga ocurre una sola vez para no sobrescribir ediciones del estudiante; se bloquean cambios mientras carga. Se comprueba el propietario antes de copiar la nota. Los servicios no disponibles se omiten con aviso. Se verificó que un pedido original de S/ 23.50 se repite con nuevas tarifas por S/ 29.00 y permite cambiar la cantidad antes de confirmar. También se probó el límite de tres pedidos activos y el rechazo de un pedido ajeno.

**¿Respeta MVVM?:** Sí. La View comunica el identificador mediante navegación; el ViewModel usa únicamente LavaRepository y expone la selección. La creación final reutiliza la transacción del repositorio existente.

## Prompt 4 — Validación y entrega

**Texto de trabajo para Codex:**

> Compila la aplicación y sus pruebas, ejecuta las reglas de aceptación y las tres mejoras en un dispositivo, revisa la interfaz oscura y documenta resultados reales. Conserva los datos del teléfono, separa los commits solicitados y publica la rama. No presentes como aprobada ninguna prueba que no se haya ejecutado.

**Qué generó la IA:** pruebas aisladas de atrasados, descuento, migración y repetición, más una prueba Compose que recorre bandeja → historial → nuevo pedido en modo oscuro; tres capturas reales y registros en docs. Se reutilizaron las pruebas de aceptación y persistencia de la Fase 1.

**Qué se corrigió y verificó:** al compilar las nuevas pruebas se ajustaron las llamadas al DAO (casillero requerido) y los identificadores Long/Int. La actividad de prueba tuvo que ponerse al frente por la restricción de lanzamiento en segundo plano del teléfono. Las pruebas funcionales usan Room en memoria y preferencias temporales. La instalación se hizo con `adb install -r`, sin borrar datos. Se compararon las filas y preferencias reales con un respaldo anterior a la Fase 2.

**¿Respeta MVVM?:** Sí. El código de prueba puede preparar datos mediante DAOs; las Views de producción siguen sin hacerlo. No se añadieron dependencias externas.

## Resultados y evidencias

- Compilación: `:app:assembleDebug :app:assembleDebugAndroidTest` correcta.
- **21 pruebas de reglas, persistencia y migración aprobadas**: [registro](docs/pruebas-mejoras.txt).
- **1 prueba de interfaz aprobada**: [registro](docs/pruebas-mejoras-ui.txt).
- [Verificación de conservación de datos](docs/verificacion-datos-mejoras.txt).
- Capturas en [README](README.md#fase-2--mejoras-con-ia).

Las pruebas se ejecutaron el 21/10/2026 en un Xiaomi 23053RN02L con Android 15. La verificación cubre los casos descritos; no representa una prueba de todos los dispositivos Android.

## Reflexión (5 líneas)

La IA ayudó a implementar atrasados, descuentos y repetición sin rehacer las pantallas existentes.<br>
El código generado necesitó revisión: los filtros blancos perdían contraste al usar el tema oscuro.<br>
MVVM permite que Compose muestre StateFlow y delegue los datos al contrato LavaRepository.<br>
Room exige subir la versión y migrar las entidades para conservar pedidos y precios históricos.<br>
Las reglas de negocio deben verificarse al guardar y con pruebas, además de mostrarse en la interfaz.
