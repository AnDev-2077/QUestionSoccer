package com.devapps.questionsoccer.items
import com.google.gson.annotations.SerializedName
data class TeamsByLeagueResponse(
    val get: String,
    val parameters: Parameters,
    val errors: Any,
    val results: Int,
    val paging: Paging,
    val response: List<ResponseItem>
)
data class Parameters(
    val league: String,
    val season: String
)

data class Paging(
    val current: Int,
    val total: Int
)

data class ResponseItem(
    val team: Team,
    val venue: Venue
)

data class Team(
    @SerializedName("id") val teamId: String,
    @SerializedName("name") val teamName: String,
    @SerializedName("code") val teamCode: String,
    @SerializedName("country")val teamCountry: String,
    @SerializedName("founded")val teamFounded: String,
    @SerializedName("national")val teamNational: String,
    @SerializedName("logo")val teamLogo: String
)

data class Venue(
    @SerializedName("id") val venueId: Int,
    @SerializedName("name") val venueName: String,
    @SerializedName("address") val venueAddress: String,
    @SerializedName("city") val venueCity: String,
    @SerializedName("capacity") val venueCapacity: Int,
    @SerializedName("surface") val venueSurface: String,
    @SerializedName("image") val venueImage: String
)
