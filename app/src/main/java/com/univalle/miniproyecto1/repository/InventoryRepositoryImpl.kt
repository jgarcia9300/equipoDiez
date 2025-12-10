package com.univalle.miniproyecto1.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.miniproyecto1.model.Inventory
import com.univalle.miniproyecto1.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : InventoryRepository {

    // Autenticación con Firebase

    override suspend fun login(email: String, pass: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun register(email: String, pass: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, pass).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun logout() {
        auth.signOut()
    }

    // Verificamos si hay un usuario actual
    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Base de datos
    override suspend fun getProducts(): List<Inventory> {
        return try {
            // Obtenemos la colección "products"
            val snapshot = db.collection("products").get().await()
            // Convertimos cada documento a un objeto Inventory
            snapshot.documents.mapNotNull { document ->
                document.toObject(Inventory::class.java)?.apply {
                    // Importante: Guardamos el ID del documento dentro del objeto
                    id = document.id
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Agregar producto
    override suspend fun addProduct(product: Inventory) {
        try {
            db.collection("products").add(product).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Editar producto
    override suspend fun updateProduct(product: Inventory) {
        try {
            if (product.id.isNotEmpty()) {
                db.collection("products").document(product.id).set(product).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Eliminar producto
    override suspend fun deleteProduct(productId: String) {
        try {
            db.collection("products").document(productId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}