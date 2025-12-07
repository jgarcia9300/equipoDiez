package com.univalle.miniproyecto1.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.databinding.FragmentLoginBinding
import java.util.concurrent.Executor

@Suppress("DEPRECATION")
class LoginFragment : Fragment() {
   private lateinit var binding: FragmentLoginBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //agregar funciones
        editPassword()
//        Button()
        binding.editEmailInput.addTextChangedListener(formWatcherPassword)
        binding.editPasswordInput.addTextChangedListener(formWatcherPassword)
    }

    private fun editPassword(){
        binding.editPasswordInput.doOnTextChanged { text, start, before, count ->
            if(text!!.length < 6){
                binding.editPassword.error = "Minimo 6 digitos"
            } else {
                binding.editPassword.error = null
            }

        }
        }

    val formWatcherPassword = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {

            var email = binding.editEmailInput.text
            var password = binding.editPasswordInput.text
            var registro = binding.txtRegistrarse.text

            binding.btnIniciarSesion.isEnabled = email!!.isNotEmpty() &&
                    password!!.length > 6

            binding.txtRegistrarse.isEnabled = email.isNotEmpty() &&
                    password!!.length > 6


    }}


//    private fun Button(){
//
//    }






    }


