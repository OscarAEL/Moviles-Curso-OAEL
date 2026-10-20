package com.tecsup.lavacampus.model

enum class EstadoPedido {
    RECIBIDO,
    EN_PROCESO,
    LISTO,
    ENTREGADO,
    CANCELADO;

    fun siguiente(): EstadoPedido? = when (this) {
        RECIBIDO -> EN_PROCESO
        EN_PROCESO -> LISTO
        LISTO -> ENTREGADO
        ENTREGADO, CANCELADO -> null
    }
}
