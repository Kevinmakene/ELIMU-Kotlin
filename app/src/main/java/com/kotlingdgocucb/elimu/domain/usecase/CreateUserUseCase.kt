// CreateUserUseCase.kt
package com.kotlingdgocucb.elimu.domain.usecase


import com.kotlingdgocucb.elimu.data.repository.UserRepository
import com.kotlingdgocucb.elimu.domain.model.User

class CreateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User): User {
        return repository.createUser(user)
    }
}
