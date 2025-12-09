package com.univalle.miniproyecto1.repository

import com.univalle.miniproyecto1.model.UserRequest
import com.univalle.miniproyecto1.model.UserResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.univalle.miniproyecto1.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    suspend fun registerUser(userRequest: UserRequest, userResponse: (UserResponse) -> Unit) {
        withContext(Dispatchers.IO){
            try {
                firebaseAuth.createUserWithEmailAndPassword(userRequest.email, userRequest.password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            val email = task.result?.user?.email
                            userResponse(
                                UserResponse(
                                    email = email,
                                    isRegister = true,
                                    message = "Registro Exitoso"
                                )
                            )
                        } else {
                            val error = task.exception
                            if (error is FirebaseAuthUserCollisionException) {
                                // Si ya existe un email registrado
                                userResponse(
                                    UserResponse(
                                        isRegister = false,
                                        message = "Error en el registro"
                                    )
                                )
                            } else {
                                // Si hay otros errores
                                userResponse(
                                    UserResponse(
                                        isRegister = false,
                                        message = "Error en el registro"
                                    )
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                // Manejo de excepciones generales
                userResponse(
                    UserResponse(
                        isRegister = false,
                        message = e.message ?: "Error desconocido"
                    )
                )
            }
        }

    }
//
//    fun loginUser(email: String, pass: String, isLogin: (Boolean) -> Unit) {
//
//        if (email.isNotEmpty() && pass.isNotEmpty()) {
//            FirebaseAuth.getInstance()
//                .signInWithEmailAndPassword(email, pass)
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        isLogin(true)
//                    } else {
//                        isLogin(false)
//                    }
//                }
//        } else {
//            isLogin(false)
//        }
//    }

    // Dentro de LoginRepository.kt

// Asegúrate de tener la instancia arriba como propiedad de clase
// private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun loginUser(email: String, pass: String, isLogin: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                // Ya no validamos si está vacío aquí. Asumimos que el dato llegó bien.
                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            isLogin(true)
                        } else {
                            isLogin(false)
                        }
                    }
                    // Es bueno agregar un listener de falla por si hay problemas de red
                    .addOnFailureListener {
                        isLogin(false)
                    }
            } catch (e: Exception) {
                // Si ocurre un error inesperado (crash), devolvemos false para que la app no se cierre
                isLogin(false)
            }
        }
    }




}