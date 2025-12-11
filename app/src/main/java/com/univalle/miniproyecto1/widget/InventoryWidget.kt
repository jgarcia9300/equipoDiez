package com.univalle.miniproyecto1.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.view.LoginActivity
import com.univalle.miniproyecto1.view.MainActivity
import com.univalle.miniproyecto1.view.fragment.HomeFragment
//import com.univalle.miniproyecto1.view.fragment.LoginFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

private const val ACTION_TOGGLE_BALANCE = "com.univalle.miniproyecto1.TOGGLE_BALANCE"
private const val ACTION_CONFIG_BALANCE = "com.univalle.miniproyecto1.CONFIG.BALANCE"

class InventoryWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_TOGGLE_BALANCE) {

            // 🔹 1. Verificar si el usuario está logueado en Firebase
            val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

            if (!isLoggedIn) {
                // Usuario NO logueado → mostrar aviso
                Toast.makeText(context, "Debes iniciar sesión para ver esta información", Toast.LENGTH_SHORT).show()

                // Opcional: abrir LoginActivity
                val loginIntent = Intent(context, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(loginIntent)

                return  // Evita que toggleBalance se ejecute
            }

            // 🔹 2. Obtener ID del widget
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)

            // 🔹 3. Ejecutar acción solo si el ID es válido
            if (appWidgetId != -1) {
                toggleBalance(context, appWidgetId)
            }
        }

        if (intent.action == ACTION_CONFIG_BALANCE) {

            // 🔹 1. Verificar si el usuario está logueado en Firebase
            val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

            if (!isLoggedIn) {
                // Usuario NO logueado → mostrar aviso
                Toast.makeText(context, "Debes iniciar sesión para ver esta información", Toast.LENGTH_SHORT).show()

                // Opcional: abrir LoginActivity
                val loginIntent = Intent(context, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(loginIntent)

                return  // Evita que toggleBalance se ejecute
            }

            // 🔹 2. Obtener ID del widget
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)

            // 🔹 3. Ejecutar acción solo si el ID es válido
            if (appWidgetId != -1) {
                val homeIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(homeIntent)
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
    val showBalance = prefs.getBoolean("show_balance_$appWidgetId", false)

    val views = RemoteViews(context.packageName, R.layout.inventory_widget)

    // Textos
    views.setTextViewText(R.id.widget_inventory_title, "Inventory")
    views.setTextViewText(R.id.widget_question_text, "¿Cuánto tengo de inventario?")

    val eyeIcon = if (showBalance) R.drawable.eye_image else R.drawable.eye_off_image
    views.setImageViewResource(R.id.widget_eye_icon, eyeIcon)

    // Evento click para el ojo
    val intent = Intent(context, InventoryWidget::class.java).apply {
        action = ACTION_TOGGLE_BALANCE
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        appWidgetId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    views.setOnClickPendingIntent(R.id.widget_eye_icon, pendingIntent)



    // icono configuracion
    val intentConfig = Intent(context, InventoryWidget::class.java).apply {
        action = ACTION_CONFIG_BALANCE
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }

    val pendingIntentConfig = PendingIntent.getBroadcast(
        context,
        appWidgetId,
        intentConfig,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    views.setOnClickPendingIntent(R.id.widget_manage_icon, pendingIntentConfig)


    // consulta en base de datos

    CoroutineScope(Dispatchers.IO).launch {

        // 1. Obtener el ID del usuario logueado
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        var totalInventory = 0.0 // Inicializar el balance

        // verificación de login para asegurar que la consulta solo se ejecute si hay usuario.
        if (userId != null) {
            try {
                // 2. Acceder a la colección de productos.

                val inventorySnapshot = FirebaseFirestore.getInstance()
                    .collection("products")
                    .get()
                    .await() // Espera el resultado de forma suspensiva

                // 3. Iterar sobre los documentos y sumar el valor (precio * cantidad)
                for (document in inventorySnapshot.documents) {

                    // Los campos de la base de datos son 'price' (Double) y 'quantity' (Long/Int)
                    val price = document.getDouble("price") ?: 0.0
                    val quantityLong = document.getLong("quantity") ?: 0L // Obtener como Long
                    val quantity = quantityLong.toDouble() // Convertir a Double para el cálculo

                    // Calcular el valor del stock de ese producto y sumarlo al total
                    totalInventory += (price * quantity)
                }

            } catch (e: Exception) {
                // Manejar errores de conexión o permisos
                Log.e("InventoryWidget", "Error al leer datos de Firebase: ${e.message}")
                totalInventory = 0.0 // Mostrar 0.0 o un valor seguro en caso de error
            }
        } else {
            // Usuario no logueado: totalInventory sigue siendo 0.0
        }

        // 4. Formatear y Mostrar el resultado (dentro de la Coroutine)

        val formatter = NumberFormat.getNumberInstance(Locale.GERMANY)
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 2

        val displayBalance =

            if(showBalance) "$ ${formatter.format(totalInventory)}"
            else "$****"

        // Actualizar la vista remota con el balance calculado
        views.setTextViewText(R.id.widget_inventory_balance, displayBalance)

        // 5. Notificar al sistema que el widget ha cambiado
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}


private fun toggleBalance(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
    val current = prefs.getBoolean("show_balance_$appWidgetId", false)

    prefs.edit().putBoolean("show_balance_$appWidgetId", !current).apply()

    val appWidgetManager = AppWidgetManager.getInstance(context)
    updateAppWidget(context, appWidgetManager, appWidgetId)
}