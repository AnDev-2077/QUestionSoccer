package com.devapps.questionsoccer.items

data class LeagueResponse(
    val get: String,
    val parameters: List<Any>,
    val errors: List<Any>,
    val results: Int,
    val paging: lPaging,
    val response: List<ResponseItem>
)

data class lPaging(
    val current: Int,
    val total: Int
)

data class LeagueResponseItem(
    val league: lLeague,
    val country: Country,
    val seasons: List<Season>
)

data class lLeague(
    val id: Int,
    val name: String,
    val type: String,
    val logo: String
)

data class Country(
    val name: String,
    val code: Any?,
    val flag: Any?
)

data class Season(
    val year: Int,
    val start: String,
    val end: String,
    val current: Boolean,
    val coverage: Coverage
)

data class Coverage(
    val fixtures: Fixtures,
    val standings: Boolean,
    val players: Boolean,
    val top_scorers: Boolean,
    val top_assists: Boolean,
    val top_cards: Boolean,
    val injuries: Boolean,
    val predictions: Boolean,
    val odds: Boolean
)

data class Fixtures(
    val events: Boolean,
    val lineups: Boolean,
    val statistics_fixtures: Boolean,
    val statistics_players: Boolean
)