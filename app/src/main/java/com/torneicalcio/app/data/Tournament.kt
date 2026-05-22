package com.torneicalcio.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val society: String,
    val category: String,
    val location: String,
    val startDate: String,
    val tournamentType: String = "gironi", // gironi, eliminazione, misto, gironi_italiani
    val numGroups: Int = 3,
    val teamsPerGroup: Int = 3,
    val qualifiedPerGroup: Int = 1, // quante squadre passano per girone
    val qualifyByCriteria: String = "punti_diff_gol", // punti_diff_gol, punti_gol_segnati
    val fields: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
