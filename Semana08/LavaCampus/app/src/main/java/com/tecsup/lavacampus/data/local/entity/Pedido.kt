package com.tecsup.lavacampus.data.local.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pedidos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["usuarioId"]),
        Index(value = ["estado"])
    ]
)
data class Pedido(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val fechaRegistro: String,
    val fechaEntrega: String,
    val estado: String,
    val total: Double,
    val casillero: String? = null,
    val pagado: Boolean = false,
    val nota: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "0") val subtotal: Double = 0.0,
    @ColumnInfo(defaultValue = "0") val descuento: Double = 0.0
)
