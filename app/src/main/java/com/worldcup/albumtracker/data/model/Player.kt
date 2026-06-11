package com.worldcup.albumtracker.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de jugador devuelto por TheSportsDB API.
 * Solo se mapean los campos utilizados en la UI.
 */
data class Player(
    @SerializedName("strPlayer") val strPlayer: String?,
    @SerializedName("strNationality") val strNationality: String?,
    @SerializedName("strTeam") val strTeam: String?,
    @SerializedName("strPosition") val strPosition: String?,
    @SerializedName("strThumb") val strThumb: String?
)
