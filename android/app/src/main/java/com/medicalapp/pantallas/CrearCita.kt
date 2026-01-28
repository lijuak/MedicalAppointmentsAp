package com.medicalapp.pantallas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.medicalapp.R
import com.medicalapp.RetrofitClient
import com.medicalapp.databinding.ActivityCrearCitaBinding
import com.medicalapp.model.Cita
import com.medicalapp.model.Doctor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CrearCita : AppCompatActivity() {

    private lateinit var binding: ActivityCrearCitaBinding
    private lateinit var cache: SharedPreferences
    
    private var doctoresList: List<Doctor> = emptyList()
    private var selectedDoctor: Doctor? = null
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Agendar Cita Médica"

        setupBottomNavigation()
        cargarDoctores()
        setupDatePicker()
        setupTimePicker()

        binding.btnCrearCitaGuardar.setOnClickListener {
            guardarCita()
        }
    }

    private fun cargarDoctores() {
        lifecycleScope.launch {
            try {
                doctoresList = RetrofitClient.doctorApi.obtenerTodosDoctores()
                
                val doctorNames = doctoresList.map { "${it.nombre} - ${it.especialidad}" }
                val adapter = ArrayAdapter(
                    this@CrearCita,
                    android.R.layout.simple_spinner_item,
                    doctorNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerDoctor.adapter = adapter

                binding.spinnerDoctor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedDoctor = doctoresList[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedDoctor = null
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@CrearCita, "Error al cargar médicos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupDatePicker() {
        binding.etFechaCita.setOnClickListener {
            val year = selectedDate.get(Calendar.YEAR)
            val month = selectedDate.get(Calendar.MONTH)
            val day = selectedDate.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate.set(Calendar.YEAR, selectedYear)
                selectedDate.set(Calendar.MONTH, selectedMonth)
                selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay)
                
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etFechaCita.setText(dateFormat.format(selectedDate.time))
            }, year, month, day).show()
        }
    }

    private fun setupTimePicker() {
        binding.etHoraCita.setOnClickListener {
            if (selectedDoctor == null) {
                Toast.makeText(this, "Primero seleccione un médico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fechaTexto = binding.etFechaCita.text.toString()
            if (fechaTexto.isEmpty()) {
                Toast.makeText(this, "Primero seleccione una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Consultar citas existentes para este doctor
                    // El formato de fecha en el edittext es dd/MM/yyyy, necesitamos yyyy-MM-dd para el filtro si el backend lo requiere
                    // Pero como el backend usa LocalDateTime, filtraremos localmente por ahora para mayor seguridad
                    val filtro = com.medicalapp.model.CitaFiltroRequest(idDoctor = selectedDoctor?.id)
                    val citasExistentes = RetrofitClient.citaApi.filtrarCitas(filtro)
                    
                    val dateStr = String.format("%04d-%02d-%02d", 
                        selectedDate.get(Calendar.YEAR), 
                        selectedDate.get(Calendar.MONTH) + 1, 
                        selectedDate.get(Calendar.DAY_OF_MONTH))

                    val reservedTimes = citasExistentes.filter { 
                        it.fechaHoraCita?.startsWith(dateStr) == true 
                    }.map { 
                        it.fechaHoraCita?.substring(11, 16) ?: ""
                    }

                    val timeSlots = mutableListOf<String>()
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, 8)
                    calendar.set(Calendar.MINUTE, 0)

                    while (calendar.get(Calendar.HOUR_OF_DAY) < 14 || (calendar.get(Calendar.HOUR_OF_DAY) == 14 && calendar.get(Calendar.MINUTE) == 0)) {
                        val slot = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
                        if (!reservedTimes.contains(slot)) {
                            timeSlots.add(slot)
                        }
                        calendar.add(Calendar.MINUTE, 15)
                    }

                    if (timeSlots.isEmpty()) {
                        Toast.makeText(this@CrearCita, "No hay horarios disponibles para este día", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val builder = androidx.appcompat.app.AlertDialog.Builder(this@CrearCita)
                    builder.setTitle("Seleccione una hora disponible")
                    builder.setItems(timeSlots.toTypedArray()) { _, which ->
                        val selectedSlot = timeSlots[which]
                        binding.etHoraCita.setText(selectedSlot)
                        
                        val parts = selectedSlot.split(":")
                        selectedTime.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                        selectedTime.set(Calendar.MINUTE, parts[1].toInt())
                    }
                    builder.show()

                } catch (e: Exception) {
                    Toast.makeText(this@CrearCita, "Error al comprobar disponibilidad: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    finish()
                    true
                }
                R.id.nav_crear_cita -> {
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
        binding.bottomNavigation.selectedItemId = R.id.nav_crear_cita
    }

    private fun guardarCita() {
        val sintomas = binding.etSintomas.text.toString().trim()
        val fechaTexto = binding.etFechaCita.text.toString()
        val horaTexto = binding.etHoraCita.text.toString()

        if (selectedDoctor == null) {
            Toast.makeText(this, "Por favor seleccione un médico", Toast.LENGTH_SHORT).show()
            return
        }

        if (fechaTexto.isEmpty()) {
            Toast.makeText(this, "Por favor seleccione una fecha", Toast.LENGTH_SHORT).show()
            return
        }

        if (horaTexto.isEmpty()) {
            Toast.makeText(this, "Por favor seleccione una hora", Toast.LENGTH_SHORT).show()
            return
        }

        if (sintomas.isEmpty()) {
            Toast.makeText(this, "Por favor describa sus síntomas", Toast.LENGTH_SHORT).show()
            return
        }

        // Combinar fecha y hora
        val appointmentDateTime = Calendar.getInstance()
        appointmentDateTime.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
        appointmentDateTime.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
        appointmentDateTime.set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
        appointmentDateTime.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
        appointmentDateTime.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))
        appointmentDateTime.set(Calendar.SECOND, 0)

        // Formato ISO 8601 para enviar al backend
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val fechaHoraISO = isoFormat.format(appointmentDateTime.time)

        val patientId = cache.getLong("id", 0L)

        // Leer el valor de urgencia del toggle
        val esUrgente = binding.toggleUrgencia.checkedButtonId == R.id.btnUrgenciaSi
        val nivelUrgencia = if (esUrgente) 3 else 1 // 3 = urgente, 1 = normal

        val nuevaCita = Cita(
            id = null,
            nombreDoctor = selectedDoctor!!.nombre,
            especialidad = selectedDoctor!!.especialidad,
            fechaHoraCita = fechaHoraISO,
            sintomas = sintomas,
            idDoctor = selectedDoctor!!.id,
            idPaciente = patientId,
            nivelUrgencia = nivelUrgencia,
            precioConsulta = 50, // Valor por defecto
            duracionMinutos = 30 // Valor por defecto
        )

        lifecycleScope.launch {
            try {
                RetrofitClient.citaApi.crearCita(nuevaCita)
                Toast.makeText(this@CrearCita, "¡Cita médica agendada correctamente!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@CrearCita, MisCitas::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@CrearCita, "Error al agendar la cita: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
