package com.worldcup.albumtracker.ui.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldcup.albumtracker.data.model.Sticker
import com.worldcup.albumtracker.data.repository.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Tabs available in the album screen. */
enum class AlbumTab { ALL, OBTAINED, MISSING, REPEATED }

/**
 * ViewModel backing the album screen. Holds the selected tab and country
 * filter and exposes the resulting sticker list.
 */
class AlbumViewModel(private val repository: AlbumRepository) : ViewModel() {

    private val _stickers = MutableLiveData<List<Sticker>>()
    val stickers: LiveData<List<Sticker>> = _stickers

    private val _countries = MutableLiveData<List<String>>()
    val countries: LiveData<List<String>> = _countries

    private var currentTab: AlbumTab = AlbumTab.ALL
    private var currentCountry: String? = null // null = all countries

    fun loadCountries() {
        viewModelScope.launch {
            _countries.value = withContext(Dispatchers.IO) { repository.getCountries() }
        }
    }

    fun selectTab(tab: AlbumTab) {
        currentTab = tab
        refresh()
    }

    fun selectCountry(country: String?) {
        currentCountry = country
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                val base = when (currentTab) {
                    AlbumTab.ALL -> repository.getAllStickers()
                    AlbumTab.OBTAINED -> repository.getObtainedStickers()
                    AlbumTab.MISSING -> repository.getMissingStickers()
                    AlbumTab.REPEATED -> repository.getRepeatedStickers()
                }
                currentCountry?.let { c -> base.filter { it.country == c } } ?: base
            }
            _stickers.value = list
        }
    }
}
