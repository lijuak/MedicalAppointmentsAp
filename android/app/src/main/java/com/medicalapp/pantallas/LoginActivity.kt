package com.medicalapp.pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.medicalapp.R
import com.medicalapp.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.medicalapp.RetrofitClient
import com.medicalapp.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var cache: SharedPreferences
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var callbackManager: CallbackManager

    companion object {
        private const val TAG = "LoginActivity"
        private const val PENDING_FB_TOKEN_KEY = "pending_facebook_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        cache = getSharedPreferences("cache", MODE_PRIVATE)

        // Render HTML in TextView
        binding.tvRegister.text = HtmlCompat.fromHtml(getString(R.string.register_prompt), HtmlCompat.FROM_HTML_MODE_LEGACY)

        setupGoogleSignIn()
        setupFacebookSignIn()

        binding.btnLogin.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString()
            val pass = binding.tilPassword.editText?.text.toString()
            loginConEmail(email, pass)
        }

        binding.btnGoogle.setOnClickListener {
            iniciarSesionConGoogle()
        }

        binding.btnFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != RESULT_OK) {
                    cache.edit().remove(PENDING_FB_TOKEN_KEY).apply()
                    return@registerForActivityResult
                }

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val googleCredential = GoogleAuthProvider.getCredential(account.idToken!!, null)

                    val pendingFacebookToken = cache.getString(PENDING_FB_TOKEN_KEY, null)

                    if (pendingFacebookToken != null) {
                        cache.edit().remove(PENDING_FB_TOKEN_KEY).apply()
                        val facebookCredential = FacebookAuthProvider.getCredential(pendingFacebookToken)

                        firebaseAuth.signInWithCredential(googleCredential)
                            .addOnSuccessListener { authResult ->
                                val user = authResult.user
                                user?.linkWithCredential(facebookCredential)
                                    ?.addOnSuccessListener { linkResult ->
                                        Toast.makeText(this, "Tu cuenta de Facebook ha sido vinculada.", Toast.LENGTH_SHORT).show()
                                        verificarTokenConBackend(linkResult.user)
                                    }?.addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al vincular: ${e.message}", Toast.LENGTH_LONG).show()
                                        verificarTokenConBackend(user)
                                    }
                            }.addOnFailureListener { e ->
                                Toast.makeText(this, "Error al iniciar sesión con Google: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        firebaseAuth.signInWithCredential(googleCredential)
                            .addOnSuccessListener { authResult ->
                                verificarTokenConBackend(authResult.user)
                            }.addOnFailureListener { e ->
                                Toast.makeText(this, "Fallo en la autenticación con Firebase: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } catch (e: ApiException) {
                    cache.edit().remove(PENDING_FB_TOKEN_KEY).apply()
                    Toast.makeText(this, "Fallo en el inicio de sesión con Google: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun setupFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "Facebook login onSuccess")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "Facebook login onCancel")
                Toast.makeText(this@LoginActivity, "Inicio de sesión con Facebook cancelado.", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
                Log.e(TAG, "Facebook login onError", exception)
                Toast.makeText(this@LoginActivity, "Error en el inicio de sesión con Facebook: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val facebookCredential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(facebookCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    verificarTokenConBackend(task.result.user)
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        cache.edit().putString(PENDING_FB_TOKEN_KEY, token.token).apply()
                        LoginManager.getInstance().logOut()
                        AlertDialog.Builder(this)
                            .setTitle("Vincular Cuentas")
                            .setMessage("Ya tienes una cuenta con este email. Para usar Facebook, primero debes vincular las cuentas iniciando sesión con Google.")
                            .setPositiveButton("Vincular con Google") { _, _ -> iniciarSesionConGoogle() }
                            .setNegativeButton("Cancelar") { dialog, _ ->
                                cache.edit().remove(PENDING_FB_TOKEN_KEY).apply()
                                dialog.dismiss()
                            }.setCancelable(false).show()
                    } else {
                        Toast.makeText(this, "Fallo en la autenticación con Firebase: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun loginConEmail(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    verificarTokenConBackend(task.result.user)
                } else {
                    Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun iniciarSesionConGoogle() {
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun verificarTokenConBackend(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null) {
            Toast.makeText(this, "Error: No se pudo obtener el usuario de Firebase.", Toast.LENGTH_LONG).show()
            return
        }
        firebaseUser.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result.token
                if (idToken != null) {
                    val tokenMap = mapOf<String, String>("token" to idToken)
                    RetrofitClient.api.verificarToken(tokenMap).enqueue(object : Callback<Usuario> {
                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                            if (response.isSuccessful && response.body() != null) {
                                val usuarioBackend = response.body()!!
                                guardarDatosYNavegar(usuarioBackend, firebaseUser)
                            } else {
                                Toast.makeText(this@LoginActivity, "Error del servidor: ${response.code()}", Toast.LENGTH_LONG).show()
                                cerrarSesion()
                            }
                        }
                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
                            Toast.makeText(this@LoginActivity, "Error de conexión con el servidor: ${t.message}", Toast.LENGTH_LONG).show()
                            cerrarSesion()
                        }
                    })
                }
            } else {
                Toast.makeText(this, "Error al obtener autenticación.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarDatosYNavegar(user: Usuario, firebaseUser: FirebaseUser) {
        val displayName = firebaseUser.displayName
        val username = if (!displayName.isNullOrEmpty()) displayName else user.username ?: "Usuario"
        val email = user.email ?: ""
        val id = user.id ?: 0L

        cache.edit()
            .putString("username", username)
            .putString("email", email)
            .putLong("id", id)
            .apply()

        Toast.makeText(this@LoginActivity, "¡Bienvenido, $username!", Toast.LENGTH_SHORT).show()
        navegarAPantallaPrincipal()
    }

    private fun navegarAPantallaPrincipal() {
        val intent = Intent(this, MisCitas::class.java)
        startActivity(intent)
        finish()
    }

    private fun cerrarSesion() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        LoginManager.getInstance().logOut()
        cache.edit().clear().apply()
    }
}
