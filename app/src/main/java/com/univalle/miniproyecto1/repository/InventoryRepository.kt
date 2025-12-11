package com.univalle.miniproyecto1.repository

import com.univalle.miniproyecto1.model.Inventory


interface InventoryRepository {
    // Auth
    suspend fun login(email: String, pass: String): Boolean
    suspend fun register(email: String, pass: String): Boolean
    fun logout()
    fun isUserLoggedIn(): Boolean

    // Database
    suspend fun getProducts(): List<Inventory>
    suspend fun addProduct(product: Inventory)
    suspend fun updateProduct(product: Inventory)
    suspend fun deleteProduct(productId: String)
}

/*
package com.univalle.miniproyecto1.repository

import android.content.Context
import com.univalle.miniproyecto1.data.InventoryDao
import com.univalle.miniproyecto1.data.InventoryDB
import com.univalle.miniproyecto1.model.Inventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryRepository(context: Context) {
    private var inventoryDao:InventoryDao = InventoryDB.getDatabase(context).inventoryDao()

    //Guardar Inventario
    suspend fun saveInventory(inventory:Inventory, messageResponse: (String) -> Unit){
        try {
            withContext(Dispatchers.IO){
                inventoryDao.saveInventory(inventory)
            } //Mensaje de exito y error
            messageResponse("Inventario Guardado Correctamente")
        } catch (e:Exception){
            messageResponse("Error al guardar el Inventario: ${e.message}")
        }
    }

    //Obtener Inventario
    suspend fun getListInventory():MutableList<Inventory>{
        return withContext(Dispatchers.IO){
            inventoryDao.getListInventory()
        }
    }
    //Eliminar articulo del inventario
    suspend fun deleteInventory(inventory: Inventory){
        withContext(Dispatchers.IO){
            inventoryDao.deleteInventory(inventory)
        }
    }
    //Actualizar articulo del inventario
    suspend fun updateRepositoy(inventory: Inventory){
        withContext(Dispatchers.IO){
            inventoryDao.updateInventory(inventory)
        }
    }

    // Obtener saldo total del inventario
    suspend fun getInventoryTotalBalance(): Double {
        return withContext(Dispatchers.IO) {
            val result = inventoryDao.getInventoryTotalBalance()
            result ?: 0.0 // si es null (tabla vacía), devolver 0
        }
    }
}*/
