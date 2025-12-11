package com.univalle.miniproyecto1.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.data.InventoryDB
import com.univalle.miniproyecto1.view.LoginActivity
import com.univalle.miniproyecto1.view.MainActivity
import com.univalle.miniproyecto1.view.fragment.HomeFragment
//import com.univalle.miniproyecto1.view.fragment.LoginFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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


//    val launchIntent = Intent(context, MainActivity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        putExtra("open_fragment", "login")
//    }
//
//    val launchPendingIntent = PendingIntent.getActivity(
//        context,
//        111,
//        launchIntent,
//        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//    )
//
//    views.setOnClickPendingIntent(R.id.widget_manage_icon, launchPendingIntent)


    // consulta en base de datos
    CoroutineScope(Dispatchers.IO).launch {
        val db = InventoryDB.getDatabase(context)
        val total = db.inventoryDao().getInventoryTotalBalance() ?: 0.0

        val displayBalance =
            if (showBalance) "$ %.2f".format(total)
            else "$****"

        views.setTextViewText(R.id.widget_inventory_balance, displayBalance)

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