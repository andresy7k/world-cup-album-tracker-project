package com.worldcup.albumtracker.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.worldcup.albumtracker.data.repository.AlbumRepository
import com.worldcup.albumtracker.data.repository.PlayerRepository
import com.worldcup.albumtracker.ui.album.AlbumViewModel
import com.worldcup.albumtracker.ui.dashboard.DashboardViewModel
import com.worldcup.albumtracker.ui.register.RegisterViewModel
import com.worldcup.albumtracker.ui.search.SearchViewModel
import com.worldcup.albumtracker.ui.statistics.StatisticsViewModel
import com.worldcup.albumtracker.ui.trade.TradeViewModel

/**
 * Generic ViewModel factory that injects the required repositories.
 * Centralising creation here avoids duplicating factory boilerplate per screen.
 */
class ViewModelFactory(
    private val albumRepository: AlbumRepository,
    private val playerRepository: PlayerRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(albumRepository) as T
            modelClass.isAssignableFrom(AlbumViewModel::class.java) ->
                AlbumViewModel(albumRepository) as T
            modelClass.isAssignableFrom(TradeViewModel::class.java) ->
                TradeViewModel(albumRepository) as T
            modelClass.isAssignableFrom(StatisticsViewModel::class.java) ->
                StatisticsViewModel(albumRepository) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) ->
                SearchViewModel(playerRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(albumRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
