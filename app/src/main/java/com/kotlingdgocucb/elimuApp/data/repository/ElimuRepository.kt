package com.kotlingdgocucb.elimuApp.data.repository

import com.kotlingdgocucb.elimuApp.domain.model.User
import com.kotlingdgocucb.elimuApp.domain.utils.Result


interface ElimuRepository {
    suspend fun setCurrentUser(user: User?): Result<User?>


    suspend fun getCurrentUser(): Result<User?>

    suspend fun updateOrSyncCurrentUser(newUser: User?): Result<User?>




}