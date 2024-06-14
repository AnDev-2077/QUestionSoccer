package com.devapps.questionsoccer.items

import java.io.Serializable


data class LeagueResponse(
    val get: String,
    val parameters: Any,
    val errors: Any,
    val results: Int,
    val paging: lPaging,
    val response: List<LeagueResponseItem>,
)

data class lPaging(
    val current: Int,
    val total: Int
)


data class LeagueResponseItem(
    val league: lLeague = lLeague(),
    val country: Country = Country(),
    val seasons: List<Season> =listOf(),
) : Serializable

data class lLeague(
    val id: Int =  0,
    val name: String = "",
    val type: String = "",
    val logo: String = ""
)

data class Country(
    val name: String = "",
    val code: Any? = null,
    val flag: Any? = null
)

data class Season(
    val year: Int = 0,
    val start: String = "",
    val end: String = "",
    val current: Boolean = false,
    val coverage: Coverage = Coverage()
)

data class Coverage(
    val fixtures: Fixtures = Fixtures(),
    val standings: Boolean = false,
    val players: Boolean = false,
    val top_scorers: Boolean = false,
    val top_assists: Boolean = false,
    val top_cards: Boolean = false,
    val injuries: Boolean = false,
    val predictions: Boolean = false,
    val odds: Boolean = false
)

data class Fixtures(
    val events: Boolean = false,
    val lineups: Boolean = false,
    val statistics_fixtures: Boolean = false,
    val statistics_players: Boolean = false
)