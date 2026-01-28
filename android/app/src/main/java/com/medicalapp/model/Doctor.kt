package com.medicalapp.model

import com.google.gson.annotations.SerializedName

data class Doctor(
    val id: Long,
    @SerializedName("name") val nombre: String,
    @SerializedName("specialty") val especialidad: String,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val telefono: String?
)
