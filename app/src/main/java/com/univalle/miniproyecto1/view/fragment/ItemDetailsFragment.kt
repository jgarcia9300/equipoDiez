package com.univalle.miniproyecto1.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.databinding.FragmentItemDetailsBinding
import com.univalle.miniproyecto1.model.Inventory
import com.univalle.miniproyecto1.viewmodel.InventoryViewModel
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemDetailsFragment : Fragment() {

    private lateinit var binding: FragmentItemDetailsBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    private lateinit var receivedInventory: Inventory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadAndDisplayInitialData()
        controladores()
        observerInventoryUpdates()
    }

    private fun controladores() {
        binding.btnDelete.setOnClickListener {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí") { _, _ ->
                    deleteInventory()
                }
                .create()
            dialog.show()
        }

        binding.contentToolbar2.toolbarDetailItem.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.fbEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("dataInventory", receivedInventory)
            findNavController().navigate(
                R.id.action_itemDetailsFragment_to_itemEditFragment,
                bundle
            )
        }
    }


    private fun renderInventoryData(item: Inventory) {
        binding.tvItem.text = item.name
        binding.tvPrice.text = "$ ${item.price}"
        binding.tvQuantity.text = item.quantity.toString()
        binding.txtTotal.text =
            "$ ${inventoryViewModel.totalProducto(item.price, item.quantity)}"
    }

    private fun loadAndDisplayInitialData() {
        val receivedBundle = arguments
            ?: run {
                findNavController().popBackStack()
                return
            }

        val data = receivedBundle.getSerializable("dataInventory") as? Inventory
            ?: run {
                findNavController().popBackStack()
                return
            }

        receivedInventory = data
        renderInventoryData(receivedInventory)
    }

    private fun observerInventoryUpdates() {

        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { list ->
            val updatedItem = list.firstOrNull { it.id == receivedInventory.id }
            if (updatedItem != null) {
                receivedInventory = updatedItem
                renderInventoryData(updatedItem)
            }
        }

        // Mensaje al eliminar
        inventoryViewModel.message.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

            if (msg.contains("eliminado", ignoreCase = true)) {
                findNavController().popBackStack()
            }
        }
    }

    private fun deleteInventory() {
        inventoryViewModel.deleteInventory(receivedInventory.id)
    }
}