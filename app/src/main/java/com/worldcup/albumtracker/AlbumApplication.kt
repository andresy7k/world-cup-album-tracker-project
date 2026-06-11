package com.worldcup.albumtracker

import android.app.Application
import com.worldcup.albumtracker.data.database.AlbumDbHelper
import com.worldcup.albumtracker.data.repository.AlbumRepository
import com.worldcup.albumtracker.data.repository.PlayerRepository

/**
 * Application class that acts as a tiny Service Locator.
 *
 * For an academic project this keeps dependency wiring simple and explicit
 * (no Dagger/Hilt) while still respecting the Repository Pattern and MVVM:
 * Activities/Fragments obtain repositories from here and pass them to a
 * [androidx.lifecycle.ViewModelProvider.Factory].
 */
class AlbumApplication : Application() {

    // Lazily created so the DB is only opened when first needed.
    val albumRepository: AlbumRepository by lazy {
        AlbumRepository(AlbumDbHelper(this))
    }

    val playerRepository: PlayerRepository by lazy {
        PlayerRepository()
    }
}
