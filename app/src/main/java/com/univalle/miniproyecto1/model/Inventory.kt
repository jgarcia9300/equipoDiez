package com.univalle.miniproyecto1.model

import java.io.Serializable

data class Inventory(
    // Guarda id del documento
    var id: String = "",
    val code: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0
) : Serializable