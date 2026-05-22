package com.torneicalcio.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.torneicalcio.app.R
import com.torneicalcio.app.data.Match
import com.torneicalcio.app.data.Team
import com.torneicalcio.app.data.Tournament
import com.torneicalcio.app.databinding.ActivityTournamentDetailBinding
import com.torneicalcio.app.utils.PdfHelper
import com.torneicalcio.app.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class TournamentDetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityTournamentDetailBinding
    private val vm: MainViewModel by viewModels()
    private var tid: Long = 0
    private var currentTournament: Tournament? = null
    private var currentTeams: List<Team> = emptyList()
    private var currentMatches: List<Match> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTournamentDetailBinding.inflate(layoutInflater)
        setContentView(b.root)
        tid = intent.getLongExtra("TID", 0)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        vm.getTournament(tid).observe(this) { t ->
            t?.let {
                currentTournament = it
                supportActionBar?.title = it.name
            }
        }

        setupTeamsTab()
        setupMatchesTab()

        b.btnTabTeams.setOnClickListener { showTab("teams") }
        b.btnTabMatches.setOnClickListener { showTab("matches") }
        b.btnTabStandings.setOnClickListener { showTab("standings") }

        b.fabTeam.setOnClickListener {
            AddTeamDialog(tid) { team -> vm.insertTeam(team) }.show(supportFragmentManager, "addTeam")
        }

        b.btnGenerateCalendar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Genera Calendario")
                .setMessage("Verrà generato automaticamente il calendario round-robin per ogni girone. Partite esistenti dei gironi verranno eliminate.")
                .setPositiveButton("Genera") { _, _ ->
                    vm.generateCalendar(tid, currentTeams)
                    showTab("matches")
                }
                .setNegativeButton("Annulla", null).show()
        }

        b.btnGoFinals.setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java).putExtra("TID", tid))
        }

        b.btnGoScorers.setOnClickListener {
            startActivity(Intent(this, ScorersActivity::class.java).putExtra("TID", tid))
        }

        b.btnShareText.setOnClickListener {
            val t = currentTournament ?: return@setOnClickListener
            val standings = vm.computeStandings(currentMatches)
            val text = PdfHelper.buildShareText(t, currentMatches, standings)
            PdfHelper.shareText(this, text)
        }

        b.btnExportPdf.setOnClickListener {
            val t = currentTournament ?: return@setOnClickListener
            lifecycleScope.launch {
                val standings = vm.computeStandings(currentMatches)
                val file = PdfHelper.generateTournamentPdf(this@TournamentDetailActivity, t, currentMatches, standings)
                PdfHelper.sharePdf(this@TournamentDetailActivity, file)
            }
        }

        showTab("teams")
    }

    private fun setupTeamsTab() {
        val adapter = TeamAdapter { team ->
            AlertDialog.Builder(this)
                .setTitle("Eliminare ${team.name}?")
                .setPositiveButton("Elimina") { _, _ -> vm.deleteTeam(team) }
                .setNegativeButton("Annulla", null).show()
        }
        b.rvTeams.layoutManager = LinearLayoutManager(this)
        b.rvTeams.adapter = adapter

        vm.getTeams(tid).observe(this) { list ->
            currentTeams = list
            adapter.submitList(list)
            b.tvTeamsEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupMatchesTab() {
        val adapter = MatchAdapter { match ->
            EditMatchDialog(match) { updated -> vm.updateMatch(updated) }.show(supportFragmentManager, "edit")
        }
        b.rvMatches.layoutManager = LinearLayoutManager(this)
        b.rvMatches.adapter = adapter

        vm.getMatches(tid).observe(this) { list ->
            currentMatches = list
            val groupMatches = list.filter { !it.isFinal }
            adapter.submitList(groupMatches)
            b.tvMatchesEmpty.visibility = if (groupMatches.isEmpty()) View.VISIBLE else View.GONE
            updateStandingsView(groupMatches)
        }
    }

    private fun updateStandingsView(matches: List<Match>) {
        val standings = vm.computeStandings(matches)
        if (standings.isEmpty()) {
            b.tvStandings.text = "Nessuna partita giocata ancora."
            return
        }
        val sb = StringBuilder()
        standings.forEach { (group, entries) ->
            sb.appendLine("── Girone $group ──")
            sb.appendLine(String.format("%-18s %2s %2s %2s %2s %2s %2s %2s", "Squadra", "Pt", "G", "V", "N", "P", "GF", "GS"))
            entries.forEachIndexed { i, e ->
                sb.appendLine(String.format("%-18s %2d %2d %2d %2d %2d %2d %2d",
                    "${i+1}.${e.teamName}".take(18), e.points, e.played, e.won, e.drawn, e.lost, e.goalsFor, e.goalsAgainst))
            }
            sb.appendLine()

            // Qualified teams
            val t = currentTournament
            if (t != null && t.qualifiedPerGroup > 0) {
                val qualified = entries.take(t.qualifiedPerGroup)
                sb.appendLine("✅ Qualificate: ${qualified.joinToString(", ") { it.teamName }}")
                sb.appendLine()
            }
        }
        b.tvStandings.text = sb.toString()
    }

    private fun showTab(tab: String) {
        b.panelTeams.visibility = if (tab == "teams") View.VISIBLE else View.GONE
        b.panelMatches.visibility = if (tab == "matches") View.VISIBLE else View.GONE
        b.panelStandings.visibility = if (tab == "standings") View.VISIBLE else View.GONE
        b.fabTeam.visibility = if (tab == "teams") View.VISIBLE else View.GONE
        b.btnGenerateCalendar.visibility = if (tab == "matches") View.VISIBLE else View.GONE

        val active = getColor(R.color.red)
        val inactive = getColor(R.color.gray)
        b.btnTabTeams.setTextColor(if (tab == "teams") active else inactive)
        b.btnTabMatches.setTextColor(if (tab == "matches") active else inactive)
        b.btnTabStandings.setTextColor(if (tab == "standings") active else inactive)
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
