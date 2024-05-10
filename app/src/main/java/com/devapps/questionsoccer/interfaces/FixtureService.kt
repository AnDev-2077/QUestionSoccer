package com.devapps.questionsoccer.interfaces

import com.devapps.questionsoccer.items.FixturesByLeagueResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface FixtureService {
    @Headers(
        //"x-rapidapi-key: aafd07163d4457eaf7a9dc45aece51a2",
        "x-rapidapi-key: dd437e2fa630f435fb208f72d9cad76b",
        "x-rapidapi-host: v3.football.api-sports.io"
    )
    @GET("fixtures?date=2024-04-27&league=39&season=2023")
    suspend fun getFixtureByLeague(): Response<FixturesByLeagueResponse>
}