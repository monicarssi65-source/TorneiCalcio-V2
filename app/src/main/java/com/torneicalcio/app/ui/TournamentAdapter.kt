package com.torneicalcio.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.torneicalcio.app.data.Tournament
import com.torneicalcio.app.databinding.ItemTournamentBinding

class TournamentAdapter(
    private val onClick: (Tournament) -> Unit,
    private val onDelete: (Tournament) -> Unit
) : ListAdapter<Tournament, TournamentAdapter.VH>(Diff()) {
    override fun onCreateViewHolder(p: ViewGroup, v: Int) = VH(ItemTournamentBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(getItem(pos))

    inner class VH(private val b: ItemTournamentBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(t: Tournament) {
            b.tvName.text = t.name
            b.tvInfo.text = "${t.society} • ${t.category}"
            b.tvLocation.text = "📍 ${t.location}  📅 ${t.startDate}"
            b.tvType.text = when (t.tournamentType) {
                "eliminazione" -> "🏆 Eliminazione Diretta"
                "misto" -> "🔀 Misto"
                "gironi_italiani" -> "🇮🇹 Gironi Italiani"
                else -> "⚽ Gironi"
            }
            b.root.setOnClickListener { onClick(t) }
            b.btnDelete.setOnClickListener { onDelete(t) }
        }
    }

    class Diff : DiffUtil.ItemCallback<Tournament>() {
        override fun areItemsTheSame(o: Tournament, n: Tournament) = o.id == n.id
        override fun areContentsTheSame(o: Tournament, n: Tournament) = o == n
    }
}
