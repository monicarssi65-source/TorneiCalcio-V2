package com.torneicalcio.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TournamentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(t: Tournament): Long
    @Update suspend fun update(t: Tournament)
    @Delete suspend fun delete(t: Tournament)
    @Query("SELECT * FROM tournaments ORDER BY createdAt DESC") fun getAll(): LiveData<List<Tournament>>
    @Query("SELECT * FROM tournaments WHERE id = :id") fun getById(id: Long): LiveData<Tournament?>
}
