package com.worldcup.albumtracker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.worldcup.albumtracker.R
import com.worldcup.albumtracker.data.model.Player
import com.worldcup.albumtracker.databinding.ItemPlayerBinding

/**
 * Adapter que muestra los jugadores devueltos por la API.
 * Carga la foto del jugador con Glide e incluye un placeholder elegante.
 */
class PlayerAdapter : ListAdapter<Player, PlayerAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPlayerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val player = getItem(position)
        val ctx = holder.itemView.context
        with(holder.binding) {
            txtPlayerName.text = player.strPlayer ?: "—"
            txtNationality.text = ctx.getString(
                R.string.player_field, ctx.getString(R.string.player_nationality),
                player.strNationality ?: "—"
            )
            txtTeam.text = ctx.getString(
                R.string.player_field, ctx.getString(R.string.player_team),
                player.strTeam ?: "—"
            )
            txtPosition.text = ctx.getString(
                R.string.player_field, ctx.getString(R.string.player_position),
                player.strPosition ?: "—"
            )

            Glide.with(imgPlayer)
                .load(player.strThumb)
                .placeholder(R.drawable.bg_sticker_obtained)
                .error(R.drawable.ic_person)
                .centerCrop()
                .into(imgPlayer)
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Player>() {
            override fun areItemsTheSame(oldItem: Player, newItem: Player) =
                oldItem.strPlayer == newItem.strPlayer

            override fun areContentsTheSame(oldItem: Player, newItem: Player) =
                oldItem == newItem
        }
    }
}
