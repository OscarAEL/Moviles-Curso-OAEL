package com.tecsup.lavacampus.data.repository

import androidx.room.withTransaction
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.ClienteResumen
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.IngresoServicio
import com.tecsup.lavacampus.model.LineaPedido
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class LavaRepositoryRoom(
    private val db: LavaDatabase
) : LavaRepository {

    private val servicioDao = db.servicioDao()
    private val usuarioDao = db.usuarioDao()
    private val pedidoDao = db.pedidoDao()
    private val detalleDao = db.detalleDao()

    // Usuarios
    override suspend fun obtenerUsuarioPorRol(rol: String): Usuario? = usuarioDao.obtenerPorRol(rol)

    override suspend fun login(correo: String, clave: String): Usuario? {
        return usuarioDao.login(correo, clave)
    }

    override suspend fun obtenerUsuario(id: Int): Usuario? {
        return usuarioDao.obtener(id)
    }

    override fun observarClientes(): Flow<List<ClienteResumen>> {
        return usuarioDao.observarClientes()
    }

    // Servicios
    override fun observarServicios(): Flow<List<Servicio>> {
        return servicioDao.observarTodos()
    }

    override fun observarServiciosActivos(): Flow<List<Servicio>> {
        return servicioDao.observarActivos()
    }

    override suspend fun guardarServicio(s: Servicio) {
        if (s.id == 0) {
            servicioDao.insertar(s)
        } else {
            servicioDao.actualizar(s)
        }
    }

    override suspend fun eliminarServicio(s: Servicio) {
        servicioDao.eliminar(s)
    }

    override suspend fun cambiarActivo(id: Int, activo: Boolean) {
        servicioDao.cambiarActivo(id, activo)
    }

    override suspend fun servicioTieneDetalles(id: Int): Boolean {
        return detalleDao.contarPorServicio(id) > 0
    }

    // Pedidos
    override fun observarPedidosAtrasados(hoy: String): Flow<List<Int>> = pedidoDao.observarAtrasados(hoy)

    override suspend fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String
    ): Long {
        return db.withTransaction {
            if (lineas.isEmpty()) {
                throw IllegalArgumentException("El pedido debe contener al menos una línea")
            }

            var totalCalculado = 0.0
            val detallesAInsertar = mutableListOf<Triple<Int, Double, Pair<Double, Double>>>()

            for (linea in lineas) {
                val servicio = servicioDao.obtener(linea.servicioId)
                    ?: throw IllegalStateException("Servicio no encontrado con ID ${linea.servicioId}")

                val precioUnitario = servicio.precio
                val subtotal = linea.cantidad * precioUnitario
                totalCalculado += subtotal

                detallesAInsertar.add(
                    Triple(servicio.id, linea.cantidad, precioUnitario to subtotal)
                )
            }

            val fechaRegistroStr = LocalDate.now().toString()
            val pedidoNuevo = Pedido(
                usuarioId = usuarioId,
                fechaRegistro = fechaRegistroStr,
                fechaEntrega = fechaEntrega,
                estado = EstadoPedido.RECIBIDO.name,
                total = totalCalculado,
                casillero = null,
                pagado = false,
                nota = nota
            )

            val pedidoId = pedidoDao.insertar(pedidoNuevo)

            val listaDetalles = detallesAInsertar.map { (servicioId, cantidad, precios) ->
                DetallePedido(
                    pedidoId = pedidoId.toInt(),
                    servicioId = servicioId,
                    cantidad = cantidad,
                    precioUnitario = precios.first,
                    subtotal = precios.second
                )
            }

            detalleDao.insertarTodos(listaDetalles)

            pedidoId
        }
    }

    override suspend fun contarActivos(usuarioId: Int): Int {
        return pedidoDao.contarActivos(usuarioId)
    }

    override fun observarPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>> {
        return pedidoDao.observarPorUsuario(usuarioId)
    }

    override fun observarPedidos(): Flow<List<PedidoConDetalle>> {
        return pedidoDao.observarTodos()
    }

    override fun observarPedido(id: Int): Flow<PedidoConDetalle?> {
        return pedidoDao.observarUno(id)
    }

    override suspend fun cambiarEstado(
        id: Int,
        estado: EstadoPedido,
        casillero: String?
    ) {
        pedidoDao.actualizarEstado(id, estado.name, casillero)
    }

    override suspend fun marcarPagado(id: Int, pagado: Boolean) {
        pedidoDao.actualizarPagado(id, pagado)
    }

    override fun observarCasillerosOcupados(): Flow<List<String>> {
        return pedidoDao.observarCasillerosOcupados()
    }

    // Panel
    override fun observarPedidosDeFecha(fecha: String): Flow<Int> {
        return pedidoDao.contarPorFecha(fecha)
    }

    override fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int> {
        return pedidoDao.contarPorEstado(estado.name)
    }

    override fun observarIngresosDesde(fechaInicio: String): Flow<Double> {
        return pedidoDao.ingresosDesde(fechaInicio)
    }

    override fun observarIngresosPorServicio(fechaInicio: String): Flow<List<IngresoServicio>> {
        return detalleDao.observarIngresosPorServicio(fechaInicio)
    }
}
