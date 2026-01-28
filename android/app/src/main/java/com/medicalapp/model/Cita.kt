package com.medicalapp.model

import com.google.gson.annotations.SerializedName

data class Cita(
    val id: Long? = null,
    @SerializedName("doctorName") val nombreDoctor: String,
    @SerializedName("specialty") val especialidad: String?,
    @SerializedName("appointmentDateTime") val fechaHoraCita: String?, // ISO 8601 format
    @SerializedName("patientSymptoms") val sintomas: String?,
    @SerializedName("doctorId") val idDoctor: Long?,
    @SerializedName("patientId") val idPaciente: Long?,
    // Campos opcionales que pueden mantenerse para futuras funcionalidades
    @SerializedName("urgencyLevel") val nivelUrgencia: Int? = null,
    @SerializedName("consultationFee") val precioConsulta: Int? = null,
    @SerializedName("durationMinutes") val duracionMinutos: Int? = null,
    @SerializedName("status") val estado: String? = "RESERVADA"
)
