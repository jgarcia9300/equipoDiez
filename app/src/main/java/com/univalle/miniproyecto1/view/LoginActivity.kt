package com.univalle.miniproyecto1.view

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import android.content.Context
import android.content.Intent
import android.view.View
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.databinding.FragmentLoginBinding
import com.univalle.miniproyecto1.model.UserRequest
import com.univalle.miniproyecto1.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.fragment_login)
        sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)

        checkSession()
        sesion()
        setup()
        viewModelObservers()
        setupTextWatchers()
    }


    private fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun viewModelObservers() {
        loginViewModel.isRegister.observe(this) { userResponse ->
            if (userResponse.isRegister) {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()

                // Guardar sesión al registrarse
                sharedPreferences.edit()
                    .putBoolean("is_logged_in", true)
                    .putString("email", userResponse.email)
                    .apply()

                goToHome()
            } else {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editPassword() {
        binding.editPasswordInput.doOnTextChanged { text, _, _, _ ->
            if (text!!.length < 6) {
                binding.editPassword.error = "Mínimo 6 dígitos"
            } else {
                binding.editPassword.error = null
            }
        }
    }


    private fun setupTextWatchers() {
        editPassword()

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = binding.editEmailInput.text.toString()
                val pass = binding.editPasswordInput.text.toString()

                binding.btnIniciarSesion.isEnabled = email.isNotEmpty() && pass.length > 5
                binding.txtRegistrarse.isEnabled = email.isNotEmpty() && pass.length > 5
            }
        }

        binding.editEmailInput.addTextChangedListener(watcher)
        binding.editPasswordInput.addTextChangedListener(watcher)
    }


    private fun setup() {
        binding.txtRegistrarse.setOnClickListener {
            registerUser()
        }

        binding.btnIniciarSesion.setOnClickListener {
            loginUser()
        }
    }


    private fun registerUser() {
        val email = binding.editEmailInput.text.toString()
        val pass = binding.editPasswordInput.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            val userRequest = UserRequest(email, pass)
            loginViewModel.registerUser(userRequest)
        } else {
            Toast.makeText(this, "Campos Vacíos", Toast.LENGTH_SHORT).show()
        }
    }

    // Login
    private fun loginUser() {
        val email = binding.editEmailInput.text.toString()
        val pass = binding.editPasswordInput.text.toString()

        loginViewModel.login(email, pass) { isLogin ->
            if (isLogin) {


                sharedPreferences.edit()
                    .putBoolean("is_logged_in", true)
                    .putString("email", email)
                    .apply()

                goToHome()
            } else {
                Toast.makeText(this, "Login incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun sesion() {
        val email = sharedPreferences.getString("email", null)
        loginViewModel.sesion(email) { isEnableView ->
            if (isEnableView) {
                binding.clContenedor.visibility = View.INVISIBLE
                goToHome()
            }
        }
    }


    private fun checkSession() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            goToHome()
        }
    }
}



