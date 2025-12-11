package com.univalle.miniproyecto1.view.viewholder

import android.os.Bundle
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.databinding.ItemInventoryBinding
import com.univalle.miniproyecto1.model.Inventory
import java.text.NumberFormat
import java.util.Locale

class InventoryViewHolder(
    private val binding: ItemInventoryBinding,
    private val navController: NavController
) : RecyclerView.ViewHolder(binding.root) {

    fun setItemInventory(inventory: Inventory) {


        binding.tvName.text = inventory.name


        val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        binding.tvPrice.text = formato.format(inventory.price)


        binding.tvQuantity.text = "ID: ${inventory.quantity}"


        binding.cvInventory.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("dataInventory", inventory)
            }
            navController.navigate(
                R.id.action_homeFragment_to_itemDetailsFragment,
                bundle
            )
        }
    }
}
