package com.torneicalcio.app.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.torneicalcio.app.databinding.ActivityMatchesBinding
import com.torneicalcio.app.viewmodel.MainViewModel

class MatchesActivity : AppCompatActivity() {
    private lateinit var b: ActivityMatchesBinding
    private val vm: MainViewModel by viewModels()
    private var tid: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMatchesBinding.inflate(layoutInflater)
        setContentView(b.root)
        tid = intent.getLongExtra("TID", 0)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "🏆 Fasi Finali"

        val adapter = MatchAdapter { match ->
            EditMatchDialog(match) { updated -> vm.updateMatch(updated) }.show(supportFragmentManager, "edit")
        }
        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter

        vm.getMatches(tid).observe(this) { list ->
            val finals = list.filter { it.isFinal }
            adapter.submitList(finals)
            b.tvEmpty.visibility = if (finals.isEmpty()) View.VISIBLE else View.GONE
        }

        b.btnAddFinal.setOnClickListener {
            AddFinalMatchDialog(tid) { match -> vm.insertMatch(match) }.show(supportFragmentManager, "addFinal")
        }

        b.btnDeleteFinals.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminare tutte le finali?")
                .setPositiveButton("Elimina") { _, _ -> vm.deleteFinals(tid) }
                .setNegativeButton("Annulla", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
