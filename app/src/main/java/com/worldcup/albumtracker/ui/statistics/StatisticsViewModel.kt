package com.worldcup.albumtracker.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldcup.albumtracker.data.model.AlbumStats
import com.worldcup.albumtracker.data.model.CountryProgress
import com.worldcup.albumtracker.data.repository.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel de la pantalla de Estadísticas. Expone el resumen agregado
 * del álbum y el progreso por selección nacional.
 */
class StatisticsViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _stats = MutableLiveData<AlbumStats>()
    val stats: LiveData<AlbumStats> = _stats

    private val _countries = MutableLiveData<List<CountryProgress>>()
    val countries: LiveData<List<CountryProgress>> = _countries

    fun load() {
        viewModelScope.launch {
            val (s, c) = withContext(Dispatchers.IO) {
                repository.getAlbumStats() to repository.getCountryProgress()
            }
            _stats.value = s
            _countries.value = c
        }
    }
}
