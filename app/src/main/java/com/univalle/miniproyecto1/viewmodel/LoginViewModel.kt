package com.univalle.miniproyecto1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.miniproyecto1.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    // Estado del Login
    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState

    // Estado del Registro
    private val _registerState = MutableLiveData<Boolean>()
    val registerState: LiveData<Boolean> get() = _registerState

    // Mensajes de error para la UI
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun login(email: String, pass: String) {
        if (email.isNotEmpty() && pass.isNotEmpty()) {
            viewModelScope.launch {
                // Usamos la función del repo nuevo que conecta con Firebase
                val isSuccess = repository.login(email, pass)
                if (isSuccess) {
                    _loginState.value = true
                } else {
                    _errorMessage.value = "Login incorrecto o error de conexión"
                    _loginState.value = false
                }
            }
        } else {
            _errorMessage.value = "Los campos no pueden estar vacíos"
        }
    }

    fun register(email: String, pass: String) {
        if (email.isNotEmpty() && pass.isNotEmpty()) {
            viewModelScope.launch {
                val isSuccess = repository.register(email, pass)
                if (isSuccess) {
                    _registerState.value = true
                } else {
                    _errorMessage.value = "Error en el registro (el correo podría estar en uso)"
                    _registerState.value = false
                }
            }
        }
    }

    // Verifica si ya hay sesión activa
    fun checkSession() {
        if (repository.isUserLoggedIn()) {
            _loginState.value = true
        }
    }
}

/*
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
}*/
