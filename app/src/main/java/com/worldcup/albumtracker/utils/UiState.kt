package com.worldcup.albumtracker.utils

/**
 * Estado genérico para operaciones asíncronas (API / DB).
 * Permite a la UI reaccionar a Loading / Success / Error de forma consistente.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val type: ErrorType, val message: String) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}

/**
 * Tipos de error manejados de forma explícita por la app.
 */
enum class ErrorType {
    NO_INTERNET,
    NO_RESULTS,
    SERVER,
    TIMEOUT,
    UNKNOWN
}
