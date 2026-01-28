package com.medicalapp.pantallas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.medicalapp.R
import com.medicalapp.databinding.ActivityMiPerfilBinding
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class MiPerfil : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMiPerfilBinding
    private lateinit var mMap: GoogleMap
    private lateinit var firebaseAuth: FirebaseAuth
    private var selectedLocation: LatLng? = null
    private var currentMarker: com.google.android.gms.maps.model.Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Mi Perfil"

        setupBottomNavigation()
        cargarNombreUsuario()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        binding.cardNombreUsuario.setOnClickListener {
            mostrarDialogoEditarNombre()
        }

        binding.btnElegirSitio.setOnClickListener {
            if (selectedLocation != null) {
                guardarUbicacionFavorita()
            } else {
                Toast.makeText(this, "Por favor, toca un punto en el mapa primero", Toast.LENGTH_SHORT).show()
            }
        }

        // SearchView para buscar ubicaciones
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    buscarUbicacion(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun cargarNombreUsuario() {
        val cache = getSharedPreferences("cache", MODE_PRIVATE)
        val username = cache.getString("username", null)
        
        if (!username.isNullOrEmpty()) {
            binding.tvNombreUsuario.text = "Nombre de usuario: $username"
        } else {
            binding.tvNombreUsuario.text = "Nombre de usuario: Usuario"
        }
    }

    private fun mostrarDialogoEditarNombre() {
        val editText = EditText(this)
        editText.hint = "Nuevo nombre de usuario"
        
        val cache = getSharedPreferences("cache", MODE_PRIVATE)
        val usernameActual = cache.getString("username", "")
        editText.setText(usernameActual)

        AlertDialog.Builder(this)
            .setTitle("Editar Nombre de Usuario")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = editText.text.toString().trim()
                if (nuevoNombre.isNotEmpty()) {
                    cache.edit().putString("username", nuevoNombre).apply()
                    binding.tvNombreUsuario.text = "Nombre de usuario: $nuevoNombre"
                    Toast.makeText(this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun buscarUbicacion(query: String) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocationName(query, 1)
            
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)
                
                mMap.clear()
                currentMarker = mMap.addMarker(MarkerOptions().position(latLng).title(query))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                selectedLocation = latLng
            } else {
                Toast.makeText(this, "No se encontró la ubicación", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al buscar ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarUbicacionFavorita() {
        selectedLocation?.let { location ->
            val cache = getSharedPreferences("cache", MODE_PRIVATE)
            cache.edit().apply {
                putFloat("fav_lat", location.latitude.toFloat())
                putFloat("fav_lng", location.longitude.toFloat())
                apply()
            }
            Toast.makeText(this, "Ubicación favorita guardada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_perfil

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mis_citas -> {
                    startActivity(Intent(this, MisCitas::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                R.id.nav_crear_cita -> {
                    startActivity(Intent(this, CrearCita::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun cerrarSesion() {
        firebaseAuth.signOut()
        LoginManager.getInstance().logOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            val cache = getSharedPreferences("cache", MODE_PRIVATE)
            cache.edit().clear().apply()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.nav_perfil
        cargarUbicacionFavorita()
    }

    private fun cargarUbicacionFavorita() {
        val cache = getSharedPreferences("cache", MODE_PRIVATE)
        val lat = cache.getFloat("fav_lat", 0f)
        val lng = cache.getFloat("fav_lng", 0f)
        
        if (lat != 0f && lng != 0f) {
            selectedLocation = LatLng(lat.toDouble(), lng.toDouble())
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Cargar ubicación favorita o mostrar Madrid por defecto
        val cache = getSharedPreferences("cache", MODE_PRIVATE)
        val lat = cache.getFloat("fav_lat", 40.416775f)
        val lng = cache.getFloat("fav_lng", -3.703790f)
        
        val location = LatLng(lat.toDouble(), lng.toDouble())
        selectedLocation = location
        
        currentMarker = mMap.addMarker(MarkerOptions().position(location).title("Mi ubicación favorita"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))

        // Permitir al usuario hacer clic en el mapa para elegir ubicación
        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            currentMarker = mMap.addMarker(MarkerOptions().position(latLng).title("Nueva ubicación"))
            selectedLocation = latLng
            Toast.makeText(this, "Ubicación seleccionada. Presiona 'Elegir sitio favorito' para guardar", Toast.LENGTH_SHORT).show()
        }
    }
}
