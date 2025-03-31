package com.kotlingdgocucb.elimuApp.data.repository

import com.kotlingdgocucb.elimuApp.domain.model.User

interface UserRepository {
    suspend fun createUser(user: User): User
}