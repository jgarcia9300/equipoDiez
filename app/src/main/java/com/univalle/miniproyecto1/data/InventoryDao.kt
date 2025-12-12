/*
package com.univalle.miniproyecto1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
// Llamado a la tabla Inventory
import com.univalle.miniproyecto1.model.Inventory

// CRUD
@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveInventory(inventory: Inventory)

    @Query("SELECT * FROM Inventory")
    suspend fun getListInventory(): MutableList<Inventory>

    @Delete
    suspend fun deleteInventory(inventory: Inventory)

    @Update
    suspend fun updateInventory(inventory: Inventory)

    @Query("SELECT SUM(price * quantity) FROM Inventory")
    suspend fun getInventoryTotalBalance(): Double?
}
*/
