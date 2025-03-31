package com.kotlingdgocucb.elimuApp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kotlingdgocucb.elimuApp.ui.viewmodel.AuthentificationViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.kotlingdgocucb.elimuApp.domain.model.User
import com.kotlingdgocucb.elimuApp.ui.screens.introScreen.OnboardingScreen
import com.kotlingdgocucb.elimuApp.ui.theme.ElimuTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

// Définition des routes
const val OnboardingRoute = "onboarding"
const val LoaddingRoute = "loading"
const val AuthentificationRoute = "authentification"
const val ScreenTrackRoute = "screen_track"
const val ChooseMentorRoute = "chooseMentor"
const val ConfirmProfileRoute = "ConfirmProfileScreen"
const val AppScreenRoute = "appScreen"
const val VideosRoute = "videos"

// Nouvelles routes pour les items du Drawer
const val FeedbackRoute = "feedback"
const val TermsRoute = "terms"
const val AboutRoute = "about"
const val NotificationsRoute = "notifications"
const val ProfileRoute = "profile"
const val ScreenVideoPopulareRoute = "screenVideoPopulare"
const val ScreenVideoTrackRoute = "screenVideoTrack/{track}"
const val VideoDetailRoute = "videoDetail/{videoId}"

class MainActivity : ComponentActivity() {

