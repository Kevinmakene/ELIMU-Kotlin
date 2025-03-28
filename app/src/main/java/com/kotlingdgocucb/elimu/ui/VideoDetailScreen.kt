package com.kotlingdgocucb.elimu.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.airbnb.lottie.compose.*
import com.kotlingdgocucb.elimu.R
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.ReviewCreate
import com.kotlingdgocucb.elimu.ui.components.Rating
import com.kotlingdgocucb.elimu.ui.components.RatingBarInput
import com.kotlingdgocucb.elimu.ui.viewmodel.MentorViewModel
import com.kotlingdgocucb.elimu.ui.viewmodel.ReviewsViewModel
import com.kotlingdgocucb.elimu.ui.viewmodel.VideoViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    videoId: Int,
    navController: NavController,
    videoViewModel: VideoViewModel = koinViewModel(),
    reviewsViewModel: ReviewsViewModel = koinViewModel(),
    mentorViewModel: MentorViewModel = koinViewModel(),
    UserEmail: String
) {
    // Charger la vidéo et les reviews par leur ID
    LaunchedEffect(videoId) {
        videoViewModel.fetchVideoById(videoId)
        reviewsViewModel.fetchReviews(videoId)
    }

    // Observer les LiveData
    val video by videoViewModel.videoDetail.observeAsState()
    val reviews by reviewsViewModel.reviews.observeAsState(initial = emptyList())

    // Calcul du score moyen
    val averageRating = if (reviews.isNotEmpty())
        reviews.map { it.stars }.average().toFloat()
    else 0f

    // Pour copier le lien
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Mentors
    val mentorsList by mentorViewModel.mentors.observeAsState(initial = emptyList())
    val mentorForVideo = mentorsList?.find { it.email.equals(video?.mentor_email, ignoreCase = true) }

    // États pour boîte de dialogue de review
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewComment by remember { mutableStateOf("") }
    var reviewStars by remember { mutableStateOf(5) }

    // État pour l'envoi de review
    var isPostingReview by remember { mutableStateOf(false) }

    // État pour afficher la vidéo ou la miniature
    var isPlaying by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1B1B1B)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Play") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                        }
                    }
                )
            },
            floatingActionButton = {
                // Masquer le bouton si l'utilisateur a déjà laissé un avis
                val userHasReviewed = reviews.any { it.menteeEmail.equals(UserEmail, ignoreCase = true) }
                if (!userHasReviewed) {
                    FloatingActionButton(
                        onClick = { showReviewDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text("Review")
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Afficher un indicateur de progression si une review est en cours d'envoi
                if (isPostingReview) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (video == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                    Log.d("VideoDetailScreen", "Aucune vidéo trouvée pour l'ID: $videoId")
                } else {
                    // Détails de la vidéo
                    Text(
                        text = "${video?.title}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    // Affichage de la miniature ou du lecteur
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        if (!isPlaying) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data("https://img.youtube.com/vi/${video?.youtube_url.orEmpty()}/hqdefault.jpg")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Miniature de la vidéo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                when (painter.state) {
                                    is AsyncImagePainter.State.Loading -> {
                                        LottieAnimation(
                                            composition = rememberLottieComposition(
                                                LottieCompositionSpec.RawRes(R.raw.imageloading)
                                            ).value,
                                            iterations = LottieConstants.IterateForever,
                                            modifier = Modifier.size(90.dp)
                                        )
                                    }
                                    else -> SubcomposeAsyncImageContent()
                                }
                            }
                            IconButton(
                                onClick = { isPlaying = true },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.4f))
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.play),
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        } else {
                            YoutubeViewerComponent(videoId = video?.youtube_url.orEmpty())
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // Affichage de la note moyenne et du composant Rating
                    if (averageRating > 0f) {
                        Text(
                            text = "Note moyenne : ${"%.1f".format(averageRating)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Rating(rating = averageRating)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    // Section des avis
                    Text(
                        text = "Avis des utilisateurs :",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (reviews.isEmpty()) {
                        Text("Aucun avis pour le moment.", color = Color.White)
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(reviews) { review ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Rating(rating = review.stars.toFloat())
                                        Spacer(modifier = Modifier.height(8.dp))
                                        review.comment?.let {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Par : ${review.menteeEmail}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Lien YouTube en OutlinedTextField pour copier le lien
                    val fullUrl = "https://www.youtube.com/watch?v=${video?.youtube_url.orEmpty()}"
                    OutlinedTextField(
                        value = fullUrl,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            var showCopyAnimation by remember { mutableStateOf(false) }
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(R.raw.copy_animation)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(fullUrl))
                                    showCopyAnimation = true
                                }
                            ) {
                                if (showCopyAnimation) {
                                    LottieAnimation(
                                        composition = composition,
                                        iterations = 1,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    LaunchedEffect(showCopyAnimation) {
                                        kotlinx.coroutines.delay(2000)
                                        showCopyAnimation = false
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copier le lien",
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Affichage de l'image du mentor (si disponible)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (mentorForVideo != null) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(mentorForVideo.profileUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Image du mentor",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            ) {
                                when (painter.state) {
                                    is AsyncImagePainter.State.Loading -> {
                                        LottieAnimation(
                                            composition = rememberLottieComposition(
                                                LottieCompositionSpec.RawRes(R.raw.imageloading)
                                            ).value,
                                            iterations = LottieConstants.IterateForever,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                    else -> SubcomposeAsyncImageContent()
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = "Mentor : ${video?.mentor_email}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Boîte de dialogue pour envoyer un avis
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Laisser un avis") },
            text = {
                Column {
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Votre commentaire") },
                        maxLines = 1, // Limiter à une seule ligne
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingBarInput(
                        rating = reviewStars.toFloat(),
                        onRatingChanged = { newRating -> reviewStars = newRating.toInt() }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        video?.let {
                            isPostingReview = true
                            val reviewCreate = ReviewCreate(
                                videoId = it.id,
                                menteeEmail = UserEmail,
                                stars = reviewStars,
                                comment = reviewComment
                            )
                            reviewsViewModel.sendReview(reviewCreate)
                            isPostingReview = false
                        }
                        showReviewDialog = false
                    }
                ) {
                    Text("Envoyer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
