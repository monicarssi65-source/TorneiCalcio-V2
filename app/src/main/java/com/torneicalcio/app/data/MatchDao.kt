package com.torneicalcio.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(m: Match): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(matches: List<Match>)
    @Update suspend fun update(m: Match)
    @Delete suspend fun delete(m: Match)
    @Query("SELECT * FROM matches WHERE tournamentId = :tid ORDER BY isFinal, groupName, roundNumber, createdAt") fun getByTournament(tid: Long): LiveData<List<Match>>
    @Query("SELECT * FROM matches WHERE tournamentId = :tid ORDER BY isFinal, groupName, roundNumber, createdAt") suspend fun getByTournamentSync(tid: Long): List<Match>
    @Query("DELETE FROM matches WHERE tournamentId = :tid AND isFinal = 0") suspend fun deleteGroupMatches(tid: Long)
    @Query("DELETE FROM matches WHERE tournamentId = :tid AND isFinal = 1") suspend fun deleteFinalMatches(tid: Long)
}
