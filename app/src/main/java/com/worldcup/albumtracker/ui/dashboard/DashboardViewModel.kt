package com.worldcup.albumtracker.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldcup.albumtracker.data.model.AlbumStats
import com.worldcup.albumtracker.data.model.Sticker
import com.worldcup.albumtracker.data.repository.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the Dashboard. Exposes aggregate stats and the recently
 * obtained stickers as observable [LiveData].
 */
class DashboardViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _stats = MutableLiveData<AlbumStats>()
    val stats: LiveData<AlbumStats> = _stats

    private val _recent = MutableLiveData<List<Sticker>>()
    val recent: LiveData<List<Sticker>> = _recent

    fun load() {
        viewModelScope.launch {
            val (s, r) = withContext(Dispatchers.IO) {
                repository.getAlbumStats() to repository.getRecentObtained(6)
            }
            _stats.value = s
            _recent.value = r
        }
    }
}
