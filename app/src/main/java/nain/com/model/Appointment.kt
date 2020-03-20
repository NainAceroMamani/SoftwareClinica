package nain.com.model

import com.google.gson.annotations.SerializedName

data class Appointment(
    val id: Int,
    val description : String,
    val type: String,
    val status: String,
    // scheduled_date nos llega del api le cambiamos el nombre para un estandar de Kotlin
    @SerializedName("scheduled_date") val scheduledDate: String,
    @SerializedName("scheduled_time_12") val scheduledTime: String,
    @SerializedName("created_at") val createdAt: String,

    // "doctor": { "id" : 3 , "name": "" } // objeto
    val specialty: Specialty, // se lo pasamos al objeto
    val doctor: Doctor
)