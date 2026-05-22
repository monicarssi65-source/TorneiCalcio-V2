package com.torneicalcio.app.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.torneicalcio.app.data.Team
import com.torneicalcio.app.databinding.ItemTeamBinding

class TeamAdapter(private val onDelete: (Team) -> Unit) : ListAdapter<Team, TeamAdapter.VH>(Diff()) {
    override fun onCreateViewHolder(p: ViewGroup, v: Int) = VH(ItemTeamBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(getItem(pos))

    inner class VH(private val b: ItemTeamBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(t: Team) {
            b.tvName.text = t.name
            b.tvGroup.text = if (t.groupName.isNotEmpty()) "Girone: ${t.groupName}" else "Nessun girone"
            b.tvColor.text = "🎨 ${t.colorName}"
            try { b.colorBar.setBackgroundColor(Color.parseColor(t.colorHex)) }
            catch (e: Exception) { b.colorBar.setBackgroundColor(Color.parseColor("#C62828")) }
            b.btnDelete.setOnClickListener { onDelete(t) }
        }
    }

    class Diff : DiffUtil.ItemCallback<Team>() {
        override fun areItemsTheSame(o: Team, n: Team) = o.id == n.id
        override fun areContentsTheSame(o: Team, n: Team) = o == n
    }
}
