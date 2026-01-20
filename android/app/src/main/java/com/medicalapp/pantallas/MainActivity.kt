package com.medicalapp.pantallas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseAuth = FirebaseAuth.getInstance()

        // Comprueba si el usuario ya ha iniciado sesión
        if (firebaseAuth.currentUser != null) {
            // Si hay sesión, va a la pantalla principal de la app
            startActivity(Intent(this, MisCitas::class.java))
        } else {
            // Si no hay sesión, va a la pantalla de Login
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Cierra esta actividad para que el usuario no pueda volver a ella
        finish()
    }
}