    private val credentialManager: CredentialManager by inject()
    private val request: GetCredentialRequest by inject()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        setContent {

            val viewModel: AuthentificationViewModel by viewModel()
            val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
            val currentUser = viewModel.currentUser.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            // Maintient l'écran de démarrage tant que le chargement est actif
            splashScreen.setKeepOnScreenCondition { isLoading.value }

            ElimuTheme {
                FirebaseApp.initializeApp(this)

                NavHost(
                    navController = navController,
                    startDestination = LoaddingRoute
                ) {
                    // Écran d'onboarding
                    composable(OnboardingRoute) {
                        OnboardingScreen(navController = navController)
                    }
                    // Écran de chargement
                    composable(LoaddingRoute) {
                        LaunchedEffect(isLoading.value) {
                            if (!isLoading.value) {
                                if (currentUser.value != null) {
                                    navController.navigate(ScreenTrackRoute) {
                                        popUpTo(LoaddingRoute) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(OnboardingRoute) {
                                        popUpTo(LoaddingRoute) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }

                    // Écran d'authentification
                    composable(AuthentificationRoute) {
                        AuthentificationScreen(
                            onSignInClicked = {
                                lifecycleScope.launch {
                                    try {
                                        // Tentative de récupération des credentials
                                        val credentialResponse = credentialManager.getCredential(
                                            request = request,
                                            context = this@MainActivity
                                        )
                                        // Si la récupération réussit, on traite la connexion
                                        handleSignIn(
                                            viewModel = viewModel,
                                            response = credentialResponse,
                                            onSignInUser = { user ->
                                                viewModel.login(user)
                                                navController.navigate(ScreenTrackRoute) {
                                                    popUpTo(OnboardingRoute) { inclusive = true }
                                                }
                                            }
                                        )
                                    } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                                        // L'utilisateur a annulé la connexion
                                        Log.e("ElIMUDEBUG", "Connexion annulée par l'utilisateur", e)
                                        // Affichage d'un message utilisateur (ici avec Toast, mais tu peux utiliser un Snackbar ou autre)
                                        Toast.makeText(this@MainActivity, "Connexion annulée", Toast.LENGTH_SHORT).show()
                                        // Ici, tu peux rediriger l'utilisateur vers l'écran de connexion ou laisser la page actuelle
                                        // Par exemple, pour revenir à l'écran d'authentification :
                                        navController.navigate(AuthentificationRoute) {
                                            popUpTo(AuthentificationRoute) { inclusive = true }
                                        }
                                    } catch (e: Exception) {
                                        // Gestion des autres exceptions potentielles
                                        Log.e("ElIMUDEBUG", "Erreur lors de la connexion", e)
                                        Toast.makeText(this@MainActivity, "Erreur lors de la connexion", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            },
                            navController = navController
                        )
                    }
                    // Écran principal pour la sélection de track
                    composable(ScreenTrackRoute) {
                        LaunchedEffect(currentUser.value) {
                            if (currentUser.value == null) {
                                navController.navigate(AuthentificationRoute) {
                                    popUpTo(ScreenTrackRoute) { inclusive = true }
                                }
                            }
                        }
                        AdaptiveProfileScreen(
                            currentUser = currentUser.value,
                            onNext = { selectedTrack ->
                                val updatedUser = currentUser.value?.copy(track = selectedTrack)
                                viewModel.login(updatedUser)
                                navController.navigate(ChooseMentorRoute)
                            }
                        )
                    }
                    // Sélection de mentor
                    composable(ChooseMentorRoute) {
                        ChooseMentorScreen(
                            selectedTrack = currentUser.value?.track ?: "",
                            onMentorChosen = { mentor ->
                                val updatedUser = currentUser.value?.copy(mentor = mentor.name)
                                val updatedUserMail = currentUser.value?.copy(mentor_email = mentor.email)

                                viewModel.login(updatedUser)
                                viewModel.login(updatedUserMail)
                                navController.navigate(ConfirmProfileRoute)
                            },
                            onBack = { navController.popBackStack() },
                            navController = navController
                        )
                    }
                    // Confirmation de profil
                    composable(ConfirmProfileRoute) {
                        ConfirmProfileScreen(
                            userInfo = currentUser.value,
                            onConfirm = {
                                viewModel.createUser(currentUser.value)
                                navController.navigate(AppScreenRoute) {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            },
                            onBack = { },
                            navController = navController
                        )
                    }
                    // Écran principal de l'application
                    composable(AppScreenRoute) {
                        LaunchedEffect(currentUser.value) {
                            if (currentUser.value == null) {
                                navController.navigate(AuthentificationRoute) {
                                    popUpTo(ConfirmProfileRoute) { inclusive = true }
                                }
                            }
                        }
                        AppScreen(
                            userInfo = currentUser.value,
                            notificationsCount = 3, // Exemple de valeur, à adapter
                            onSigninOutClicked = { viewModel.logout() },
                            navController = navController
                        )
                    }
                    // Sous-graphe pour les vidéos
                    navigation(startDestination = "videoList", route = VideosRoute) {
                        composable("videoList") {
                            CourseScreen(
                                navController = navController,
                                userInfo = currentUser.value
                            )
                        }
                        composable(
                            route = VideoDetailRoute,
                            arguments = listOf(navArgument("videoId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val videoId = backStackEntry.arguments?.getInt("videoId") ?: 0
                            VideoDetailScreen(
                                videoId = videoId,
                                navController = navController,
                                UserEmail = currentUser.value!!.email
                            )
                        }
                        // Nouvelle route pour l'écran des vidéos populaires
                        composable(ScreenVideoPopulareRoute) {
                            ScreenVideoPopulare(navController = navController)
                        }
                        // Nouvelle route pour l'écran des vidéos par track
                        composable(ScreenVideoTrackRoute, arguments = listOf(navArgument("track") { type = NavType.StringType })) { backStackEntry ->
                            val track = backStackEntry.arguments?.getString("track") ?: ""
                            ScreenVideoTrack(track = track, navController = navController)
                        }
                    }
                    // Destination Notifications (accessible depuis le TopAppBar ou le Drawer)
                    composable(NotificationsRoute) {
                        NotificationsScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    // Destination Profil (accessible depuis le TopAppBar ou le Drawer)
                    composable(ProfileRoute) {
                        ProfileScreen(
                            userInfo = currentUser.value,
                            onUpdateProfile = { updatedUser ->
                                viewModel.login(updatedUser)
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    // Nouvelles destinations pour les items du Drawer
                    composable(FeedbackRoute) {
                        // Implémentez votre écran Feedback ici
                        FeedbackScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(TermsRoute) {
                        // Implémentez votre écran Terms & Conditions ici
                        TermsScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(AboutRoute) {
                        // Implémentez votre écran À propos ici
                        AboutScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    private fun handleSignIn(
        viewModel: AuthentificationViewModel,
        response: GetCredentialResponse,
        onSignInUser: (User?) -> Unit
    ) {
        val credential = response.credential
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        viewModel.firebaseSignInWithGoogle(idToken) { success, errorMsg, user ->
                            if (success) {
                                onSignInUser(user)
                            } else {
                                onSignInUser(null)
                                Log.e("ElIMUDEBUG", "Erreur Firebase : $errorMsg")
                            }
                        }
                    } catch (e: Exception) {
                        onSignInUser(null)
                        Log.e("ElIMUDEBUG", "Erreur du token Google dans la réponse", e)
                    }
                } else {
                    onSignInUser(null)
                    Log.e("ElIMUDEBUG", "Type de credential inconnu")
                }
            }
            else -> {
                onSignInUser(null)
                Log.e("ElIMUDEBUG", "Type de credential non supporté")
            }
        }
    }



}
