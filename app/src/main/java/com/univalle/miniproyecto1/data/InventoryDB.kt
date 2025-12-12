/*
package com.univalle.miniproyecto1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.univalle.miniproyecto1.model.Inventory
import com.univalle.miniproyecto1.utils.Constants.NAME_DB


@Database(entities = [Inventory::class], version = 1)
abstract class InventoryDB : RoomDatabase() {

    abstract fun inventoryDao(): InventoryDao

    companion object{
        fun getDatabase(context: Context): InventoryDB {
            return Room.databaseBuilder(
                context.applicationContext,
                InventoryDB::class.java,
                NAME_DB
            ).build()
        }
    }
}
*/
