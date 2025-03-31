// CreateUserUseCase.kt
package com.kotlingdgocucb.elimuApp.domain.usecase


import com.kotlingdgocucb.elimuApp.data.repository.UserRepository
import com.kotlingdgocucb.elimuApp.domain.model.User

class CreateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User): User {
        return repository.createUser(user)
    }
}
