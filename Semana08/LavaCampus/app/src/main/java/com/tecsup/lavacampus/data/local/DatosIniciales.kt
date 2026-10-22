package com.tecsup.lavacampus.data.local

import androidx.room.withTransaction
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

object DatosIniciales {
    suspend fun sembrar(db: LavaDatabase) {
        val servicioDao = db.servicioDao()
        val usuarioDao = db.usuarioDao()
        val pedidoDao = db.pedidoDao()
        val detalleDao = db.detalleDao()

        // Toda la comprobación y siembra ocurre dentro de una única transacción atómica
        db.withTransaction {
            // 1. Comprobar primero usuarioDao.contar()
            if (usuarioDao.contar() > 0) {
                // Si ya existen usuarios, no hacer nada y terminar la transacción
                return@withTransaction
            }

            // 2. Si no existen usuarios (instalación limpia o migración desde versión 1):
            // Consultar servicios existentes e insertar únicamente los servicios base faltantes
            val serviciosExistentes = servicioDao.obtenerTodosLista()
            val nombresExistentes = serviciosExistentes.map { it.nombre }.toSet()

            val serviciosBase = listOf(
                Servicio(
                    nombre = "Lavado y secado",
                    descripcion = "Lavado completo y secado profesional por kilo",
                    unidad = "KG",
                    precio = 8.00,
                    horasEntrega = 24,
                    activo = true,
                    icono = "wash"
                ),
                Servicio(
                    nombre = "Planchado",
                    descripcion = "Planchado a vapor impecable por prenda",
                    unidad = "PRENDA",
                    precio = 2.50,
                    horasEntrega = 48,
                    activo = true,
                    icono = "iron"
                ),
                Servicio(
                    nombre = "Lavado en seco",
                    descripcion = "Tratamiento delicado e higiénico para trajes y vestidos",
                    unidad = "PRENDA",
                    precio = 12.00,
                    horasEntrega = 48,
                    activo = true,
                    icono = "shirt"
                ),
                Servicio(
                    nombre = "Edredón / frazada",
                    descripcion = "Lavado profundo y desinfección de ropa de cama grande",
                    unidad = "UNIDAD",
                    precio = 20.00,
                    horasEntrega = 48,
                    activo = false,
                    icono = "shirt"
                )
            )

            val faltantes = serviciosBase.filter { it.nombre !in nombresExistentes }
            if (faltantes.isNotEmpty()) {
                servicioDao.insertarTodos(faltantes)
            }

            // Inserta los usuarios iniciales
            val usuariosBase = listOf(
                Usuario(
                    nombre = "Ana García",
                    codigo = "U202310234",
                    correo = "ana@correo.test",
                    clave = "1234",
                    rol = "CLIENTE"
                ),
                Usuario(
                    nombre = "Luis Ramos",
                    codigo = "U202310877",
                    correo = "luis@correo.test",
                    clave = "1234",
                    rol = "CLIENTE"
                ),
                Usuario(
                    nombre = "Marta Paredes",
                    codigo = "U202410122",
                    correo = "marta@correo.test",
                    clave = "1234",
                    rol = "CLIENTE"
                ),
                Usuario(
                    nombre = "Administración",
                    codigo = "",
                    correo = "admin@lavacampus.test",
                    clave = "admin",
                    rol = "ADMIN"
                )
            )
            usuarioDao.insertarTodos(usuariosBase)

            // Obtener los registros reales insertados para recuperar sus IDs
            val listaUsuarios = usuarioDao.obtenerTodosLista()
            val ana = listaUsuarios.first { it.correo == "ana@correo.test" }
            val luis = listaUsuarios.first { it.correo == "luis@correo.test" }
            val marta = listaUsuarios.first { it.correo == "marta@correo.test" }

            val listaServicios = servicioDao.obtenerTodosLista()
            val lavado = listaServicios.first { it.nombre == "Lavado y secado" }
            val planchado = listaServicios.first { it.nombre == "Planchado" }
            val enSeco = listaServicios.first { it.nombre == "Lavado en seco" }
            val edredon = listaServicios.first { it.nombre == "Edredón / frazada" }

            val today = LocalDate.now()
            val thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE

            fun calcularFechaEntrega(fechaReg: LocalDate, horas: Int): String {
                val dias = (horas / 24).coerceAtLeast(1).toLong()
                var entrega = fechaReg.plusDays(dias)
                if (entrega.dayOfWeek == DayOfWeek.SUNDAY) {
                    entrega = entrega.plusDays(1)
                }
                return entrega.format(formatter)
            }

            suspend fun crearPedidoConDetalles(
                usuarioId: Int,
                fechaRegDate: LocalDate,
                horasMax: Int,
                estado: String,
                casillero: String? = null,
                pagado: Boolean = false,
                nota: String = "",
                items: List<Pair<Servicio, Double>>
            ) {
                val fechaRegStr = fechaRegDate.format(formatter)
                val fechaEntStr = if (estado == "ENTREGADO" && fechaRegDate <= today) {
                    fechaRegDate.format(formatter)
                } else {
                    calcularFechaEntrega(fechaRegDate, horasMax)
                }

                var totalPedido = 0.0
                val detallesTemp = items.map { (servicio, cantidad) ->
                    val subtotal = cantidad * servicio.precio
                    totalPedido += subtotal
                    Triple(servicio.id, cantidad, servicio.precio to subtotal)
                }

                val pedidoId = pedidoDao.insertar(
                    Pedido(
                        usuarioId = usuarioId,
                        fechaRegistro = fechaRegStr,
                        fechaEntrega = fechaEntStr,
                        estado = estado,
                        total = totalPedido,
                        subtotal = totalPedido,
                        casillero = casillero,
                        pagado = pagado,
                        nota = nota
                    )
                ).toInt()

                val listaDetalles = detallesTemp.map { (servicioId, cantidad, precios) ->
                    DetallePedido(
                        pedidoId = pedidoId,
                        servicioId = servicioId,
                        cantidad = cantidad,
                        precioUnitario = precios.first,
                        subtotal = precios.second
                    )
                }
                detalleDao.insertarTodos(listaDetalles)
            }

            val fechaEntregadoMarta = if (today > thisMonday) thisMonday.plusDays(1) else thisMonday

            // 10 Pedidos
            // 1. Ana - ENTREGADO
            crearPedidoConDetalles(
                usuarioId = ana.id,
                fechaRegDate = thisMonday,
                horasMax = 24,
                estado = "ENTREGADO",
                pagado = true,
                items = listOf(lavado to 3.5, planchado to 4.0)
            )

            // 2. Luis - ENTREGADO
            crearPedidoConDetalles(
                usuarioId = luis.id,
                fechaRegDate = thisMonday,
                horasMax = 48,
                estado = "ENTREGADO",
                pagado = true,
                items = listOf(enSeco to 2.0)
            )

            // 3. Marta - ENTREGADO
            crearPedidoConDetalles(
                usuarioId = marta.id,
                fechaRegDate = fechaEntregadoMarta,
                horasMax = 48,
                estado = "ENTREGADO",
                pagado = true,
                items = listOf(edredon to 1.0, lavado to 2.0)
            )

            // 4. Ana - LISTO (C-05)
            crearPedidoConDetalles(
                usuarioId = ana.id,
                fechaRegDate = today.minusDays(2),
                horasMax = 24,
                estado = "LISTO",
                casillero = "C-05",
                pagado = false,
                items = listOf(lavado to 5.0)
            )

            // 5. Luis - LISTO (C-07)
            crearPedidoConDetalles(
                usuarioId = luis.id,
                fechaRegDate = today.minusDays(2),
                horasMax = 48,
                estado = "LISTO",
                casillero = "C-07",
                pagado = false,
                items = listOf(planchado to 6.0, enSeco to 1.0)
            )

            // 6. Luis - EN_PROCESO
            crearPedidoConDetalles(
                usuarioId = luis.id,
                fechaRegDate = today.minusDays(1),
                horasMax = 24,
                estado = "EN_PROCESO",
                pagado = false,
                items = listOf(lavado to 4.0)
            )

            // 7. Marta - EN_PROCESO
            crearPedidoConDetalles(
                usuarioId = marta.id,
                fechaRegDate = today.minusDays(1),
                horasMax = 48,
                estado = "EN_PROCESO",
                pagado = false,
                items = listOf(planchado to 10.0)
            )

            // 8. Ana - RECIBIDO
            crearPedidoConDetalles(
                usuarioId = ana.id,
                fechaRegDate = today,
                horasMax = 48,
                estado = "RECIBIDO",
                pagado = false,
                items = listOf(lavado to 2.5, enSeco to 1.0)
            )

            // 9. Marta - RECIBIDO
            crearPedidoConDetalles(
                usuarioId = marta.id,
                fechaRegDate = today,
                horasMax = 48,
                estado = "RECIBIDO",
                pagado = false,
                items = listOf(planchado to 4.0)
            )

            // 10. Ana - CANCELADO
            crearPedidoConDetalles(
                usuarioId = ana.id,
                fechaRegDate = today.minusDays(3),
                horasMax = 48,
                estado = "CANCELADO",
                pagado = false,
                items = listOf(edredon to 1.0)
            )
        }
    }
}
