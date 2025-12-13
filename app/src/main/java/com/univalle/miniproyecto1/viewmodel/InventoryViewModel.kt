package com.univalle.miniproyecto1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.miniproyecto1.model.Inventory
import com.univalle.miniproyecto1.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _listInventory = MutableLiveData<List<Inventory>>()
    val listInventory: LiveData<List<Inventory>> get() = _listInventory

    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> get() = _progresState

    // Mensaje para notificar guardado/borrado/error
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    // Criterio HU 3.0: Cargar al iniciar
    init {
        getListInventory()
    }

    fun getListInventory() {
        viewModelScope.launch {
            _progresState.value = true
            try {
                // Ahora llama a Firestore a través del repositorio
                val products = repository.getProducts()
                _listInventory.value = products
            } catch (e: Exception) {
                _message.value = "Error al cargar datos"
            } finally {
                _progresState.value = false
            }
        }
    }

    fun saveInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                repository.addProduct(inventory)
                _message.value = "Producto guardado correctamente"
                getListInventory() // Recargar lista
            } catch (e: Exception) {
                _message.value = "Error al guardar: ${e.message}"
            } finally {
                _progresState.value = false
            }
        }
    }

    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                repository.updateProduct(inventory)
                _message.value = "Producto actualizado"
                getListInventory()
            } catch (e: Exception) {
                _message.value = "Error al actualizar"
            } finally {
                _progresState.value = false
            }
        }
    }

    fun deleteInventory(productId: String) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                repository.deleteProduct(productId)
                _message.value = "Producto eliminado"
                getListInventory()
            } catch (e: Exception) {
                _message.value = "Error al eliminar"
            } finally {
                _progresState.value = false
            }
        }
    }

    fun signOut() {
        repository.logout()
    }

    // Lógica de negocio (HU 1.0 Criterio 8) - Mantenemos tu función
    fun totalProducto(price: Double, quantity: Int): Double {
        return price * quantity
    }
}

/*
package com.univalle.miniproyecto1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.miniproyecto1.model.Inventory
import com.univalle.miniproyecto1.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val inventoryRepository = InventoryRepository(application)

    private val _listInventory = MutableLiveData<MutableList<Inventory>>()
    val listInventory: LiveData<MutableList<Inventory>> get() = _listInventory

    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> get() = _progresState


    fun saveInventory(inventory: Inventory, message: (String) -> Unit) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                inventoryRepository.saveInventory(inventory) { msg ->
                    message(msg)
                }


                _listInventory.value = inventoryRepository.getListInventory()

            } finally {
                _progresState.value = false
            }
        }
    }


    fun getListInventory() {
        viewModelScope.launch {
            _progresState.value = true
            try {
                _listInventory.value = inventoryRepository.getListInventory()
            } finally {
                _progresState.value = false
            }
        }
    }


    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                inventoryRepository.deleteInventory(inventory)

                // REFRESCAR LISTA
                _listInventory.value = inventoryRepository.getListInventory()

            } finally {
                _progresState.value = false
            }
        }
    }



    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                inventoryRepository.updateRepositoy(inventory)


                _listInventory.value = inventoryRepository.getListInventory()

            } finally {
                _progresState.value = false
            }
        }
    }


    fun totalProducto(price: Int, quantity: Int): Double {
        return (price * quantity).toDouble()
    }
}
*/
