package com.kotlingdgocucb.elimu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.airbnb.lottie.compose.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import com.kotlingdgocucb.elimu.R
import com.kotlingdgocucb.elimu.domain.model.User
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Video
import com.kotlingdgocucb.elimu.ui.components.Rating
import com.kotlingdgocucb.elimu.ui.viewmodel.VideoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    videoViewModel: VideoViewModel = koinViewModel(),
    navController: NavController,
    // Nouveau paramètre pour indiquer le track de l'utilisateur
    userInfo: User?
) {
    // Observer la liste des vidéos depuis le ViewModel
    val videosState = videoViewModel.videos.observeAsState(initial = emptyList())
    // État pour le "pull-to-refresh"
    var isRefreshing by remember { mutableStateOf(false) }

    // Lancer la requête dès l’affichage
    LaunchedEffect(Unit) {
        videoViewModel.fetchAllVideos()
    }

    // SwipeRefreshState de Accompanist
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    // Tri des vidéos selon "order"
    val sortedVideos = videosState.value.sortedBy { it.order }

    // Gestion du champ de recherche
    var searchQuery by remember { mutableStateOf("") }

    // Filtrer les vidéos par titre
    val filteredVideos = if (searchQuery.isBlank()) sortedVideos
    else sortedVideos.filter { it.title.contains(searchQuery, ignoreCase = true) }

    // Filtrer les vidéos correspondant au track de l'utilisateur (basé sur la catégorie)
    val trackVideos = filteredVideos.filter { it.category.equals(userInfo?.track, ignoreCase = true) }

    // Dans la section "Populaires" on affiche les 3 premières vidéos du track
    val popularVideos = trackVideos.take(3)
    // Et dans "Pour vous" le reste
    val recommendedVideos = trackVideos.drop(3)

    // Détection du type d’appareil (téléphone vs tablette)
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val coroutineScope = rememberCoroutineScope()

    // Fond sombre
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1B1B1B)
    ) {
        Scaffold { innerPadding ->
            // Intégration du SwipeRefresh
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    // Démarrer l'actualisation
                    isRefreshing = true
                    videoViewModel.fetchAllVideos()
                    // Simulation d'un délai de rafraîchissement (ou attendre la fin de la requête)
                    coroutineScope.launch {
                        delay(1000)
                        isRefreshing = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Champ de recherche
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        label = { Text("Chercher un cours", color = Color.Gray) },
                        shape = RoundedCornerShape(50.dp)
                    )

                    // Section "Populaires"
                    SectionTitle(
                        title = "Populaires",
                        onVoirPlus = { /* Action "voir plus" si nécessaire */ },
                        textColor = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(popularVideos) { video ->
                            VideoCardPopular(video = video) {
                                navController.navigate("videoDetail/${video.id}")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section "Pour vous" : affiche les vidéos correspondant au track de l'utilisateur
                    SectionTitle(
                        title = "Pour vous",
                        onVoirPlus = { /* Action "voir plus" si nécessaire */ },
                        textColor = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isTablet) {
                        // Affichage en grille sur tablette
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(recommendedVideos) { video ->
                                VideoGridItem(video = video) {
                                    navController.navigate("videoDetail/${video.id}")
                                }
                            }
                        }
                    } else {
                        // Affichage en liste sur téléphone
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(recommendedVideos) { video ->
                                VideoRowItem(video = video) {
                                    navController.navigate("videoDetail/${video.id}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Titre de section avec lien "voir plus" */
@Composable
fun SectionTitle(
    title: String,
    onVoirPlus: () -> Unit,
    textColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
        Text(
            text = "voir plus...",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onVoirPlus() }
        )
    }
}

/** Carte "Populaire" pour une vidéo */
@Composable
fun VideoCardPopular(video: Video, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .width(300.dp)
            .height(170.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color.LightGray)
            ) {
                // Affichage de la miniature avec animation Lottie pendant le chargement
                SubcomposeAsyncImage(
                    model = "https://img.youtube.com/vi/${video.youtube_url}/hqdefault.jpg",
                    contentDescription = "Miniature de ${video.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Loading -> LottieImageLoadingAnimation()
                        else -> SubcomposeAsyncImageContent()
                    }
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                // Ligne affichant la note juste en dessous du titre
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Note : ${"%.1f".format(video.stars)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Rating(rating = video.stars)
                }
                Text(
                    text = "Chapitre : ${video.order}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

/** Élément d'une liste (téléphone) : miniature et texte à droite */
@Composable
fun VideoRowItem(video: Video, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
        ) {
            SubcomposeAsyncImage(
                model = "https://img.youtube.com/vi/${video.youtube_url}/hqdefault.jpg",
                contentDescription = "Miniature de ${video.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> LottieImageLoadingAnimation()
                    else -> SubcomposeAsyncImageContent()
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            // Afficher la note juste en dessous du titre
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Note : ${"%.1f".format(video.stars)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Rating(rating = video.stars)
            }
            Text(
                text = "Catégorie : ${video.category}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

/** Élément d'une grille (tablette) pour une vidéo */
@Composable
fun VideoGridItem(video: Video, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray)
            ) {
                SubcomposeAsyncImage(
                    model = "https://img.youtube.com/vi/${video.youtube_url}/hqdefault.jpg",
                    contentDescription = "Miniature de ${video.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Loading -> LottieImageLoadingAnimation()
                        else -> SubcomposeAsyncImageContent()
                    }
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "Catégorie : ${video.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

/** Animation Lottie pour le chargement d'une image miniature */
@Composable
fun LottieImageLoadingAnimation() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.imageloading)
    )
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier.size(80.dp)
    )
}
