package nain.com.io

import nain.com.io.response.LoginResponse
import nain.com.io.response.SimpleResponse
import nain.com.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @GET("user")
    @Headers("Accept: application/json")
    fun getUser(@Header("Authorization") authHeader: String): Call<User>

    @POST("user")
    @Headers("Accept: application/json")
    fun postUser(
        @Header("Authorization") authHeader: String,
        @Query("name") name: String,
        @Query("phone") phone: String,
        @Query("address") address: String
    ): Call<Void>

    @GET("specialties") // anotacion de la clase abstracta gracias a retrofic
    fun getSpecialties(): Call<ArrayList<Specialty>>

    @GET("specialties/{specialty}/doctors") // anotacion de la clase abstracta gracias a retrofic
    fun getDoctor(@Path("specialty") especialtyId: Int): Call<ArrayList<Doctor>> // especialtyId => nombre reasisgnado al parametro

    @GET("schedule/hours")
    fun getHours(@Query("doctor_id") doctorId: Int, @Query("date") date: String) // @Query es pra enviar parametros tipo post
            // mo recifimos un array sino recifimos un objetos dentro dos arraylist los vamos a matear ahora
            : Call<Schedule>

    @POST("login")
    fun postLogin(@Query("email") email: String, @Query("password") password: String) // @Query es para enviar parametros tipo post
        : Call<LoginResponse>

    @POST("logout")
    fun postLogout(@Header("Authorization") authHeader: String): Call<Void> // Void pra ignorarlo la respuesta

    @GET("appointments")
    fun getAppointments(@Header("Authorization") authHeader: String)
            : Call<ArrayList<Appointment>> // Void pra ignorarlo la respuesta

    @POST("appointments")
    @Headers("Accept: application/json")
    fun storeAppointments(
        @Header("Authorization") authHeader: String,
        @Query("description") description: String,
        @Query("specialty_id") specialtyId: Int,
        @Query("doctor_id") doctorId: Int,
        @Query("scheduled_date") scheduledDate: String,
        @Query("scheduled_time") scheduledTime: String,
        @Query("type") type: String
    ) : Call<SimpleResponse>


    @POST("register")
    @Headers("Accept: application/json")
    fun postRegister(
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("password_confirmation") passwordConfirmation: String
    ) : Call<LoginResponse>

    @POST("fcm/token")
    fun postToken(
        @Header("Authorization") authHeader: String,
        @Query("device_token") token: String
    ) : Call<Void>

    // companion para llavar sin la necesidad de factory
    companion object Factory {
        private const val BASE_URL = "http://64.225.53.166/api/"

        fun create(): ApiService {
//            Interceptor para saber las pediciones que esta haciendo la url
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

}