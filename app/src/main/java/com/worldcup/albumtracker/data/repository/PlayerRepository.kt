package com.worldcup.albumtracker.data.repository

import com.worldcup.albumtracker.data.model.Player
import com.worldcup.albumtracker.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Result wrapper for the player search use case. It models every state the
 * academic requirements ask us to handle explicitly.
 */
sealed class PlayerResult {
    data class Success(val players: List<Player>) : PlayerResult()
    object Empty : PlayerResult()
    object NoInternet : PlayerResult()
    object Timeout : PlayerResult()
    data class ServerError(val code: Int) : PlayerResult()
    data class Unknown(val message: String) : PlayerResult()
}

/**
 * Repository in charge of the external API (TheSportsDB).
 * Translates raw networking exceptions into a clean [PlayerResult].
 */
class PlayerRepository {

    suspend fun searchPlayers(query: String): PlayerResult = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.api.searchPlayers(query)
            when {
                response.isSuccessful -> {
                    val players = response.body()?.players
                    if (players.isNullOrEmpty()) PlayerResult.Empty
                    else PlayerResult.Success(players)
                }
                else -> PlayerResult.ServerError(response.code())
            }
        } catch (e: SocketTimeoutException) {
            PlayerResult.Timeout
        } catch (e: IOException) {
            // No network connectivity / DNS failure / connection dropped.
            PlayerResult.NoInternet
        } catch (e: HttpException) {
            PlayerResult.ServerError(e.code())
        } catch (e: Exception) {
            PlayerResult.Unknown(e.message ?: "Unknown error")
        }
    }
}
