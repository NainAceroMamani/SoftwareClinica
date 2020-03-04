package nain.com.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu.*
import nain.com.util.PreferenceHelper
import nain.com.util.PreferenceHelper.set
import nain.com.util.PreferenceHelper.get
import nain.com.R
import nain.com.io.ApiService
import nain.com.util.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private val apiService by lazy {
        ApiService.create()
    }

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        btnCreateAppointment.setOnClickListener {
            val intent = Intent(this, CreateAppoinmentActivity::class.java)
            startActivity(intent)
        }

        btnMyAppointment.setOnClickListener {
            val intent = Intent(this, AppoinmentsActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout(){
        val jwt = preferences["jwt", ""] // en caso de que no existiera llamamos a una cadena vacia no olvidar import get
        val call = apiService.postLogout("Bearer $jwt")
        call.enqueue(object: Callback<Void>{
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // mensaje que nos devuelve onfailure
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // no verificamos que el servidor nos devuelb=va una respuesta satisfactoria puede estar ofline por eso eliminamos el token del celular
                clearSessionPreferences()
                val intent = Intent(this@MenuActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun clearSessionPreferences() {
/*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", false) // le damos el dalor de false => cerrar sesion
        editor.apply() // para guardar cambios
*/
//        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["jwt"] = ""
    }
}
