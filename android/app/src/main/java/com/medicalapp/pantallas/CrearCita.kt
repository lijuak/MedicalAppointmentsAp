package com.medicalapp.pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import com.medicalapp.R
import com.medicalapp.RetrofitClient
import com.medicalapp.databinding.ActivityCrearCitaBinding
import com.medicalapp.model.Cita
import kotlinx.coroutines.launch

class CrearCita : AppCompatActivity() {

    private lateinit var binding: ActivityCrearCitaBinding
    private lateinit var cache: SharedPreferences
    private var currentlyCheckedId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Crear Nueva Cita"

        currentlyCheckedId = binding.groupTemporada.checkedButtonId
        setupBottomNavigation()
        setupClearableToggleGroup()

        binding.btnCrearCitaGuardar.setOnClickListener {
            guardarCita()
        }
    }

    private fun setupClearableToggleGroup() {
        binding.groupTemporada.children.forEach { button ->
            button.setOnClickListener {
                if (currentlyCheckedId == button.id) {
                    binding.groupTemporada.clearChecked()
                    currentlyCheckedId = -1
                } else {
                    binding.groupTemporada.check(button.id)
                    currentlyCheckedId = button.id
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_crear_cita

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mis_citas -> {
                    startActivity(Intent(this, MisCitas::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish() // Cierra esta actividad para no apilarlas
                    true
                }
                R.id.nav_crear_cita -> {
                    // Ya estamos aquí
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, MiPerfil::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Asegura que el ítem de "Crear" esté seleccionado al volver a esta pantalla
        binding.bottomNavigation.selectedItemId = R.id.nav_crear_cita
    }

    private fun guardarCita() {
        val titulo = binding.etTituloCita.text.toString().trim()
        val descripcion = binding.etDescripcionCita.text.toString().trim()

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "El título y la descripción no pueden estar vacíos", Toast.LENGTH_SHORT).show()
            return
        }

        val temporada = when (binding.groupTemporada.checkedButtonId) {
            R.id.btnInvierno -> 1
            R.id.btnVerano -> 2
            R.id.btnOtono -> 3
            R.id.btnPrimavera -> 4
            else -> null
        }

        val dinero = binding.sliderDinero.value.toInt()
        val intensidad = binding.sliderIntensidad.value.toInt()
        val cercania = binding.sliderCercania.value.toInt()
        val facilidad = binding.sliderFacilidad.value.toInt()
        val creadorId = cache.getLong("id", 0L)

        val nuevaCita = Cita(
            id = null, // El ID lo genera el servidor
            titulo = titulo,
            descripcion = descripcion,
            temporada = temporada,
            dinero = dinero,
            intensidad = intensidad,
            cercania = cercania,
            facilidad = facilidad,
            creadorId = creadorId
        )

        lifecycleScope.launch {
            try {
                RetrofitClient.citaApi.crearCita(nuevaCita)
                Toast.makeText(this@CrearCita, "¡Aventura creada con éxito!", Toast.LENGTH_SHORT).show()
                // Vuelve a la lista de citas para ver la nueva creación
                startActivity(Intent(this@CrearCita, MisCitas::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@CrearCita, "Error al crear la cita: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
