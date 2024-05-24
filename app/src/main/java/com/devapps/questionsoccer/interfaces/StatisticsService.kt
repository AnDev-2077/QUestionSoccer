package com.devapps.questionsoccer.interfaces


import com.devapps.questionsoccer.items.StatisticsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface StatisticsService {

    @Headers(
        "x-rapidapi-key: 84bd0aa6dc133eb6b2fe0c8a336da534",
        //"x-rapidapi-key: 1d63a493af62503a34eee5919e8ea2cd",
        //"x-rapidapi-key: dd437e2fa630f435fb208f72d9cad76b",
        "x-rapidapi-host: v3.football.api-sports.io"
    )

    @GET("teams/statistics?league=39&team=33&season=2023")
    suspend fun getStatistics(): Response<StatisticsResponse>
}