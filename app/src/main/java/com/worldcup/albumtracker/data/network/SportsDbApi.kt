package com.worldcup.albumtracker.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit definition for TheSportsDB search endpoint.
 * Free/dev API key "3" is used as documented by TheSportsDB.
 */
interface SportsDbApi {

    @GET("searchplayers.php")
    suspend fun searchPlayers(
        @Query("p") playerName: String
    ): Response<PlayerResponse>

    companion object {
        const val BASE_URL = "https://www.thesportsdb.com/api/v1/json/3/"
    }
}
