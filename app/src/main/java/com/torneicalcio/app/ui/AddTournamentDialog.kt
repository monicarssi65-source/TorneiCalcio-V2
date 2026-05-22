package com.torneicalcio.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.torneicalcio.app.data.Tournament
import com.torneicalcio.app.databinding.DialogAddTournamentBinding

class AddTournamentDialog(private val onSave: (Tournament) -> Unit) : DialogFragment() {
    private var _b: DialogAddTournamentBinding? = null
    private val b get() = _b!!

    private val typeValues = arrayOf("gironi", "gironi_italiani", "eliminazione", "misto")
    private val typeLabels = arrayOf("Gironi", "Gironi Italiani", "Eliminazione Diretta", "Misto (Gironi + Finale)")
    private val criteriaValues = arrayOf("punti_diff_gol", "punti_gol_segnati")
    private val criteriaLabels = arrayOf("Punti → Diff. Reti → Gol Segnati", "Punti → Gol Segnati")

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = DialogAddTournamentBinding.inflate(i, c, false); return b.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        b.spinnerType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeLabels).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        b.spinnerCriteria.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, criteriaLabels).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        b.btnSave.setOnClickListener { save() }
        b.btnCancel.setOnClickListener { dismiss() }
    }

    private fun save() {
        val name = b.etName.text.toString().trim()
        val society = b.etSociety.text.toString().trim()
        if (name.isEmpty()) { b.etName.error = "Obbligatorio"; return }
        if (society.isEmpty()) { b.etSociety.error = "Obbligatorio"; return }

        onSave(Tournament(
            name = name, society = society,
            category = b.etCategory.text.toString().trim(),
            location = b.etLocation.text.toString().trim(),
            startDate = b.etDate.text.toString().trim(),
            tournamentType = typeValues[b.spinnerType.selectedItemPosition],
            numGroups = b.etGroups.text.toString().toIntOrNull() ?: 3,
            teamsPerGroup = b.etTeamsPerGroup.text.toString().toIntOrNull() ?: 3,
            qualifiedPerGroup = b.etQualified.text.toString().toIntOrNull() ?: 1,
            qualifyByCriteria = criteriaValues[b.spinnerCriteria.selectedItemPosition],
            fields = b.etFields.text.toString().trim()
        ))
        dismiss()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
