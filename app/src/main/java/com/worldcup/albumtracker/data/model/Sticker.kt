package com.worldcup.albumtracker.data.model

/**
 * Representa una lámina del álbum del Mundial 2026.
 *
 * @property id Identificador único en la base de datos.
 * @property number Número de la lámina dentro del álbum.
 * @property playerName Nombre del jugador.
 * @property country Selección nacional a la que pertenece.
 * @property obtained Indica si el usuario ya posee la lámina.
 * @property repeatedCount Cantidad de repetidas disponibles para intercambio.
 */
data class Sticker(
    val id: Long = 0,
    val number: Int,
    val playerName: String,
    val country: String,
    val obtained: Boolean = false,
    val repeatedCount: Int = 0
)
