package com.worldcup.albumtracker.data.network

import com.google.gson.annotations.SerializedName
import com.worldcup.albumtracker.data.model.Player

/**
 * Wrapper that maps the JSON returned by:
 * https://www.thesportsdb.com/api/v1/json/3/searchplayers.php?p=<query>
 *
 * The API returns { "player": [ ... ] } or { "player": null } when nothing matches.
 */
data class PlayerResponse(
    @SerializedName("player")
    val players: List<Player>?
)
