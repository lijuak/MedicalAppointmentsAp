package com.medicalapp.pantallas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.medicalapp.RetrofitClient
import com.medicalapp.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseAuth = FirebaseAuth.getInstance()
        val cache = getSharedPreferences("cache", MODE_PRIVATE)
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser != null) {
            val localId = cache.getLong("id", 0L)
            if (localId == 0L) {
                // Sincronización fallback si hay sesión pero no hay ID en caché
                sincronizarYSaltar(firebaseUser)
            } else {
                startActivity(Intent(this, MisCitas::class.java))
                finish()
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun sincronizarYSaltar(firebaseUser: FirebaseUser) {
        firebaseUser.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result.token
                if (idToken != null) {
                    val tokenMap = mapOf("token" to idToken)
                    RetrofitClient.api.verificarToken(tokenMap).enqueue(object : Callback<Usuario> {
                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                            if (response.isSuccessful && response.body() != null) {
                                val user = response.body()!!
                                val cache = getSharedPreferences("cache", MODE_PRIVATE)
                                cache.edit()
                                    .putString("username", firebaseUser.displayName ?: user.username)
                                    .putString("email", user.email)
                                    .putLong("id", user.id ?: 0L)
                                    .apply()
                            }
                            startActivity(Intent(this@MainActivity, MisCitas::class.java))
                            finish()
                        }
                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
                            // Si falla, vamos a login para re-autenticar
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            finish()
                        }
                    })
                } else {
                    irALogin()
                }
            } else {
                irALogin()
            }
        }
    }

    private fun irALogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
