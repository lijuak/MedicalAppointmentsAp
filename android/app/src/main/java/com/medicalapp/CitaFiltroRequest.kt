package com.medicalapp

import com.google.gson.annotations.SerializedName

data class CitaFiltroRequest(
    val id: Long? = null,
    @SerializedName("urgencyLevel") val temporada: Int? = null,
    @SerializedName("consultationFee") val dinero: Int? = null,
    @SerializedName("durationMinutes") val intensidad: Int? = null,
    @SerializedName("clinicLocationId") val cercania: Int? = null,
    @SerializedName("availabilityScore") val facilidad: Int? = null,
    @SerializedName("patientId") val creadorId: Long? = null
)
