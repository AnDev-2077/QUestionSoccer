package com.devapps.questionsoccer.interfaces

import com.devapps.questionsoccer.items.CountryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface CountryService {
    @Headers(
        "x-rapidapi-key: 84bd0aa6dc133eb6b2fe0c8a336da534",
        //"x-rapidapi-key: 1d63a493af62503a34eee5919e8ea2cd",
        //"x-rapidapi-key: dd437e2fa630f435fb208f72d9cad76b",
        "x-rapidapi-host: v3.football.api-sports.io"
    )
    @GET("teams/countries")
    suspend fun getCountries(): Response<CountryResponse>
}