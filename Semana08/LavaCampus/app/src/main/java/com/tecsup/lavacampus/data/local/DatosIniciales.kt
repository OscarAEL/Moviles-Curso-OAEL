package com.tecsup.lavacampus.data.local

import com.tecsup.lavacampus.data.local.entity.Servicio

object DatosIniciales {
    suspend fun sembrar(db: LavaDatabase) {
        val dao = db.servicioDao()
        if (dao.contar() == 0) {
            val lista = listOf(
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
            dao.insertarTodos(lista)
        }
    }
}
