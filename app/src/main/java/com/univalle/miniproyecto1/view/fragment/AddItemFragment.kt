package com.univalle.miniproyecto1.view.fragment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.viewModels
import androidx.core.widget.addTextChangedListener
import com.univalle.miniproyecto1.databinding.FragmentAddItemBinding
import com.univalle.miniproyecto1.model.Inventory
import com.univalle.miniproyecto1.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddItemFragment : Fragment() {
    private lateinit var binding: FragmentAddItemBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddItemBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controladores()
    }

    private fun controladores() {
        validarDatos()
        binding.btnSaveInventory.setOnClickListener {
            saveInvetory()
        }
        binding.contentToolbar2.toolbarCreateItem.setNavigationOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun saveInvetory(){
        val codigo = binding.etCodigo.text.toString()
        val name = binding.etName.text.toString()
        val price = binding.etPrice.text.toString().toInt()
        val quantity = binding.etQuantity.text.toString().toInt()
        val inventory = Inventory(code = codigo,name = name, price = price, quantity = quantity)

        inventoryViewModel.saveInventory(inventory){message ->
            Toast.makeText(context,"Artículo guardado !!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
        Log.d("test",inventory.toString())
    }

    private fun validarDatos() {
        val listEditText = listOf(binding.etCodigo,binding.etName, binding.etPrice, binding.etQuantity)

        for (editText in listEditText) {
            editText.addTextChangedListener {
                val isListFull = listEditText.all{
                    it.text.isNotEmpty() // si toda la lista no está vacía
                }
                binding.btnSaveInventory.isEnabled = isListFull
            }
        }
    }

}