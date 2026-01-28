package com.medicalapp.pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.medicalapp.R
import com.medicalapp.RetrofitClient
import com.medicalapp.databinding.ActivityEditarCitaBinding
import com.medicalapp.model.Cita
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EditarCita : AppCompatActivity() {

    private lateinit var binding: ActivityEditarCitaBinding
    private var citaId: Long? = null
    private var citaActual: Cita? = null

    private lateinit var cache: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        cache = getSharedPreferences("cache", MODE_PRIVATE)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Detalles de Consulta"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.open_drawer, R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // --- ACTUALIZAR HEADER CON USERNAME ---
        val headerView = binding.navView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.tvNavHeaderUsername)
        val username = cache.getString("username", "Usuario")
        usernameTextView.text = "BIENVENIDO,\n$username!"

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_inicio -> {
                    startActivity(Intent(this, Pantalla1::class.java))
                    finish()
                }
                R.id.menu_crear_cita -> {
                    startActivity(Intent(this, CrearCita::class.java))
                }
                R.id.menu_lista_citas -> {
                    startActivity(Intent(this, MisCitas::class.java))
                }
                R.id.menu_perfil -> {
                    startActivity(Intent(this, MiPerfil::class.java))
                }
                R.id.menu_cerrar_sesion -> {
                    cerrarSesion()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        citaId = intent.getLongExtra("CITA_ID", -1)

        if (citaId == -1L) {
            Toast.makeText(this, "Error: ID de cita no válido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        cargarDatosDeLaCita()

        binding.btnGuardarCambios.setOnClickListener {
            actualizarCita()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cerrarSesion() {
        auth.signOut()
        googleSignInClient.signOut()
        cache.edit().clear().apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun cargarDatosDeLaCita() {
        lifecycleScope.launch {
            try {
                val cita = RetrofitClient.citaApi.getCitaPorId(citaId!!)
                citaActual = cita
                binding.etTituloCita.setText(cita.nombreDoctor)
                binding.etDescripcionCita.setText(cita.especialidad)
                
                // Mapeo inverso para los RadioGroups (Asumiendo IDs médicos en el layout)
                // Nota: Si el layout aún tiene IDs viejos, esto funcionará igual si se mantienen los mismos RadioButtons
                binding.groupTemporada.check(when (cita.nivelUrgencia) { 1 -> R.id.btnTemporadaBaja; 4 -> R.id.btnTemporadaAlta; else -> R.id.btnTemporadaMedia })
                binding.groupDinero.check(when (cita.precioConsulta) { 1 -> R.id.btnDineroBajo; 3 -> R.id.btnDineroAlto; else -> R.id.btnDineroMedio })
                binding.groupIntensidad.check(when (cita.duracionMinutos) { 1 -> R.id.btnIntensidadBaja; 3 -> R.id.btnIntensidadAlta; else -> R.id.btnIntensidadMedia })
                // Campos removidos: idClinica y disponibilidad ya no existen en el modelo
            } catch (e: Exception) {
                Toast.makeText(this@EditarCita, "Error al cargar los datos: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun actualizarCita() {
        val doctorName = binding.etTituloCita.text.toString().trim()
        val specialty = binding.etDescripcionCita.text.toString().trim()
        
        if (doctorName.isEmpty() || specialty.isEmpty()) {
            Toast.makeText(this, "El nombre del doctor y el motivo no pueden estar vacíos", Toast.LENGTH_SHORT).show()
            return
        }

        val urgencyLevel = when (binding.groupTemporada.checkedButtonId) { 
            R.id.btnTemporadaBaja -> 1 
            R.id.btnTemporadaAlta -> 3 
            else -> 2 
        }
        val consultationFee = when (binding.groupDinero.checkedButtonId) { R.id.btnDineroBajo -> 1; R.id.btnDineroAlto -> 3; else -> 2 }
        val durationMinutes = when (binding.groupIntensidad.checkedButtonId) { R.id.btnIntensidadBaja -> 1; R.id.btnIntensidadAlta -> 3; else -> 2 }
        val clinicLocationId = when (binding.groupCercania.checkedButtonId) { R.id.btnCercaniaBaja -> 3; R.id.btnCercaniaAlta -> 1; else -> 2 }
        val availabilityScore = when (binding.groupFacilidad.checkedButtonId) { R.id.btnFacilidadBaja -> 3; R.id.btnFacilidadAlta -> 1; else -> 2 }
        
        val patientId = citaActual?.idPaciente ?: cache.getLong("id", 0L)

        val citaActualizada = Cita(
            id = citaId!!,
            nombreDoctor = doctorName,
            especialidad = specialty,
            fechaHoraCita = citaActual?.fechaHoraCita,
            sintomas = citaActual?.sintomas,
            idDoctor = citaActual?.idDoctor,
            idPaciente = patientId,
            nivelUrgencia = urgencyLevel,
            precioConsulta = consultationFee,
            duracionMinutos = durationMinutes
        )

        lifecycleScope.launch {
            try {
                RetrofitClient.citaApi.actualizarCita(citaId!!, citaActualizada)
                Toast.makeText(this@EditarCita, "¡Cita médica actualizada correctamente!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@EditarCita, "Error al actualizar la cita: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
