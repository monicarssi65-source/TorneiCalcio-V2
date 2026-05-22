package com.torneicalcio.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.torneicalcio.app.R
import com.torneicalcio.app.data.ScorerEntry
import com.torneicalcio.app.databinding.ActivityScorersBinding
import com.torneicalcio.app.viewmodel.MainViewModel

class ScorersActivity : AppCompatActivity() {
    private lateinit var b: ActivityScorersBinding
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityScorersBinding.inflate(layoutInflater)
        setContentView(b.root)
        val tid = intent.getLongExtra("TID", 0)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "⚽ Classifica Cannonieri"

        val adapter = ScorersAdapter()
        b.rv.layoutManager = LinearLayoutManager(this)
        b.rv.adapter = adapter

        vm.getMatches(tid).observe(this) { matches ->
            val scorers = vm.computeScorers(matches)
            adapter.submitList(scorers)
            b.tvEmpty.visibility = if (scorers.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}

class ScorersAdapter : RecyclerView.Adapter<ScorersAdapter.VH>() {
    private var items = listOf<ScorerEntry>()

    fun submitList(list: List<ScorerEntry>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_scorer, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position], position + 1)
    override fun getItemCount() = items.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvPos: TextView = v.findViewById(R.id.tvPos)
        private val tvName: TextView = v.findViewById(R.id.tvName)
        private val tvTeam: TextView = v.findViewById(R.id.tvTeam)
        private val tvGoals: TextView = v.findViewById(R.id.tvGoals)

        fun bind(s: ScorerEntry, pos: Int) {
            val medal = when (pos) { 1 -> "🥇"; 2 -> "🥈"; 3 -> "🥉"; else -> "$pos." }
            tvPos.text = medal
            tvName.text = s.playerName
            tvTeam.text = s.teamName
            tvGoals.text = "${s.goals} ⚽"
        }
    }
}
