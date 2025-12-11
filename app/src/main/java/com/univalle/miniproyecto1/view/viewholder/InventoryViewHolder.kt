package com.univalle.miniproyecto1.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.univalle.miniproyecto1.databinding.ItemInventoryBinding
import com.univalle.miniproyecto1.model.Inventory
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class InventoryViewHolder(private val binding: ItemInventoryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun setItemInventory(inventory: Inventory) {

        binding.tvName.text = inventory.name
        binding.tvCode.text = "ID: ${inventory.code}"
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val decimalFormat = DecimalFormat("#,###.00", symbols)
        binding.tvPrice.text = "$ ${decimalFormat.format(inventory.price)}"
    }
}