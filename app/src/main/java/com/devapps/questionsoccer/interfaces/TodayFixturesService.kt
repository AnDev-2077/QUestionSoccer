package com.devapps.questionsoccer.interfaces

import com.devapps.questionsoccer.items.FixturesByLeagueResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface TodayFixturesService {
    interface FixtureService {
        @Headers(
            //"x-rapidapi-key: 84bd0aa6dc133eb6b2fe0c8a336da534",
            "x-rapidapi-key: dd437e2fa630f435fb208f72d9cad76b",
            "x-rapidapi-host: v3.football.api-sports.io"
        )
        @GET("fixtures?date=2024-04-27&league=39&season=2023")
        suspend fun getFixturesForToday(): Response<FixturesByLeagueResponse>
    }
}