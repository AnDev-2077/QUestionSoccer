package com.devapps.questionsoccer.items

data class StatisticsResponse(
    val get: String,
    val parameters: sParameters,
    val errors: Any,
    val results: Int,
    val paging: sPaging,
    val response: StaticResponse
)

data class sParameters(
    val league: String,
    val team: String,
    val season: String
)

data class sPaging(
    val current: Int,
    val total: Int
)

data class StaticResponse(
    val league: sLeague,
    val team: sTeam,
    val form: String,
    val fixtures: sFixtures,
    val goals: Goals,
    val biggest: Biggest,
    val clean_sheet: CleanSheet,
    val failed_to_score: FailedToScore,
    val penalty: Penalty,
    val lineups: List<Formation>,
    val cards: Cards
)

data class sLeague(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String,
    val season: Int
)

data class sTeam(
    val id: Int,
    val name: String,
    val logo: String
)

data class sFixtures(
    val played: Played,
    val wins: Wins,
    val draws: Draws,
    val loses: Loses
)

data class Played(
    val home: Int,
    val away: Int,
    val total: Int
)

data class Wins(
    val home: Int,
    val away: Int,
    val total: Int
)

data class Draws(
    val home: Int,
    val away: Int,
    val total: Int
)

data class Loses(
    val home: Int,
    val away: Int,
    val total: Int
)

data class Goals(
    val `for`: For,
    val against: Against
)

data class For(
    val total: Total,
    val average: Average,
    val minute: Minute
)

data class Total(
    val home: Int,
    val away: Int,
    val total: Int
)

data class Average(
    val home: String,
    val away: String,
    val total: String
)

data class Minute(
    val `0-15`: TotalPercentage,
    val `16-30`: TotalPercentage,
    val `31-45`: TotalPercentage,
    val `46-60`: TotalPercentage,
    val `61-75`: TotalPercentage,
    val `76-90`: TotalPercentage,
    val `91-105`: TotalPercentage,
    val `106-120`: TotalPercentage?
)

data class TotalPercentage(
    val total: Int?,
    val percentage: String?
)

data class Against(
    val total: TotalX,
    val average: AverageX,
    val minute: MinuteX
)

data class TotalX(
    val home: Int,
    val away: Int,
    val total: Int
)

data class AverageX(
    val home: String,
    val away: String,
    val total: String
)

data class MinuteX(
    val `0-15`: TotalPercentage,
    val `16-30`: TotalPercentage,
    val `31-45`: TotalPercentage,
    val `46-60`: TotalPercentage,
    val `61-75`: TotalPercentage,
    val `76-90`: TotalPercentage,
    val `91-105`: TotalPercentage,
    val `106-120`: TotalPercentage?
)

data class Biggest(
    val streak: Streak,
    val wins: WinsX,
    val loses: LosesX,
    val goals: GoalsX
)

data class Streak(
    val wins: Int,
    val draws: Int,
    val loses: Int
)

data class WinsX(
    val home: String,
    val away: String
)

data class LosesX(
    val home: String,
    val away: String
)

data class GoalsX(
    val `for`: ForX,
    val against: AgainstX
)

data class ForX(
    val home: Int,
    val away: Int
)

data class AgainstX(
    val home: Int,
    val away: Int
)

data class CleanSheet(
    val home: Int,
    val away: Int,
    val total: Int
)

data class FailedToScore(
    val home: Int,
    val away: Int,
    val total: Int
)

data class Penalty(
    val scored: Scored,
    val missed: Missed,
    val total: Int
)

data class Scored(
    val total: Int,
    val percentage: String
)

data class Missed(
    val total: Int,
    val percentage: String
)

data class Formation(
    val formation: String,
    val played: Int
)

data class Cards(
    val yellow: Yellow,
    val red: Red
)

data class Yellow(
    val `0-15`: TotalPercentage,
    val `16-30`: TotalPercentage,
    val `31-45`: TotalPercentage,
    val `46-60`: TotalPercentage,
    val `61-75`: TotalPercentage,
    val `76-90`: TotalPercentage,
    val `91-105`: TotalPercentage,
    val `106-120`: TotalPercentage?,
    val empty: TotalPercentage
)

data class Red(
    val `0-15`: TotalPercentage?,
    val `16-30`: TotalPercentage?,
    val `31-45`: TotalPercentage?,
    val `46-60`: TotalPercentage?,
    val `61-75`: TotalPercentage?,
    val `76-90`: TotalPercentage?,
    val `91-105`: TotalPercentage?,
    val `106-120`: TotalPercentage?
)