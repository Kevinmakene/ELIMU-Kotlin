package com.kotlingdgocucb.elimu.ui.viewmodel
import com.kotlingdgocucb.elimu.domain.usecase.CreateUserUseCase
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlingdgocucb.elimu.domain.model.User
import com.kotlingdgocucb.elimu.domain.usecase.GetCurrentUserUseCase
import com.kotlingdgocucb.elimu.domain.usecase.SetCurrentUserUseCase
import com.kotlingdgocucb.elimu.domain.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthentificationViewModel(
    private val setCurrentUserUseCase: SetCurrentUserUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createUserUseCase: CreateUserUseCase
)  : ViewModel(){


    private var _currentUser : MutableStateFlow<User?> = MutableStateFlow(null)

    val currentUser = _currentUser.asStateFlow()

    private var _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading = _isLoading
        .onStart {
            getCurrentUser()
        }

        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            true
        )
    fun login(user: User?){

        viewModelScope.launch{
            val result = setCurrentUserUseCase(user)
            when(result) {
                is Result.Error -> {
                    Log.d("ELIMUDEBUG",result.message.toString())
                }
                is Result.Loading -> TODO()
                is Result.Success -> {
                    _currentUser.value = result.data
                }
            }
        }


    }

    fun logout(){
        viewModelScope.launch{
            val result = setCurrentUserUseCase(null)
            when(result) {
                is Result.Error -> {
                    Log.d("ELIMUDEBUG",result.message.toString())
                }
                is Result.Loading -> TODO()
                is Result.Success -> {
                    _currentUser.value = result.data
                }
            }
        }


    }

    fun getCurrentUser(){
        viewModelScope.launch{
            val result = getCurrentUserUseCase()

            when(result){
                is Result.Error -> {
                    _isLoading.value = false
                    _currentUser.value = null
                    Log.d("ELIMUDEBUG",result.message.toString())
                }
                is Result.Loading -> TODO()
                is Result.Success ->  {
                    _isLoading.value = false
                    _currentUser.value = result.data
                }
            }
        }
    }

    fun createUser(user: User?) {
        if (user == null) return
        viewModelScope.launch {
            try {
                val createdUser = createUserUseCase(user)
                _currentUser.value = createdUser
            } catch (e: Exception) {
                // Gérer l'erreur (afficher un message, log, etc.)
            }
        }
    }


}
