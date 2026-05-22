package com.torneicalcio.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.torneicalcio.app.data.Match
import com.torneicalcio.app.databinding.DialogEditMatchBinding

class EditMatchDialog(private val match: Match, private val onSave: (Match) -> Unit) : DialogFragment() {
    private var _b: DialogEditMatchBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = DialogEditMatchBinding.inflate(i, c, false); return b.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        b.tvTitle.text = "${match.homeName} vs ${match.awayName}"
        b.tvHomeLabel.text = "Gol ${match.homeName}"
        b.tvAwayLabel.text = "Gol ${match.awayName}"
        b.tvHomeScorersLabel.text = "Marcatori ${match.homeName} (virgola)"
        b.tvAwayScorersLabel.text = "Marcatori ${match.awayName} (virgola)"
        b.etField.setText(match.field)
        b.etTime.setText(match.matchTime)
        b.etHomeScore.setText(match.homeScore?.toString() ?: "")
        b.etAwayScore.setText(match.awayScore?.toString() ?: "")
        b.etHomeScorers.setText(match.homeScorers)
        b.etAwayScorers.setText(match.awayScorers)
        b.btnSave.setOnClickListener { save() }
        b.btnCancel.setOnClickListener { dismiss() }
    }

    private fun save() {
        val hs = b.etHomeScore.text.toString().toIntOrNull()
        val as_ = b.etAwayScore.text.toString().toIntOrNull()
        onSave(match.copy(
            field = b.etField.text.toString().trim(),
            matchTime = b.etTime.text.toString().trim(),
            homeScore = hs, awayScore = as_,
            homeScorers = b.etHomeScorers.text.toString().trim(),
            awayScorers = b.etAwayScorers.text.toString().trim(),
            isPlayed = hs != null && as_ != null
        ))
        dismiss()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
