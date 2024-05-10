package com.devapps.questionsoccer.items

data class FixturesByLeagueResponse(
    val get: String,
    val parameters: fixtureParameters,
    val errors: Any,
    val results: Int,
    val paging: fixturePaging,
    val response: List<fixtureResponse>
)
data class fixtureParameters(
    val date: String,
    val league: String,
    val season: String
)

data class fixturePaging(
    val current: Int,
    val total: Int
)

data class fixtureResponse(
    val fixture: Fixture,
    val league: League,
    val teams: Teams,
    val goals: Goals,
    val score: Score
)

data class Fixture(
    val id: Int,
    val referee: Any?,
    val timezone: String,
    val date: String,
    val timestamp: Int,
    val periods: Periods,
    val venue: fixtureVenue,
    val status: Status
)

data class Periods(
    val first: Any?,
    val second: Any?
)

data class fixtureVenue(
    val id: Int,
    val name: String,
    val city: String
)

data class Status(
    val long: String,
    val short: String,
    val elapsed: Any?
)

data class League(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String,
    val season: Int,
    val round: String
)

data class Teams(
    val home: Home,
    val away: Away
)

data class Home(
    val id: Int,
    val name: String,
    val logo: String,
    val winner: Any?
)

data class Away(
    val id: Int,
    val name: String,
    val logo: String,
    val winner: Any?
)

data class Goals(
    val home: Any?,
    val away: Any?
)

data class Score(
    val halftime: Halftime,
    val fulltime: Fulltime,
    val extratime: Extratime,
    val penalty: Penalty
)

data class Halftime(
    val home: Any?,
    val away: Any?
)

data class Fulltime(
    val home: Any?,
    val away: Any?
)

data class Extratime(
    val home: Any?,
    val away: Any?
)

data class Penalty(
    val home: Any?,
    val away: Any?
)
