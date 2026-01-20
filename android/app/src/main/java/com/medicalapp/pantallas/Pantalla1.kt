package com.medicalapp.pantallas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Esta actividad ha quedado obsoleta.
 * Su única función ahora es redirigir inmediatamente a MisCitas,
 * que es la nueva pantalla principal de la aplicación.
 */
class Pantalla1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirige a la nueva pantalla principal
        val intent = Intent(this, MisCitas::class.java)
        // Añadimos flags para que esta actividad no quede en el historial de navegación.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
