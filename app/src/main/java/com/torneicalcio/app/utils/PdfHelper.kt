package com.torneicalcio.app.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.torneicalcio.app.data.Match
import com.torneicalcio.app.data.StandingEntry
import com.torneicalcio.app.data.Tournament
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfHelper {

    private val RED = BaseColor(198, 40, 40)
    private val YELLOW = BaseColor(249, 168, 37)
    private val LIGHT_GRAY = BaseColor(245, 245, 245)

    fun generateTournamentPdf(
        context: Context,
        tournament: Tournament,
        matches: List<Match>,
        standings: Map<String, List<StandingEntry>>
    ): File {
        val file = File(context.cacheDir, "${tournament.name.replace(" ", "_")}_torneo.pdf")
        val doc = Document(PageSize.A4, 36f, 36f, 54f, 36f)
        PdfWriter.getInstance(doc, FileOutputStream(file))
        doc.open()

        // Title
        val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20f, RED)
        val subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12f, BaseColor.DARK_GRAY)
        val headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, BaseColor.WHITE)
        val bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10f, BaseColor.BLACK)
        val boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, BaseColor.BLACK)

        doc.add(Paragraph("⚽ ${tournament.name}", titleFont).apply { alignment = Element.ALIGN_CENTER })
        doc.add(Paragraph("${tournament.society} • ${tournament.category} • ${tournament.location}", subtitleFont).apply { alignment = Element.ALIGN_CENTER })
        doc.add(Paragraph("Data: ${tournament.startDate}  |  Generato: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY).format(Date())}", subtitleFont).apply { alignment = Element.ALIGN_CENTER })
        doc.add(Chunk.NEWLINE)

        // Standings per group
        if (standings.isNotEmpty()) {
            doc.add(Paragraph("CLASSIFICHE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f, RED)))
            doc.add(Chunk.NEWLINE)

            standings.forEach { (groupName, entries) ->
                doc.add(Paragraph("Girone: $groupName", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, BaseColor.DARK_GRAY)))

                val table = PdfPTable(8).apply { widthPercentage = 100f; setWidths(floatArrayOf(3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)) }
                listOf("Squadra", "Pt", "G", "V", "N", "P", "GF", "GS").forEach { h ->
                    table.addCell(PdfPCell(Phrase(h, headerFont)).apply {
                        backgroundColor = RED; horizontalAlignment = Element.ALIGN_CENTER; paddingBottom = 6f
                    })
                }
                entries.forEachIndexed { i, e ->
                    val bg = if (i % 2 == 0) BaseColor.WHITE else LIGHT_GRAY
                    listOf(e.teamName, e.points.toString(), e.played.toString(), e.won.toString(),
                        e.drawn.toString(), e.lost.toString(), e.goalsFor.toString(), e.goalsAgainst.toString())
                        .forEachIndexed { j, v ->
                            table.addCell(PdfPCell(Phrase(v, if (j == 0) boldFont else bodyFont)).apply {
                                backgroundColor = bg
                                horizontalAlignment = if (j == 0) Element.ALIGN_LEFT else Element.ALIGN_CENTER
                                paddingBottom = 4f; paddingLeft = 4f
                            })
                        }
                }
                doc.add(table)
                doc.add(Chunk.NEWLINE)
            }
        }

        // Matches by group
        val groupMatches = matches.filter { !it.isFinal }
        if (groupMatches.isNotEmpty()) {
            doc.add(Paragraph("CALENDARIO PARTITE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f, RED)))
            doc.add(Chunk.NEWLINE)

            groupMatches.groupBy { it.groupName }.forEach { (groupName, gMatches) ->
                doc.add(Paragraph("Girone: $groupName", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, BaseColor.DARK_GRAY)))
                val table = PdfPTable(4).apply { widthPercentage = 100f; setWidths(floatArrayOf(3f, 3f, 1.5f, 2f)) }
                listOf("Casa", "Ospite", "Risultato", "Campo/Ora").forEach { h ->
                    table.addCell(PdfPCell(Phrase(h, headerFont)).apply {
                        backgroundColor = RED; horizontalAlignment = Element.ALIGN_CENTER; paddingBottom = 6f
                    })
                }
                gMatches.forEachIndexed { i, m ->
                    val bg = if (i % 2 == 0) BaseColor.WHITE else LIGHT_GRAY
                    val result = if (m.isPlayed) "${m.homeScore} - ${m.awayScore}" else "- vs -"
                    val info = buildString {
                        if (m.field.isNotEmpty()) append(m.field)
                        if (m.matchTime.isNotEmpty()) append(" ${m.matchTime}")
                    }
                    listOf(m.homeName, m.awayName, result, info).forEachIndexed { j, v ->
                        table.addCell(PdfPCell(Phrase(v, bodyFont)).apply {
                            backgroundColor = bg
                            horizontalAlignment = if (j == 2) Element.ALIGN_CENTER else Element.ALIGN_LEFT
                            paddingBottom = 4f; paddingLeft = 4f
                        })
                    }
                }
                doc.add(table)
                doc.add(Chunk.NEWLINE)
            }
        }

        // Finals
        val finalMatches = matches.filter { it.isFinal }
        if (finalMatches.isNotEmpty()) {
            doc.add(Paragraph("FASI FINALI", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f, RED)))
            doc.add(Chunk.NEWLINE)
            val table = PdfPTable(4).apply { widthPercentage = 100f; setWidths(floatArrayOf(2f, 3f, 3f, 1.5f)) }
            listOf("Fase", "Casa", "Ospite", "Risultato").forEach { h ->
                table.addCell(PdfPCell(Phrase(h, headerFont)).apply {
                    backgroundColor = RED; horizontalAlignment = Element.ALIGN_CENTER; paddingBottom = 6f
                })
            }
            finalMatches.forEachIndexed { i, m ->
                val bg = if (i % 2 == 0) BaseColor.WHITE else LIGHT_GRAY
                val result = if (m.isPlayed) "${m.homeScore} - ${m.awayScore}" else "- vs -"
                listOf(m.finalStage, m.homeName, m.awayName, result).forEachIndexed { j, v ->
                    table.addCell(PdfPCell(Phrase(v, if (j == 0) boldFont else bodyFont)).apply {
                        backgroundColor = bg
                        horizontalAlignment = if (j == 3) Element.ALIGN_CENTER else Element.ALIGN_LEFT
                        paddingBottom = 4f; paddingLeft = 4f
                    })
                }
            }
            doc.add(table)
        }

        doc.close()
        return file
    }

    fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Condividi PDF"))
    }

    fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Condividi su..."))
    }

    fun buildShareText(tournament: Tournament, matches: List<Match>, standings: Map<String, List<StandingEntry>>): String {
        val sb = StringBuilder()
        sb.appendLine("⚽ *${tournament.name}*")
        sb.appendLine("🏢 ${tournament.society} • ${tournament.category}")
        sb.appendLine("📍 ${tournament.location} • ${tournament.startDate}")
        sb.appendLine()

        standings.forEach { (group, entries) ->
            sb.appendLine("📊 *Classifica Girone $group*")
            entries.forEachIndexed { i, e ->
                sb.appendLine("${i+1}. ${e.teamName} — ${e.points}pt (${e.won}V ${e.drawn}N ${e.lost}P) GF:${e.goalsFor} GS:${e.goalsAgainst}")
            }
            sb.appendLine()
        }

        val finalMatches = matches.filter { it.isFinal && it.isPlayed }
        if (finalMatches.isNotEmpty()) {
            sb.appendLine("🏆 *Risultati Finali*")
            finalMatches.forEach { m ->
                sb.appendLine("${m.finalStage}: ${m.homeName} ${m.homeScore}-${m.awayScore} ${m.awayName}")
            }
        }
        return sb.toString()
    }
}
