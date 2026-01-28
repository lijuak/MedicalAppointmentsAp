package com.medicalapp.APIS

import com.medicalapp.model.CitaFiltroRequest
import com.medicalapp.model.Cita
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CitaApi {

    @POST("api/appointments/filter")
    suspend fun filtrarCitas(@Body filtro: CitaFiltroRequest): List<Cita>

    @POST("api/appointments")
    suspend fun crearCita(@Body cita: Cita): Cita

    @DELETE("api/appointments/{id}")
    suspend fun eliminarCita(@Path("id") id: Long)

    // --- NUEVOS ENDPOINTS PARA EDITAR ---

    @GET("api/appointments/{id}")
    suspend fun getCitaPorId(@Path("id") id: Long): Cita

    @PUT("api/appointments/{id}") // NOTE: Backend controller didn't seem to have PUT, only POST/GET/DELETE.
    suspend fun actualizarCita(@Path("id") id: Long, @Body cita: Cita): Cita
}
