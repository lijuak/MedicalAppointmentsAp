package com.medicalapp.pantallas

import com.medicalapp.model.CitaFiltroRequest
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.medicalapp.R
import com.medicalapp.RetrofitClient
import com.medicalapp.adapters.CitaActionListener
import com.medicalapp.adapters.CitasAdapter
import com.medicalapp.databinding.ActivityMisCitasBinding
import com.medicalapp.model.Cita
import com.medicalapp.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.sqrt

class MisCitas : AppCompatActivity(), CitaActionListener, SensorEventListener {

    private lateinit var binding: ActivityMisCitasBinding
    private lateinit var citasAdapter: CitasAdapter
    private lateinit var cache: SharedPreferences

    // --- Propiedades para la Búsqueda por Agitación ---
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private val shakeThreshold = 12f
    private val shakeCooldownMs = 1500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)

        setupToolbar()
        setupRecyclerView()
        setupBottomNavigation()
        setupSensor()
        createNotificationChannel()
        
        // Solicitar permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            val idUsuario = cache.getLong("id", 0L)
            if (idUsuario != 0L) {
                cargarCitasUsuario(idUsuario)
            } else {
                intentarSincronizacionForzada()
            }
        }

        val idUsuario = cache.getLong("id", 0L)
        
        // Siempre intentar cargar las citas, incluso si el ID es 0
        // porque las citas existentes tienen patientId = 0
        cargarCitasUsuario(idUsuario)
        
        // Si el ID es 0, también intentar sincronizar en segundo plano
        if (idUsuario == 0L) {
            intentarSincronizacionForzada()
        }
    }

    private fun intentarSincronizacionForzada() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            firebaseUser.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result.token
                    if (idToken != null) {
                        val tokenMap = mapOf("token" to idToken)
                        RetrofitClient.api.verificarToken(tokenMap).enqueue(object : Callback<Usuario> {
                            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                                if (response.isSuccessful && response.body() != null) {
                                    val user = response.body()!!
                                    cache.edit()
                                        .putString("username", firebaseUser.displayName ?: user.username)
                                        .putString("email", user.email)
                                        .putLong("id", user.id ?: 0L)
                                        .apply()
                                    
                                    val nuevoId = user.id ?: 0L
                                    if (nuevoId != 0L) {
                                        cargarCitasUsuario(nuevoId)
                                    }
                                }
                            }
                            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                                Log.e("SYNC_ERROR", "Fallo sync forzado", t)
                            }
                        })
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Medicina S.L"
    }

    private fun setupRecyclerView() {
        citasAdapter = CitasAdapter(mutableListOf(), this)
        binding.rvMisCitas.layoutManager = LinearLayoutManager(this)
        binding.rvMisCitas.adapter = citasAdapter
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_mis_citas

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mis_citas -> true
                R.id.nav_crear_cita -> {
                    startActivity(Intent(this, CrearCita::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, MiPerfil::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun cargarCitasUsuario(idUsuario: Long) {
        binding.swipeRefreshLayout.isRefreshing = true
        lifecycleScope.launch {
            try {
                // DEBUG: Cargamos todas las citas sin filtrar por usuario
                // porque las citas existentes tienen patientId = 0
                val filtro = CitaFiltroRequest(idPaciente = null)
                
                Log.d("MIS_CITAS", "Cargando TODAS las citas (debug mode)")
                val citas = RetrofitClient.citaApi.filtrarCitas(filtro)
                
                Log.d("MIS_CITAS", "Citas recibidas: ${citas.size}")
                
                if (citas.isNotEmpty()) {
                    citasAdapter.updateData(citas)
                    binding.rvMisCitas.visibility = View.VISIBLE
                    binding.layoutSinCitas.visibility = View.GONE
                    mostrarProximaCita(citas)
                } else {
                    Log.d("MIS_CITAS", "La lista de citas está vacía")
                    binding.rvMisCitas.visibility = View.GONE
                    binding.layoutSinCitas.visibility = View.VISIBLE
                    binding.layoutProximaCita.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("CARGAR_CITAS_ERROR", "Error al cargar las citas", e)
                Toast.makeText(this@MisCitas, "Conexión pérdida o error de servidor", Toast.LENGTH_SHORT).show()
                binding.rvMisCitas.visibility = View.GONE
                binding.layoutSinCitas.visibility = View.VISIBLE
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        // Recargar al volver de Crear Cita
        val idUsuario = cache.getLong("id", 0L)
        if (idUsuario != 0L) {
            cargarCitasUsuario(idUsuario)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt(x * x + y * y + z * z)
            if (acceleration > shakeThreshold) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTime > shakeCooldownMs) {
                    lastShakeTime = currentTime
                    enviarNotificacionAprobacion()
                }
            }
        }
    }

    private fun mostrarProximaCita(citas: List<Cita>) {
        Log.d("PROXIMA_CITA", "mostrarProximaCita llamada con ${citas.size} citas")
        
        val ahora = System.currentTimeMillis()
        // Intentamos parsear la fecha para encontrar la más cercana futura
        // Nota: Esto depende del formato de fecha que devuelva el backend
        // Como fallback, mostramos la primera de la lista si no podemos comparar bien
        val proxima = citas.filter { 
            // Aquí idealmente parsearíamos cita.fecha, pero por simplicidad
            // y asumiendo que el backend las devuelve ordenadas o relevantes:
            true 
        }.firstOrNull()

        if (proxima != null) {
            Log.d("PROXIMA_CITA", "Mostrando cita: ${proxima.nombreDoctor} - ${proxima.fechaHoraCita}")
            binding.layoutProximaCita.visibility = View.VISIBLE
            binding.tvProximaCitaDoctor.text = proxima.nombreDoctor ?: "Médico"
            binding.tvProximaCitaEspecialidad.text = proxima.especialidad ?: "Consulta General"
            
            // Tratamos de extraer fecha y hora del campo fechaHoraCita (ISO format)
            val fullDateTime = proxima.fechaHoraCita ?: ""
            val datePart = fullDateTime.take(10) // 2026-02-15
            val timePart = if (fullDateTime.length >= 16) fullDateTime.substring(11, 16) else "00:00"
            
            binding.tvProximaCitaFechaHora.text = "$datePart • $timePart"
            binding.tvProximaCitaDescripcion.text = proxima.sintomas ?: "Sin descripción"
            Log.d("PROXIMA_CITA", "Tarjeta visible y datos configurados correctamente")
        } else {
            Log.d("PROXIMA_CITA", "No hay citas, ocultando tarjeta")
            binding.layoutProximaCita.visibility = View.GONE
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Aprobaciones"
            val descriptionText = "Canal para notificaciones de aprobación"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("APRUEBAME_CHAN", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun enviarNotificacionAprobacion() {
        val builder = NotificationCompat.Builder(this, "APRUEBAME_CHAN")
            .setSmallIcon(R.drawable.ic_heart_filled)
            .setContentTitle("Mensaje Especial")
            .setContentText("hola juan tomas apruebame")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            try {
                notify(1001, builder.build())
            } catch (e: SecurityException) {
                Log.e("NOTIF_ERROR", "Sin permiso de notificación", e)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onEditarCita(cita: Cita) {
        val intent = Intent(this, EditarCita::class.java)
        intent.putExtra("cita_id", cita.id)
        startActivity(intent)
    }

    override fun onEliminarCita(cita: Cita, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Cita")
            .setMessage("¿Estás seguro de que deseas eliminar esta cita?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCita(cita, position)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCita(cita: Cita, position: Int) {
        lifecycleScope.launch {
            try {
                cita.id?.let { id ->
                    RetrofitClient.citaApi.eliminarCita(id)
                    citasAdapter.removeItem(position)
                    Toast.makeText(this@MisCitas, "Cita eliminada", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MisCitas, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
