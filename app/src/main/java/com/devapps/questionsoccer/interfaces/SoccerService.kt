package com.devapps.questionsoccer.interfaces

import com.devapps.questionsoccer.items.TeamsByLeagueResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface SoccerService {
    @Headers(
        //"x-rapidapi-key: 84bd0aa6dc133eb6b2fe0c8a336da534",
        "x-rapidapi-key: dd437e2fa630f435fb208f72d9cad76b",
        "x-rapidapi-host: v3.football.api-sports.io"
    )
    @GET("teams?league=39&season=2023")
    suspend fun getTeamsByLeague(): Response<TeamsByLeagueResponse>
}