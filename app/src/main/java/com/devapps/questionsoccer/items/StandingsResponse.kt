package com.devapps.questionsoccer.items

data class StandingsResponse(
    val get: String,
    val parameters: StandingsParameters,
    val errors: Any,
    val results: Int,
    val paging: StandingsPaging,
    val response: List<itemStandingsResponse>
)

data class StandingsParameters(
    val league: String,
    val season: String
)

data class StandingsPaging(
    val current: Int,
    val total: Int
)

data class itemStandingsResponse(
    val league: StandingsLeague
)

data class StandingsLeague(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String,
    val season: Int,
    val standings: List<List<Standing>>
)

data class Standing(
    val rank: Int,
    val team: StandingsTeam,
    val points: Int,
    val goalsDiff: Int,
    val group: String,
    val form: String,
    val status: String,
    val description: String?,
    val all: Record,
    val home: Record,
    val away: Record,
    val update: String
)

data class StandingsTeam(
    val id: Int,
    val name: String,
    val logo: String
)

data class Record(
    val played: Int,
    val win: Int,
    val draw: Int,
    val lose: Int,
    val goals: StandingsGoals
)

data class StandingsGoals(
    val `for`: Int,
    val against: Int
)

