package com.torneicalcio.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.torneicalcio.app.data.Team
import com.torneicalcio.app.databinding.DialogAddTeamBinding
import com.torneicalcio.app.utils.ColorHelper

class AddTeamDialog(private val tournamentId: Long, private val onSave: (Team) -> Unit) : DialogFragment() {
    private var _b: DialogAddTeamBinding? = null
    private val b get() = _b!!
    private var selectedColorHex = "#C62828"

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = DialogAddTeamBinding.inflate(i, c, false); return b.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ColorHelper.getColorNames()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        b.spinnerColor.adapter = adapter
        b.spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                selectedColorHex = ColorHelper.COLORS[pos].second
                b.colorPreview.setBackgroundColor(Color.parseColor(selectedColorHex))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        b.colorPreview.setBackgroundColor(Color.parseColor(selectedColorHex))
        b.btnSave.setOnClickListener { save() }
        b.btnCancel.setOnClickListener { dismiss() }
    }

    private fun save() {
        val name = b.etName.text.toString().trim()
        if (name.isEmpty()) { b.etName.error = "Obbligatorio"; return }
        val colorName = ColorHelper.COLORS[b.spinnerColor.selectedItemPosition].first
        onSave(Team(
            tournamentId = tournamentId, name = name,
            groupName = b.etGroup.text.toString().trim(),
            colorName = colorName, colorHex = selectedColorHex
        ))
        dismiss()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
