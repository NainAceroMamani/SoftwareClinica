package nain.com.io

import nain.com.model.Doctor
import nain.com.model.Specialty
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("specialties") // anotacion de la clase abstracta gracias a retrofic
    abstract fun getSpecialties(): Call<ArrayList<Specialty>>

    @GET("specialties/{specialty}/doctors") // anotacion de la clase abstracta gracias a retrofic
    abstract fun getDoctor(@Path("specialty") especialtyId: Int): Call<ArrayList<Doctor>> // especialtyId => nombre reasisgnado al parametro

    // companion para llavar sin la necesidad de factory
    companion object Factory {
        private const val BASE_URL = "http://64.225.53.166/api/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

}