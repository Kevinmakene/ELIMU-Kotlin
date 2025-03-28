package com.kotlingdgocucb.elimu.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Modèle de notification (à adapter selon votre application)
data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    // Exemple de liste de notifications
    val notifications = remember {
        listOf(
            NotificationItem(1, "Nouvelle vidéo", "Une nouvelle vidéo est disponible sur la plateforme.", "Il y a 5 minutes"),
            NotificationItem(2, "Mise à jour", "Votre profil a été mis à jour avec succès.", "Il y a 1 heure"),
            NotificationItem(3, "Invitation", "Vous avez reçu une invitation à rejoindre un nouveau groupe.", "Aujourd'hui")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            if (notifications.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = paddingValues.calculateTopPadding() + 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notifications) { notification ->
                        NotificationCard(notification = notification)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aucune notification pour le moment.")
                }
            }
        }
    )
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
