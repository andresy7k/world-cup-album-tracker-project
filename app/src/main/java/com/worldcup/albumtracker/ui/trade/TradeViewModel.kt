package com.worldcup.albumtracker.ui.trade

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldcup.albumtracker.data.repository.AlbumRepository
import com.worldcup.albumtracker.data.repository.TradeOutcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel del flujo "Intercambiar lámina".
 *
 * El usuario indica la lámina que entrega (debe tener repetidas) y la que
 * recibe; al confirmar se valida y registra el intercambio en el repositorio.
 */
class TradeViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _result = MutableLiveData<TradeOutcome?>()
    val result: LiveData<TradeOutcome?> = _result

    /** Registra el intercambio aplicando todas las validaciones del repositorio. */
    fun trade(givenNumber: Int, receivedNumber: Int) {
        viewModelScope.launch {
            _result.value = withContext(Dispatchers.IO) {
                repository.registerTrade(givenNumber, receivedNumber)
            }
        }
    }

    /** Limpia el último resultado para evitar reentregas al rotar pantalla. */
    fun clearResult() {
        _result.value = null
    }
}
