package com.torneicalcio.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TeamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(t: Team): Long
    @Delete suspend fun delete(t: Team)
    @Query("SELECT * FROM teams WHERE tournamentId = :tid ORDER BY groupName, name") fun getByTournament(tid: Long): LiveData<List<Team>>
    @Query("SELECT * FROM teams WHERE tournamentId = :tid ORDER BY groupName, name") suspend fun getByTournamentSync(tid: Long): List<Team>
    @Query("DELETE FROM teams WHERE tournamentId = :tid") suspend fun deleteByTournament(tid: Long)
}
