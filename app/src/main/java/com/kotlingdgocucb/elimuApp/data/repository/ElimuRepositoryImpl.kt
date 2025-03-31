package com.kotlingdgocucb.elimuApp.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kotlingdgocucb.elimuApp.data.datasource.local.datastore.PreferenceKeys
import com.kotlingdgocucb.elimuApp.domain.model.User
import com.kotlingdgocucb.elimuApp.domain.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ElimuRepositoryImpl(
    private val datastore: DataStore<Preferences>,
) : ElimuRepository {

    override suspend fun setCurrentUser(user: User?): Result<User?> {
        return try {
            Log.d("ElimuRepositoryImpl", "Début de setCurrentUser avec user: $user")
            datastore.edit { preferences ->
                preferences[PreferenceKeys.userName] = user?.name ?: ""
                preferences[PreferenceKeys.userEmail] = user?.email ?: ""
                preferences[PreferenceKeys.userProfilePicture] = user?.profile_picture_uri ?: ""
                preferences[PreferenceKeys.isLoggedIn] = user?.isLoggedIn ?: false
                preferences[PreferenceKeys.userTrack] = user?.track ?: ""
                preferences[PreferenceKeys.userMentor] = user?.mentor ?: ""
            }
            Log.d("ElimuRepositoryImpl", "Utilisateur enregistré avec succès")
            Result.Success(user)
        } catch (e: Exception) {
            Log.e("ElimuRepositoryImpl", "Erreur lors de la modification de l'utilisateur", e)
            Result.Error("Erreur lors de la modification de l'utilisateur : ${e.message}")
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            Log.d("ElimuRepositoryImpl", "Début de getCurrentUser")
            val currentUser = datastore.data.map { preferences ->
                User(
                    id = 0,
                    name = preferences[PreferenceKeys.userName] ?: "",
                    email = preferences[PreferenceKeys.userEmail] ?: "",
                    profile_picture_uri = preferences[PreferenceKeys.userProfilePicture] ?: "",
                    isLoggedIn = preferences[PreferenceKeys.isLoggedIn] == true,
                    track = preferences[PreferenceKeys.userTrack] ?: "",
                    mentor = preferences[PreferenceKeys.userMentor] ?: "",
                    createdAt = ""
                )
            }.first()

            Log.d("ElimuRepositoryImpl", "Utilisateur récupéré: $currentUser")
            Result.Success(if (currentUser.isLoggedIn) currentUser else null)
        } catch (e: Exception) {
            Log.e("ElimuRepositoryImpl", "Erreur lors de la récupération de l'utilisateur", e)
            Result.Error("Erreur lors de la récupération de l'utilisateur : ${e.message}")
        }
    }

    /**
     * Cette fonction vérifie si l'utilisateur stocké est différent de celui passé en paramètre.
     * En cas de différence, elle met à jour le DataStore et retourne le nouvel utilisateur.
     * Sinon, elle retourne l'utilisateur existant.
     */
    override suspend fun updateOrSyncCurrentUser(newUser: User?): Result<User?> {
        return try {
            Log.d("ElimuRepositoryImpl", "Début de updateOrSyncCurrentUser avec newUser: $newUser")
            val currentUserResult = getCurrentUser()
            val currentUser = if (currentUserResult is Result.Success) currentUserResult.data else null

            if (currentUser != newUser) {
                // Des modifications ont été détectées, on met à jour.
                Log.d("ElimuRepositoryImpl", "Modification détectée, mise à jour de l'utilisateur.")
                setCurrentUser(newUser)
            } else {
                Log.d("ElimuRepositoryImpl", "Aucune modification détectée, utilisateur inchangé: $currentUser")
                currentUserResult
            }
        } catch (e: Exception) {
            Log.e("ElimuRepositoryImpl", "Erreur lors de la mise à jour de l'utilisateur", e)
            Result.Error("Erreur lors de la mise à jour de l'utilisateur : ${e.message}")
        }
    }
}
