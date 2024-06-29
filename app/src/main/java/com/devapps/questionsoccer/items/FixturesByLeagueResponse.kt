package com.devapps.questionsoccer.items

import java.io.Serializable

data class FixturesByLeagueResponse(
    val get: String,
    val parameters: FixtureParameters,
    val errors: Any,
    val results: Int,
    val paging: FixturePaging,
    val response: List<fixtureResponse>
): Serializable

data class FixtureParameters(
    val date: String ,
    val league: String ,
    val season: String
): Serializable

data class FixturePaging(
    val current: Int,
    val total: Int
): Serializable

data class fixtureResponse(
    val fixture: Fixture = Fixture(),
    val league: League = League(),
    val teams: Teams = Teams(),
    val goals: SGoals = SGoals(),
    val score: Score = Score()
) : Serializable

data class Fixture(
    val id: Int = 0,
    val referee: Any? = null,
    val timezone: String = "",
    val date: String = "",
    val timestamp: Int = 0,
    val periods: Periods = Periods(),
    val venue: FixtureVenue = FixtureVenue(),
    val status: Status = Status()
) : Serializable

data class Periods(
    val first: Any? = null,
    val second: Any? = null
) : Serializable

data class FixtureVenue(
    val id: Int = 0,
    val name: String = "",
    val city: String = ""
) : Serializable

data class Status(
    val long: String = "",
    val short: String = "",
    val elapsed: Any? = null
) : Serializable

data class League(
    val id: Int = 0,
    val name: String = "",
    val country: String = "",
    val logo: String = "",
    val flag: String = "",
    val season: Int = 0,
    val round: String = ""
) : Serializable

data class Teams(
    val home: Home = Home(),
    val away: Away = Away()
) : Serializable

data class Home(
    val id: Int = 0,
    val name: String = "",
    val logo: String = "",
    val winner: Any? = null
) : Serializable

data class Away(
    val id: Int = 0,
    val name: String = "",
    val logo: String = "",
    val winner: Any? = null
) : Serializable

data class SGoals(
    val home: Any? = null,
    val away: Any? = null
) : Serializable

data class Score(
    val halftime: Halftime = Halftime(),
    val fulltime: Fulltime = Fulltime(),
    val extratime: Extratime = Extratime(),
    val penalty: SPenalty = SPenalty()
) : Serializable

data class Halftime(
    val home: Any? = null,
    val away: Any? = null
) : Serializable

data class Fulltime(
    val home: Any? = null,
    val away: Any? = null
) : Serializable

data class Extratime(
    val home: Any? = null,
    val away: Any? = null
) : Serializable

data class SPenalty(
    val home: Any? = null,
    val away: Any? = null
) : Serializable
