package com.kotlingdgocucb.elimuApp.domain.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val route: String,         // Utilisé pour la navigation
    val label: String,         // Libellé affiché
    val icon: ImageVector,     // Icône associée
    val contentDescription: String? = null // Description pour l'accessibilité
) {
    Accueil("accueil", "Accueil", Icons.Default.Home, "Page d'accueil"),
    Message("message", "Message", Icons.Default.Comment, "Page de messages"),

}
