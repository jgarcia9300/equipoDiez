package com.univalle.miniproyecto1.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.miniproyecto1.databinding.FragmentItemEditBinding
import com.univalle.miniproyecto1.model.Inventory
import com.univalle.miniproyecto1.viewmodel.InventoryViewModel
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint


class ItemEditFragment : Fragment() {

    private lateinit var binding: FragmentItemEditBinding
    private val inventoryViewModel: InventoryViewModel by activityViewModels()

    private lateinit var receivedInventory: Inventory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recibirDatos()
        configurarToolbar()
        configurarBotonEditar()
        observarCambiosCampos()
    }

    private fun recibirDatos() {
        arguments?.let { bundle ->
            receivedInventory = bundle.getSerializable("dataInventory") as Inventory

            binding.txtIdValue.text = receivedInventory.id.toString()
            binding.edtNombre.setText(receivedInventory.name)
            binding.edtPrecio.setText(receivedInventory.price.toString())
            binding.edtCantidad.setText(receivedInventory.quantity.toString())
        }
    }

    private fun configurarToolbar() {
        binding.contentToolbar2.toolbarEdit.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun configurarBotonEditar() {
        binding.btnEditar.setOnClickListener {
            actualizarProducto()
        }
    }

    private fun observarCambiosCampos() {
        val watcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                validarCampos()
            }
        }

        binding.edtNombre.addTextChangedListener(watcher)
        binding.edtPrecio.addTextChangedListener(watcher)
        binding.edtCantidad.addTextChangedListener(watcher)

        validarCampos()
    }

    private fun validarCampos() {
        val nombre = binding.edtNombre.text.toString().trim()
        val precio = binding.edtPrecio.text.toString().trim()
        val cantidad = binding.edtCantidad.text.toString().trim()

        val habilitado = nombre.isNotEmpty() && precio.isNotEmpty() && cantidad.isNotEmpty()
        binding.btnEditar.isEnabled = habilitado
        binding.btnEditar.alpha = if (habilitado) 1f else 0.5f
    }

    private fun actualizarProducto() {
        val nombre = binding.edtNombre.text.toString().trim()
        val precioText = binding.edtPrecio.text.toString().trim()
        val cantidadText = binding.edtCantidad.text.toString().trim()

        val precio = precioText.toIntOrNull()
        val cantidad = cantidadText.toIntOrNull()

        if (precio == null) {
            binding.edtPrecio.error = "Precio inválido"
            return
        }
        if (cantidad == null) {
            binding.edtCantidad.error = "Cantidad inválida"
            return
        }


        val productoActualizado = Inventory(
            id = receivedInventory.id,
            code = receivedInventory.code,   // STRING ✔
            name = nombre,
            price = precio,                  // INT ✔
            quantity = cantidad              // INT ✔
        )

        inventoryViewModel.updateInventory(productoActualizado)
        inventoryViewModel.getListInventory()

        findNavController().popBackStack()
    }
}
