package com.kotlingdgocucb.elimu.domain.usecase

import com.kotlingdgocucb.elimu.data.repository.ElimuRepository
import com.kotlingdgocucb.elimu.domain.model.User
import com.kotlingdgocucb.elimu.domain.utils.Result

class SetCurrentUserUseCase (private val repository: ElimuRepository) {

    suspend operator fun invoke(user: User?) : Result<User?>{

        return repository.setCurrentUser(user)
    }
}