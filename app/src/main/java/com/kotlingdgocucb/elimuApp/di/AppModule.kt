package com.kotlingdgocucb.elimuApp.di

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.room.Room
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.kotlingdgocucb.elimuApp.data.datasource.local.datastore.dataStore
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.ElimuDao
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.ElimuDatabase
import com.kotlingdgocucb.elimuApp.data.repository.ElimuRepository
import com.kotlingdgocucb.elimuApp.data.repository.ElimuRepositoryImpl
import com.kotlingdgocucb.elimuApp.data.repository.MentorRepository
import com.kotlingdgocucb.elimuApp.data.repository.MentorRepositoryImpl
import com.kotlingdgocucb.elimuApp.data.repository.MessageRepository
import com.kotlingdgocucb.elimuApp.data.repository.MessageRepositoryImpl
import com.kotlingdgocucb.elimuApp.data.repository.ProgressRepository
import com.kotlingdgocucb.elimuApp.data.repository.ProgressRepositoryImpl
import com.kotlingdgocucb.elimuApp.data.repository.ReviewRepository
import com.kotlingdgocucb.elimuApp.data.repository.ReviewRepositoryImpl
import com.kotlingdgocucb.elimuApp.data.repository.UserRepository
import com.kotlingdgocucb.elimuApp.data.repository.UserRepositoryImpl
import com.kotlingdgocucb.elimuApp.data.repository.VideoRepository
import com.kotlingdgocucb.elimuApp.data.repository.VideoRepositoryImpl
import com.kotlingdgocucb.elimuApp.domain.model.User
import com.kotlingdgocucb.elimuApp.domain.usecase.CreateChatUseCase

import com.kotlingdgocucb.elimuApp.domain.usecase.CreateUserUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.GetAllVideosUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.GetAverageRatingUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.GetChatMessageUseCase


import com.kotlingdgocucb.elimuApp.domain.usecase.GetCurrentUserUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.GetMentorsUseCase

import com.kotlingdgocucb.elimuApp.domain.usecase.GetReviewsUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.GetVideoByIdUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.MarkMessageAsReadUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.PostReviewUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.ProgressUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.SendMessageUseCase

import com.kotlingdgocucb.elimuApp.domain.usecase.SetCurrentUserUseCase
import com.kotlingdgocucb.elimuApp.ui.viewmodel.AuthentificationViewModel
import com.kotlingdgocucb.elimuApp.ui.viewmodel.MentorViewModel
import com.kotlingdgocucb.elimuApp.ui.viewmodel.MessageViewModel
import com.kotlingdgocucb.elimuApp.ui.viewmodel.ProgressViewModel
import com.kotlingdgocucb.elimuApp.ui.viewmodel.ReviewsViewModel
import com.kotlingdgocucb.elimuApp.ui.viewmodel.VideoViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single<ElimuRepository> { ElimuRepositoryImpl(get())}
    factory { GetMentorsUseCase(get()) }



    // CredentialManager for Android credentials
    factory {
        CredentialManager.create(androidContext())
    }

    // Build GetCredentialRequest with Google options
    factory {
        val googleOptions = GetSignInWithGoogleOption.Builder(
            "547623569349-lvgrbermdvb5e8ssa021kgrh9nunfdk8.apps.googleusercontent.com"
        ).build()


        GetCredentialRequest.Builder()
            .addCredentialOption(googleOptions)
            .build()
    }

    // Provide DataStore instance
    single { androidContext().dataStore }



    // Provide UseCases for Elimu features
    factoryOf(::SetCurrentUserUseCase)
    factoryOf(::GetCurrentUserUseCase)




    // Provide Authentification ViewModel
    viewModelOf(::AuthentificationViewModel)

    // Provide Ktor HttpClient configuration
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    single<MessageRepository>{
        MessageRepositoryImpl(get())
}

    single{FirebaseFirestore.getInstance()}

    // Provide Room database
    single {
        Room.databaseBuilder(
            androidApplication(),
            ElimuDatabase::class.java,
            "elimuApp.db"
        ).build()
    }

    // Provide ElimuDao from Room database
    factory<ElimuDao> {
        get<ElimuDatabase>().elimuDao()
    }

    // Provide MentorRepository implementation (Firebase)
    single<MentorRepository> { MentorRepositoryImpl(get()) }

    single{Firebase}

    // Provide Mentor ViewModel
    viewModelOf(::MentorViewModel)

    // Fournir l'instance du Repository en utilisant l'implémentation
    single<VideoRepository> { VideoRepositoryImpl(get(),get()) }

    // Fournir les use cases
    factory { GetAllVideosUseCase(get()) }
    factory { GetVideoByIdUseCase(get()) }

    // Fournir le ViewModel
    viewModel { VideoViewModel(get(), get()) }

    // Fournir les UseCases pour les reviews
    factory { GetReviewsUseCase(get()) }
    factory { GetAverageRatingUseCase(get()) }
    factory { PostReviewUseCase(get()) }
    // Fournir les UseCases pour les message
    factory{MarkMessageAsReadUseCase(get())}
    factory{GetChatMessageUseCase(get())}
    factory{SendMessageUseCase(get())}
    factory{CreateChatUseCase(get())}

    // Fournir le ViewModel pour les vidéos
    viewModel { VideoViewModel(get(), get()) }
    // Fournir le ViewModel pour les reviews
    viewModel { ReviewsViewModel(get(), get(), get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get(),get()) }
    viewModelOf(::MessageViewModel)

    // Repository et use case pour l'utilisateur
    single<UserRepository> { UserRepositoryImpl(get()) }
    single { CreateUserUseCase(get()) }




    // Repository de progression
    // Fournir le Repository (pour les appels réseau – Room peut être utilisé en complément si besoin)
    single<ProgressRepository> { ProgressRepositoryImpl(get(),get()) }

    // Fournir le UseCase
    single { ProgressUseCase(get()) }

    // Fournir le ViewModel
    viewModel { ProgressViewModel(get()) }

}
