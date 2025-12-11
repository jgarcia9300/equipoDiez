package com.univalle.miniproyecto1.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.databinding.FragmentLoginBinding
import com.univalle.miniproyecto1.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: FragmentLoginBinding

    // Inyección del ViewModel
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verifica si hay sesión activa al iniciar
        viewModel.checkSession()

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        // Validación de campos
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.editEmailInput.addTextChangedListener(textWatcher)
        binding.editPasswordInput.addTextChangedListener(textWatcher)

        // Botón Login
        binding.btnIniciarSesion.setOnClickListener {
            val email = binding.editEmailInput.text.toString()
            val pass = binding.editPasswordInput.text.toString()
            viewModel.login(email, pass)
        }

        // Botón Registrarse
        binding.txtRegistrarse.setOnClickListener {
            val email = binding.editEmailInput.text.toString()
            val pass = binding.editPasswordInput.text.toString()

            if (binding.btnIniciarSesion.isEnabled) {
                viewModel.register(email, pass)
            } else {
                Toast.makeText(this, "Complete los campos para registrarse", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs() {
        val email = binding.editEmailInput.text.toString()
        val pass = binding.editPasswordInput.text.toString()

        var isValid = true

        // Validación Contraseña
        if (pass.isNotEmpty() && pass.length < 6) {
            binding.editPassword.error = "Mínimo 6 caracteres"
            isValid = false
        } else {
            binding.editPassword.error = null
        }

        // Validación Email
        if (email.isEmpty()) {
            isValid = false
        }

        binding.btnIniciarSesion.isEnabled = isValid
        binding.txtRegistrarse.isEnabled = isValid

        val colorResource = if(isValid) android.R.color.white else R.color.white_stroke_color
        binding.txtRegistrarse.setTextColor(resources.getColor(colorResource, null))
    }

    private fun setupObservers() {

        // Observar resultado de Login/Sesión
        viewModel.loginState.observe(this) { isSuccess ->
            if (isSuccess) {
                navigateToHome()
            }
        }

        // Observar resultado de Registro
        viewModel.registerState.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Registro Exitoso. Iniciando sesión...", Toast.LENGTH_LONG).show()
                navigateToHome()
            }
        }

        // Observar Errores
        viewModel.errorMessage.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }

    // Navegacion de login al home
    private fun navigateToHome() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

/*
package com.univalle.miniproyecto1.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.databinding.FragmentLoginBinding
import com.univalle.miniproyecto1.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verificar si hay sesión activa al iniciar
        viewModel.checkSession()

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {

        // Validación de campos
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.editEmailInput.addTextChangedListener(textWatcher)
        binding.editPasswordInput.addTextChangedListener(textWatcher)

        // Botón Login
        binding.btnIniciarSesion.setOnClickListener {
            val email = binding.editEmailInput.text.toString()
            val pass = binding.editPasswordInput.text.toString()
            viewModel.login(email, pass)
        }

        // Botón Registrarse
        binding.txtRegistrarse.setOnClickListener {
            val email = binding.editEmailInput.text.toString()
            val pass = binding.editPasswordInput.text.toString()

            // Solo permite registrar si los campos son válidos
            if (binding.btnIniciarSesion.isEnabled) {
                viewModel.register(email, pass)
            } else {
                Toast.makeText(requireContext(), "Complete los campos para registrarse", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función de Validación
    private fun validateInputs() {
        val email = binding.editEmailInput.text.toString()
        val pass = binding.editPasswordInput.text.toString()

        var isValid = true

        // Validación Contraseña
        if (pass.isNotEmpty() && pass.length < 6) {
            binding.editPassword.error = "Mínimo 6 caracteres"
            isValid = false
        } else {
            binding.editPassword.error = null
        }

        // Validación Email
        if (email.isEmpty()) {
            isValid = false
        }


        // Habilitar Botón Login
        binding.btnIniciarSesion.isEnabled = isValid

        // Cambiar color texto Registrarse
        val colorInt = if(isValid) resources.getColor(android.R.color.white, null) else resources.getColor(R.color.white_stroke_color, null)
        binding.txtRegistrarse.setTextColor(colorInt)

    }

    private fun setupObservers() {
        val navController = findNavController()

        // Observar resultado de Login/Sesión
        viewModel.loginState.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                // Navegar a Home si es exitoso
                navController.navigate(R.id.action_loginFragment_to_homeFragment) // ASEGURA ESTA RUTA
            }
        }

        // Observar resultado de Registro
        viewModel.registerState.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Registro Exitoso. Iniciando sesión...", Toast.LENGTH_LONG).show()
                navController.navigate(R.id.action_loginFragment_to_homeFragment) // ASEGURA ESTA RUTA
            }
        }

        // Observar Errores
        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}*/
