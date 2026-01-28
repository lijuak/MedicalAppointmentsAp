package com.medicalapp.APIS

import com.medicalapp.model.Doctor
import retrofit2.http.*

interface DoctorApi {

    @GET("api/doctors")
    suspend fun obtenerTodosDoctores(): List<Doctor>

    @GET("api/doctors/{id}")
    suspend fun obtenerDoctorPorId(@Path("id") id: Long): Doctor

    @GET("api/doctors/specialty/{specialty}")
    suspend fun obtenerDoctoresPorEspecialidad(@Path("specialty") especialidad: String): List<Doctor>
}
