package com.univalle.miniproyecto1.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.univalle.miniproyecto1.R
import com.univalle.miniproyecto1.view.MainActivity

private const val ACTION_TOGGLE_BALANCE = "com.univalle.miniproyecto1.TOGGLE_BALANCE"
private const val PREFS_NAME = "widget_prefs"
private const val DATA_PREFS = "app_data"
private const val KEY_TOTAL_BALANCE = "total_balance"

class InventoryWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        // Manejar el click del ojo
        if (intent.action == ACTION_TOGGLE_BALANCE) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            if (appWidgetId != -1) {
                toggleBalance(context, appWidgetId)
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
    // Obtener estado de visualización del ojo abierto y cerrado
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val showBalance = prefs.getBoolean("show_balance_$appWidgetId", false)

    // Obtener el SALDO guardado por la App
    val dataPrefs = context.getSharedPreferences(DATA_PREFS, Context.MODE_PRIVATE)
    val totalBalance = dataPrefs.getFloat(KEY_TOTAL_BALANCE, 0.0f).toDouble()

    val views = RemoteViews(context.packageName, R.layout.inventory_widget)

    // Configurar Textos
    views.setTextViewText(R.id.widget_inventory_title, "Inventory")
    views.setTextViewText(R.id.widget_question_text, "¿Cuánto tengo de inventario?")

    // Configurar Icono Ojo
    val eyeIcon = if (showBalance) R.drawable.eye_image else R.drawable.eye_off_image
    views.setImageViewResource(R.id.widget_eye_icon, eyeIcon)

    // Configurar el valor a mostrar
    val displayBalance = if (showBalance) {
        "$ %.2f".format(totalBalance)
    } else {
        "$ ****"
    }
    views.setTextViewText(R.id.widget_inventory_balance, displayBalance)


    // Click en el Ojo
    val intent = Intent(context, InventoryWidget::class.java).apply {
        action = ACTION_TOGGLE_BALANCE
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, appWidgetId, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.widget_eye_icon, pendingIntent)

    // Click en Configuración
    val launchIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("open_fragment", "login")
    }
    val launchPendingIntent = PendingIntent.getActivity(
        context, 111, launchIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.widget_manage_icon, launchPendingIntent)

    // Actualizar el widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun toggleBalance(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val current = prefs.getBoolean("show_balance_$appWidgetId", false)
    prefs.edit().putBoolean("show_balance_$appWidgetId", !current).apply()

    val appWidgetManager = AppWidgetManager.getInstance(context)
    updateAppWidget(context, appWidgetManager, appWidgetId)
}