package com.torneicalcio.app.utils

object ColorHelper {
    val COLORS = listOf(
        "Rosso" to "#C62828",
        "Blu" to "#1565C0",
        "Verde" to "#2E7D32",
        "Giallo" to "#F9A825",
        "Arancione" to "#E65100",
        "Viola" to "#6A1B9A",
        "Rosa" to "#AD1457",
        "Azzurro" to "#0277BD",
        "Nero" to "#212121",
        "Bianco" to "#FAFAFA",
        "Grigio" to "#546E7A",
        "Marrone" to "#4E342E"
    )

    fun getColorNames() = COLORS.map { it.first }.toTypedArray()
    fun getHexForName(name: String) = COLORS.find { it.first == name }?.second ?: "#C62828"
    fun getNameForHex(hex: String) = COLORS.find { it.second.equals(hex, ignoreCase = true) }?.first ?: "Rosso"
}
