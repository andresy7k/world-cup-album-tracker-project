package com.worldcup.albumtracker.data.model

/**
 * Resumen agregado de estadísticas del álbum, usado por el Dashboard
 * y la pantalla de estadísticas.
 */
data class AlbumStats(
    val total: Int,
    val obtained: Int,
    val missing: Int,
    val repeated: Int,
    val completionPercent: Int,
    val topCountries: List<CountryProgress>
)

/**
 * Progreso de completitud por selección nacional.
 */
data class CountryProgress(
    val country: String,
    val obtained: Int,
    val total: Int
) {
    val percent: Int
        get() = if (total == 0) 0 else (obtained * 100) / total
}
