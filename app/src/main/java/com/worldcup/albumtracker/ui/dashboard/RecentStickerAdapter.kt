package com.worldcup.albumtracker.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.data.model.Sticker
import com.worldcup.albumtracker.databinding.ItemRecentStickerBinding

/**
 * Horizontal adapter showing the most recently obtained stickers on the dashboard.
 */
class RecentStickerAdapter :
    ListAdapter<Sticker, RecentStickerAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemRecentStickerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRecentStickerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val sticker = getItem(position)
        val ctx = holder.itemView.context
        holder.binding.txtNumber.text = ctx.getString(R.string.sticker_number, sticker.number)
        holder.binding.txtPlayer.text = sticker.playerName
        holder.binding.txtCountry.text = sticker.country
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Sticker>() {
            override fun areItemsTheSame(oldItem: Sticker, newItem: Sticker) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Sticker, newItem: Sticker) =
                oldItem == newItem
        }
    }
}
