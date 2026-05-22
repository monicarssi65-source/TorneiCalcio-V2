package com.torneicalcio.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.torneicalcio.app.data.Match
import com.torneicalcio.app.databinding.DialogAddFinalBinding

class AddFinalMatchDialog(private val tournamentId: Long, private val onSave: (Match) -> Unit) : DialogFragment() {
    private var _b: DialogAddFinalBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = DialogAddFinalBinding.inflate(i, c, false); return b.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        b.btnSave.setOnClickListener { save() }
        b.btnCancel.setOnClickListener { dismiss() }
    }

    private fun save() {
        val home = b.etHome.text.toString().trim()
        val away = b.etAway.text.toString().trim()
        if (home.isEmpty() || away.isEmpty()) return
        onSave(Match(
            tournamentId = tournamentId, homeName = home, awayName = away,
            field = b.etField.text.toString().trim(),
            matchTime = b.etTime.text.toString().trim(),
            finalStage = b.etStage.text.toString().trim().ifEmpty { "Finale" },
            isFinal = true
        ))
        dismiss()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
