package com.devapps.questionsoccer.items

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class LineupResponse(
    val get: String,
    val parameters: LineupParameters,
    val errors: Any,
    val results: Int,
    val paging: LineupPaging,
    val response: List<LineupResponseItem>
)

data class LineupParameters(
    val fixture: String
)

data class LineupPaging(
    val current: Int,
    val total: Int
)
@Parcelize
data class LineupResponseItem(
    val team: LineupTeam,
    val formation: String,
    val startXI: List<Player>,
    val substitutes: List<Player>,
    val coach: Coach
):Parcelable
@Parcelize
data class LineupTeam(
    val id: Int,
    val name: String,
    val logo: String,
    val colors: Colors
):Parcelable
@Parcelize
data class Colors(
    val player: PlayerColor,
    val goalkeeper: GoalkeeperColor
):Parcelable
@Parcelize
data class PlayerColor(
    val primary: String,
    val number: String,
    val border: String
):Parcelable
@Parcelize
data class GoalkeeperColor(
    val primary: String,
    val number: String,
    val border: String
):Parcelable
@Parcelize
data class Player(
    val player: PlayerDetails
):Parcelable
@Parcelize
data class PlayerDetails(
    val id: Int,
    val name: String,
    val number: Int,
    val pos: String,
    val grid: String?
):Parcelable
@Parcelize
data class Coach(
    val id: Int,
    val name: String,
    val photo: String
):Parcelable
