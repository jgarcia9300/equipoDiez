package com.univalle.miniproyecto1.view

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.univalle.miniproyecto1.model.UserRequest
import com.univalle.miniproyecto1.viewmodel.LoginViewModel
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.databinding.FragmentHomeBinding
import com.univalle.miniproyecto1.databinding.FragmentLoginBinding
import com.univalle.miniproyecto1.view.fragment.HomeFragment

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

//Navegar al Home
    private fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

//observador viewmodel
    private fun viewModelObservers() {
        loginViewModel.isRegister.observe(this) { userResponse ->
            if (userResponse.isRegister) {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()

                // guardar sesión
                sharedPreferences.edit()
                    .putString("email", userResponse.email)
                    .apply()

                goToHome()
            } else {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //validacion contraseña
    private fun editPassword() {
        binding.editPasswordInput.doOnTextChanged { text, _, _, _ ->
            if (text!!.length < 6) {
                binding.editPassword.error = "Mínimo 6 dígitos"
            } else {
                binding.editPassword.error = null
            }
        }
    }

    //cantidad de caracteres en tiempo real
    private fun setupTextWatchers() {

        editPassword()

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = binding.editEmailInput.text.toString()
                val pass = binding.editPasswordInput.text.toString()

                binding.btnIniciarSesion.isEnabled =
                    email.isNotEmpty() && pass.length > 5

                binding.txtRegistrarse.isEnabled =
                    email.isNotEmpty() && pass.length > 5
            }
        }

        binding.editEmailInput.addTextChangedListener(watcher)
        binding.editPasswordInput.addTextChangedListener(watcher)
    }

  //listeners botones
    private fun setup() {
        binding.txtRegistrarse.setOnClickListener {
            registerUser()
        }

        binding.btnIniciarSesion.setOnClickListener {
            loginUser()
        }
    }

    //Registro
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

    //Login
    private fun loginUser() {
        val email = binding.editEmailInput.text.toString()
        val pass = binding.editPasswordInput.text.toString()
        loginViewModel.login(email,pass){ isLogin ->
            if (isLogin){
                sharedPreferences.edit().putString("email",email).apply()
                goToHome()
            }else {
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




