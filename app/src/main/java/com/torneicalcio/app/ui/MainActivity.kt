package com.torneicalcio.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.torneicalcio.app.databinding.ActivityMainBinding
import com.torneicalcio.app.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)

        val adapter = TournamentAdapter(
            onClick = { t -> startActivity(Intent(this, TournamentDetailActivity::class.java).putExtra("TID", t.id)) },
            onDelete = { t ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminare ${t.name}?")
                    .setMessage("Tutti i dati del torneo verranno eliminati definitivamente.")
                    .setPositiveButton("Elimina") { _, _ -> vm.deleteTournament(t) }
                    .setNegativeButton("Annulla", null).show()
            }
        )
        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter

        vm.allTournaments.observe(this) { list ->
            adapter.submitList(list)
            b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        b.fab.setOnClickListener {
            AddTournamentDialog { t -> vm.insertTournament(t) }.show(supportFragmentManager, "add")
        }
    }
}
