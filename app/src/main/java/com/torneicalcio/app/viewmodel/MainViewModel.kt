package com.torneicalcio.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.torneicalcio.app.data.*
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getDatabase(app)
    private val tDao = db.tournamentDao()
    private val teamDao = db.teamDao()
    private val matchDao = db.matchDao()

    val allTournaments: LiveData<List<Tournament>> = tDao.getAll()

    fun getTournament(id: Long) = tDao.getById(id)
    fun getTeams(tid: Long) = teamDao.getByTournament(tid)
    fun getMatches(tid: Long) = matchDao.getByTournament(tid)

    fun insertTournament(t: Tournament) = viewModelScope.launch { tDao.insert(t) }
    fun updateTournament(t: Tournament) = viewModelScope.launch { tDao.update(t) }
    fun deleteTournament(t: Tournament) = viewModelScope.launch { tDao.delete(t) }

    fun insertTeam(t: Team) = viewModelScope.launch { teamDao.insert(t) }
    fun deleteTeam(t: Team) = viewModelScope.launch { teamDao.delete(t) }

    fun insertMatch(m: Match) = viewModelScope.launch { matchDao.insert(m) }
    fun updateMatch(m: Match) = viewModelScope.launch { matchDao.update(m) }
    fun deleteMatch(m: Match) = viewModelScope.launch { matchDao.delete(m) }
    fun deleteFinals(tid: Long) = viewModelScope.launch { matchDao.deleteFinalMatches(tid) }

    fun generateCalendar(tournamentId: Long, teams: List<Team>) = viewModelScope.launch {
        matchDao.deleteGroupMatches(tournamentId)
        val groups = teams.groupBy { it.groupName }
        groups.forEach { (groupName, groupTeams) ->
            val n = groupTeams.size
            // Round-robin algorithm
            val list = groupTeams.toMutableList()
            if (n % 2 != 0) list.add(Team(tournamentId = tournamentId, name = "BYE"))
            val rounds = list.size - 1
            val half = list.size / 2
            for (round in 0 until rounds) {
                for (i in 0 until half) {
                    val home = list[i]
                    val away = list[list.size - 1 - i]
                    if (home.name != "BYE" && away.name != "BYE") {
                        matchDao.insert(Match(
                            tournamentId = tournamentId,
                            groupName = groupName,
                            homeName = home.name,
                            awayName = away.name,
                            roundNumber = round + 1
                        ))
                    }
                }
                // Rotate list keeping first element fixed
                val last = list.removeAt(list.size - 1)
                list.add(1, last)
            }
        }
    }

    fun computeStandings(matches: List<Match>): Map<String, List<StandingEntry>> {
        val played = matches.filter { !it.isFinal && it.isPlayed }
        val allTeams = matches.filter { !it.isFinal }
            .flatMap { listOf(it.homeName to it.groupName, it.awayName to it.groupName) }
            .distinctBy { it.first }

        val map = mutableMapOf<String, StandingEntry>()
        allTeams.forEach { (name, group) ->
            map[name] = StandingEntry(teamName = name, groupName = group)
        }

        played.forEach { m ->
            val h = map[m.homeName] ?: return@forEach
            val a = map[m.awayName] ?: return@forEach
            val hs = m.homeScore ?: 0
            val as_ = m.awayScore ?: 0
            h.played++; a.played++
            h.goalsFor += hs; h.goalsAgainst += as_
            a.goalsFor += as_; a.goalsAgainst += hs
            when {
                hs > as_ -> { h.won++; a.lost++ }
                hs < as_ -> { a.won++; h.lost++ }
                else -> { h.drawn++; a.drawn++ }
            }
        }

        return map.values.groupBy { it.groupName }.mapValues { (_, entries) ->
            entries.sortedWith(compareByDescending<StandingEntry> { it.points }
                .thenByDescending { it.goalDiff }
                .thenByDescending { it.goalsFor })
        }
    }

    fun computeScorers(matches: List<Match>): List<ScorerEntry> {
        val scorerMap = mutableMapOf<String, Pair<String, Int>>() // name -> (team, goals)
        matches.filter { it.isPlayed }.forEach { m ->
            m.homeScorers.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { name ->
                val cur = scorerMap.getOrDefault(name, m.homeName to 0)
                scorerMap[name] = cur.first to cur.second + 1
            }
            m.awayScorers.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { name ->
                val cur = scorerMap.getOrDefault(name, m.awayName to 0)
                scorerMap[name] = cur.first to cur.second + 1
            }
        }
        return scorerMap.entries
            .map { (name, pair) -> ScorerEntry(name, pair.first, pair.second) }
            .sortedByDescending { it.goals }
    }

    fun getQualifiedTeams(standings: Map<String, List<StandingEntry>>, qualifiedPerGroup: Int, criteria: String): List<StandingEntry> {
        val qualified = mutableListOf<StandingEntry>()
        standings.values.forEach { groupStandings ->
            val sorted = when (criteria) {
                "punti_gol_segnati" -> groupStandings.sortedWith(
                    compareByDescending<StandingEntry> { it.points }.thenByDescending { it.goalsFor })
                else -> groupStandings.sortedWith(
                    compareByDescending<StandingEntry> { it.points }
                        .thenByDescending { it.goalDiff }.thenByDescending { it.goalsFor })
            }
            qualified.addAll(sorted.take(qualifiedPerGroup))
        }
        return qualified
    }
}
