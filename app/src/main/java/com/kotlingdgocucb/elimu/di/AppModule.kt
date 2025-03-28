package com.kotlingdgocucb.elimu.di

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.room.Room
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.kotlingdgocucb.elimu.data.datasource.local.datastore.dataStore
import com.kotlingdgocucb.elimu.data.datasource.local.room.ElimuDao
import com.kotlingdgocucb.elimu.data.datasource.local.room.ElimuDatabase
import com.kotlingdgocucb.elimu.data.repository.ElimuRepository
import com.kotlingdgocucb.elimu.data.repository.ElimuRepositoryImpl
import com.kotlingdgocucb.elimu.data.repository.MentorRepository
import com.kotlingdgocucb.elimu.data.repository.MentorRepositoryImpl
import com.kotlingdgocucb.elimu.data.repository.ReviewRepository
import com.kotlingdgocucb.elimu.data.repository.ReviewRepositoryImpl
import com.kotlingdgocucb.elimu.data.repository.UserRepository
import com.kotlingdgocucb.elimu.data.repository.UserRepositoryImpl
import com.kotlingdgocucb.elimu.data.repository.VideoRepository
import com.kotlingdgocucb.elimu.data.repository.VideoRepositoryImpl

import com.kotlingdgocucb.elimu.domain.usecase.CreateUserUseCase
import com.kotlingdgocucb.elimu.domain.usecase.GetAllVideosUseCase
import com.kotlingdgocucb.elimu.domain.usecase.GetAverageRatingUseCase


import com.kotlingdgocucb.elimu.domain.usecase.GetCurrentUserUseCase
import com.kotlingdgocucb.elimu.domain.usecase.GetMentorsUseCase

import com.kotlingdgocucb.elimu.domain.usecase.GetReviewsUseCase
import com.kotlingdgocucb.elimu.domain.usecase.GetVideoByIdUseCase
import com.kotlingdgocucb.elimu.domain.usecase.PostReviewUseCase

import com.kotlingdgocucb.elimu.domain.usecase.SetCurrentUserUseCase
import com.kotlingdgocucb.elimu.ui.viewmodel.AuthentificationViewModel
import com.kotlingdgocucb.elimu.ui.viewmodel.MentorViewModel
import com.kotlingdgocucb.elimu.ui.viewmodel.ReviewsViewModel
import com.kotlingdgocucb.elimu.ui.viewmodel.VideoViewModel
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
            "706992175373-htcljl29hj97d7dqd4tdikdu5i3mrvom.apps.googleusercontent.com"
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

    // Fournir le ViewModel pour les vidéos
    viewModel { VideoViewModel(get(), get()) }
    // Fournir le ViewModel pour les reviews
    viewModel { ReviewsViewModel(get(), get(), get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get(),get()) }

    // Repository et use case pour l'utilisateur
    single<UserRepository> { UserRepositoryImpl(get()) }
    single { CreateUserUseCase(get()) }

}
