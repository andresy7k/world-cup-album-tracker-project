package com.worldcup.albumtracker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldcup.albumtracker.data.model.Player
import com.worldcup.albumtracker.data.repository.PlayerRepository
import com.worldcup.albumtracker.data.repository.PlayerResult
import com.worldcup.albumtracker.utils.ErrorType
import com.worldcup.albumtracker.utils.UiState
import kotlinx.coroutines.launch

/**
 * ViewModel del buscador de jugadores (consumo de API externa).
 *
 * Expone el resultado como [UiState] para que la UI reaccione de forma
 * homogénea a los estados Loading / Success / Empty / Error.
 */
class SearchViewModel(private val repository: PlayerRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<Player>>>()
    val state: LiveData<UiState<List<Player>>> = _state

    fun search(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.searchPlayers(trimmed)
            _state.value = when (result) {
                is PlayerResult.Success -> UiState.Success(result.players)
                is PlayerResult.Empty -> UiState.Empty
                is PlayerResult.NoInternet ->
                    UiState.Error(ErrorType.NO_INTERNET, "Sin conexión a internet")
                is PlayerResult.Timeout ->
                    UiState.Error(ErrorType.TIMEOUT, "La conexión tardó demasiado")
                is PlayerResult.ServerError ->
                    UiState.Error(ErrorType.SERVER, "Error del servidor (${result.code})")
                is PlayerResult.Unknown ->
                    UiState.Error(ErrorType.UNKNOWN, result.message)
            }
        }
    }
}
