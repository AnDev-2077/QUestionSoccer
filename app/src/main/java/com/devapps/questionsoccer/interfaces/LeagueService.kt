package com.devapps.questionsoccer.interfaces

import com.devapps.questionsoccer.items.FixturesByLeagueResponse
import com.devapps.questionsoccer.items.LeagueResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface LeagueService {
    @Headers(
        "x-rapidapi-key: 1d63a493af62503a34eee5919e8ea2cd",
        //"x-rapidapi-key: dd437e2fa630f435fb208f72d9cad76b",
        "x-rapidapi-host: v3.football.api-sports.io"
    )
    @GET("leagues")
    suspend fun getLeagues(): Response<LeagueResponse>
}