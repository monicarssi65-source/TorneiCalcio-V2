package com.torneicalcio.app.data

data class StandingEntry(
    val teamName: String,
    val groupName: String,
    var played: Int = 0,
    var won: Int = 0,
    var drawn: Int = 0,
    var lost: Int = 0,
    var goalsFor: Int = 0,
    var goalsAgainst: Int = 0
) {
    val points get() = won * 3 + drawn
    val goalDiff get() = goalsFor - goalsAgainst
}

data class ScorerEntry(
    val playerName: String,
    val teamName: String,
    val goals: Int
)
