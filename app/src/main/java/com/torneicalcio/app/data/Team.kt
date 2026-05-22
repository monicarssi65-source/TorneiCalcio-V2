package com.torneicalcio.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "teams",
    foreignKeys = [ForeignKey(
        entity = Tournament::class,
        parentColumns = ["id"],
        childColumns = ["tournamentId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("tournamentId")]
)
data class Team(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tournamentId: Long,
    val name: String,
    val colorName: String = "Rosso", // nome colore: Rosso, Blu, Verde, Giallo, ecc.
    val colorHex: String = "#C62828",
    val groupName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
