package com.kotlingdgocucb.elimu.data.repository

import com.kotlingdgocucb.elimu.domain.model.User
import com.kotlingdgocucb.elimu.domain.utils.Result


interface ElimuRepository {
    suspend fun setCurrentUser(user: User?): Result<User?>


    suspend fun getCurrentUser(): Result<User?>

    suspend fun updateOrSyncCurrentUser(newUser: User?): Result<User?>




}