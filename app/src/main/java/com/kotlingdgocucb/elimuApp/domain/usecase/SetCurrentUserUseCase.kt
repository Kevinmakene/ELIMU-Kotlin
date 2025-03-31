package com.kotlingdgocucb.elimuApp.domain.usecase

import com.kotlingdgocucb.elimuApp.data.repository.ElimuRepository
import com.kotlingdgocucb.elimuApp.domain.model.User
import com.kotlingdgocucb.elimuApp.domain.utils.Result

class SetCurrentUserUseCase (private val repository: ElimuRepository) {

    suspend operator fun invoke(user: User?) : Result<User?>{

        return repository.setCurrentUser(user)
    }
}