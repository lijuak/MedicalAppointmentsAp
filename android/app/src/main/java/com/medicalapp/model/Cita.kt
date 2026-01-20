package com.medicalapp.model

import com.google.gson.annotations.SerializedName

data class Cita(
    val id: Long? = null,
    @SerializedName("doctorName") val titulo: String,
    @SerializedName("specialty") val descripcion: String,
    @SerializedName("urgencyLevel") val temporada: Int?,
    @SerializedName("consultationFee") val dinero: Int?,
    @SerializedName("durationMinutes") val intensidad: Int?,
    @SerializedName("clinicLocationId") val cercania: Int?,
    @SerializedName("availabilityScore") val facilidad: Int?,
    @SerializedName("patientId") val creadorId: Long?
)
