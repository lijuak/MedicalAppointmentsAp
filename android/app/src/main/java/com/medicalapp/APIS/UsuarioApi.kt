package com.medicalapp.APIS

import com.medicalapp.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuarioApi {

    /**
     * Este es ahora el ÚNICO endpoint que la app necesita para la autenticación.
     * Recibe un token de Firebase y devuelve el perfil de usuario de nuestro backend
     * (ya sea uno existente o uno recién creado por el servidor).
     */
    @POST("auth/verify-token")
    fun verificarToken(@Body token: Map<String, String>): Call<Usuario>
}
