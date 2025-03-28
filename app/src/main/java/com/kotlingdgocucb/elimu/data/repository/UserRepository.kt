package com.kotlingdgocucb.elimu.data.repository

import com.kotlingdgocucb.elimu.domain.model.User

interface UserRepository {
    suspend fun createUser(user: User): User
}