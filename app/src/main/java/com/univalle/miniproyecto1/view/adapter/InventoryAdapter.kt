package com.univalle.miniproyecto1.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.univalle.miniproyecto1.databinding.ItemInventoryBinding
import com.univalle.miniproyecto1.model.Inventory
import com.univalle.miniproyecto1.view.viewholder.InventoryViewHolder


class InventoryAdapter(
    private var listInventory: List<Inventory>,
    private val navController: NavController
) : RecyclerView.Adapter<InventoryViewHolder>() {


    var onClickItem: ((Inventory) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InventoryViewHolder(binding)
    }

    override fun getItemCount(): Int = listInventory.size

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val inventory = listInventory[position]
        holder.setItemInventory(inventory)


        holder.itemView.setOnClickListener {
            onClickItem?.invoke(inventory)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Inventory>) {
        listInventory = newList
        notifyDataSetChanged()
    }
}