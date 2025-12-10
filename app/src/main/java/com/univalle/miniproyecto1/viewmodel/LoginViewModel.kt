package com.univalle.miniproyecto1.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.miniproyecto1.model.UserRequest
import com.univalle.miniproyecto1.model.UserResponse
import com.univalle.miniproyecto1.repository.LoginRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.activity.viewModels


class LoginViewModel : ViewModel() {
    private val repository = LoginRepository()
    private val _isRegister = MutableLiveData<UserResponse>()
    val isRegister: LiveData<UserResponse> = _isRegister

    fun registerUser(userRequest: UserRequest) {
        viewModelScope.launch {
            repository.registerUser(userRequest) { userResponse ->
                _isRegister.value = userResponse
            }
        }
    }

    fun login(email: String, pass: String, isLogin: (Boolean) -> Unit) {
// validacion
        if (email.isNotEmpty() && pass.isNotEmpty()) {

  //corrutina
            viewModelScope.launch {
                // se pasa el email y el password y se espera respuesta
                repository.loginUser(email, pass) { loginExitoso ->

                    // respuesta a la vista
                    isLogin(loginExitoso)
                }
            }
        } else {
            // Si los campos están vacíos, devolvemos false inmediatamente
            isLogin(false)
        }

    }


   fun sesion(email: String?, isEnableView: (Boolean) -> Unit) {
        if (email != null) {
            isEnableView(true)
        } else {
            isEnableView(false)
        }
    }
}