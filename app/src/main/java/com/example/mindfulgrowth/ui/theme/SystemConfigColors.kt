package com.example.mindfulgrowth.ui.theme

object SystemConfigColors {
    // 1. PRIMARY BACKGROUND: "Deep Gunmetal"
    // A near-black cool grey. Zero distraction.
    const val PRIMARY_BG = 0xFF101214.toInt()
    const val STATUS_BAR_BG = 0xFF101214.toInt()

    // 2. SECONDARY SURFACES: "Dark Graphite"
    // Distinct enough to show cards, but barely.
    const val BOTTOM_NAV_BG = 0xFF1B1E21.toInt()
    const val TOGGLE_INACTIVE_BG = 0xFF1B1E21.toInt()
    const val GLASS_SECONDARY = 0xFF16191C.toInt()

    // 3. TEXT: "Platinum"
    // High contrast white-grey for perfect readability without the glare.
    const val TEXT_PRIMARY = 0xFFECEFF1.toInt()
    const val TEXT_ACCENT = 0xFFECEFF1.toInt()

    // Medium Grey for secondary info
    const val TEXT_SECONDARY = 0xFF90A4AE.toInt()
    const val BOTTOM_NAV_ICON_INACTIVE = 0xFF546E7A.toInt()

    // 4. PRIMARY ACCENT (Growth): "Steel Haze"
    // A very desaturated, light blue-grey. It glows like moonlight through fog.
    // Replaces the "Neon" colors with something much calmer.
    const val NEON_GREEN_ACCENT = 0xFFB0BEC5.toInt()
    const val GLASS_BORDER = 0x40B0BEC5.toInt()
    const val TOGGLE_TEXT_ACTIVE = 0xFF101214.toInt() // Dark text on light buttons

    // 5. SECONDARY ACCENT (Active): "Crisp Silver"
    // The "Active" state is simply brighter/whiter than the rest.
    const val ACCENT_RED_PRIMARY = 0xFFCFD8DC.toInt()
    const val TOGGLE_ACTIVE_BG = 0xFFCFD8DC.toInt()
    const val BOTTOM_NAV_ICON_ACTIVE = 0xFFCFD8DC.toInt()

    // 6. TERTIARY ACCENT: "Muted Iron"
    // For rare items.
    const val ACCENT_RED_SECONDARY = 0xFF78909C.toInt()

    // 7. GLASS TINT
    // Very subtle cool grey tint.
    const val GLASS_PRIMARY = 0x15B0BEC5.toInt()

    // 8. INACTIVE TEXT
    const val TOGGLE_TEXT_INACTIVE = 0xFF78909C.toInt()
}