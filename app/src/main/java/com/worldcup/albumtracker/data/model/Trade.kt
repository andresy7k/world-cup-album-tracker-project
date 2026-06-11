package com.worldcup.albumtracker.data.model

/**
 * Representa un intercambio de láminas realizado por el usuario.
 *
 * @property id Identificador único en la base de datos.
 * @property stickerGiven Descripción/numero de la lámina entregada.
 * @property stickerReceived Descripción/numero de la lámina recibida.
 * @property tradeDate Fecha del intercambio (formato legible).
 */
data class Trade(
    val id: Long = 0,
    val stickerGiven: String,
    val stickerReceived: String,
    val tradeDate: String
)
