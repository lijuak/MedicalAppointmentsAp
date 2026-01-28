package com.medicalapp.model

import com.google.gson.annotations.SerializedName

data class CitaFiltroRequest(
    val id: Long? = null,
    @SerializedName("urgencyLevel") val nivelUrgencia: Int? = null,
    @SerializedName("consultationFee") val precioConsulta: Int? = null,
    @SerializedName("durationMinutes") val duracionMinutos: Int? = null,
    @SerializedName("clinicLocationId") val idClinica: Int? = null,
    @SerializedName("availabilityScore") val disponibilidad: Int? = null,
    @SerializedName("patientId") val idPaciente: Long? = null,
    @SerializedName("doctorId") val idDoctor: Long? = null
)
