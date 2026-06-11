package com.worldcup.albumtracker.ui.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.worldcup.albumtracker.data.model.CountryProgress
import com.worldcup.albumtracker.databinding.ItemCountryProgressBinding

/**
 * Adapter que muestra el progreso de completitud por selección nacional,
 * con una barra de progreso por país (requisito "selecciones más completadas").
 */
class CountryProgressAdapter :
    ListAdapter<CountryProgress, CountryProgressAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemCountryProgressBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCountryProgressBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            txtCountry.text = item.country
            txtCount.text = "${item.obtained}/${item.total}"
            txtPercent.text = "${item.percent}%"
            progressCountry.progress = item.percent
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CountryProgress>() {
            override fun areItemsTheSame(oldItem: CountryProgress, newItem: CountryProgress) =
                oldItem.country == newItem.country

            override fun areContentsTheSame(oldItem: CountryProgress, newItem: CountryProgress) =
                oldItem == newItem
        }
    }
}
