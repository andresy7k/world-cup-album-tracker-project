package com.worldcup.albumtracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.databinding.ActivityMainBinding
import com.worldcup.albumtracker.ui.album.AlbumFragment
import com.worldcup.albumtracker.ui.dashboard.DashboardFragment
import com.worldcup.albumtracker.ui.search.SearchFragment
import com.worldcup.albumtracker.ui.settings.SettingsFragment
import com.worldcup.albumtracker.ui.statistics.StatisticsFragment

/**
 * Single host activity that swaps the five primary destinations using a
 * [com.google.android.material.bottomnavigation.BottomNavigationView].
 *
 * Detail / action flows (register, trade, history) are launched as separate
 * activities from within the fragments.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            showFragment(DashboardFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_dashboard -> DashboardFragment()
                R.id.nav_album -> AlbumFragment()
                R.id.nav_search -> SearchFragment()
                R.id.nav_stats -> StatisticsFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> DashboardFragment()
            }
            showFragment(fragment)
            true
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.item_fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    /** Allows fragments to refresh the dashboard after an action. */
    fun navigateToDashboard() {
        binding.bottomNav.selectedItemId = R.id.nav_dashboard
    }
}
