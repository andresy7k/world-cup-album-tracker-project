package com.worldcup.albumtracker.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldcup.albumtracker.data.model.Sticker
import com.worldcup.albumtracker.data.repository.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel del flujo de "Registrar lámina".
 * Permite buscar una lámina por número y registrarla como obtenida.
 */
class RegisterViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _sticker = MutableLiveData<Sticker?>()
    val sticker: LiveData<Sticker?> = _sticker

    private val _registered = MutableLiveData<RegisterResult?>()
    val registered: LiveData<RegisterResult?> = _registered

    /** Busca una lámina por número para previsualizarla antes de registrar. */
    fun lookup(number: Int) {
        viewModelScope.launch {
            _sticker.value = withContext(Dispatchers.IO) { repository.getStickerByNumber(number) }
        }
    }

    /** Registra la lámina actual como obtenida (o suma repetida si ya la tenía). */
    fun register(number: Int) {
        viewModelScope.launch {
            val (ok, updated) = withContext(Dispatchers.IO) {
                val success = repository.registerSticker(number)
                success to repository.getStickerByNumber(number)
            }
            _registered.value = if (ok && updated != null) {
                RegisterResult(updated, wasRepeated = updated.repeatedCount > 0)
            } else null
        }
    }
}

/** Resultado del registro, para que la UI muestre el feedback adecuado. */
data class RegisterResult(val sticker: Sticker, val wasRepeated: Boolean)
