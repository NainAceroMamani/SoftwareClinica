package nain.com.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_menu.*
import nain.com.util.PreferenceHelper
import nain.com.util.PreferenceHelper.set
import nain.com.util.PreferenceHelper.get
import nain.com.R
import nain.com.io.ApiService
import nain.com.model.User
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

    private val authHeader by lazy {
        val jwt = preferences["jwt", ""]
        "Bearer $jwt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val storeToken = intent.getBooleanExtra("store_token", false)
        if(storeToken)
            storageToken()

        setOnClickListeners()
    }

    private fun setOnClickListeners() {

        btnCreateAppointment.setOnClickListener {
            val intent = Intent(this, CreateAppoinmentActivity::class.java)
            startActivity(intent)
        }

        btnMyAppointment.setOnClickListener {
            createAppointment(it) // it ase referencias  a esta vista
        }

        btnLogout.setOnClickListener {
            performLogout()
        }

        btnProfile.setOnClickListener {
            editPrfile()
        }
    }

    private fun createAppointment(view: View) {
        val call = apiService.getUser(authHeader)
        call.enqueue(object: Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful) {
                    val user = response.body();
                    val phoneLength = user?.phone?.length ?: 0 // si es null le damos cero
                    if( phoneLength >= 6 ) {
                        val intent = Intent(this@MenuActivity, AppoinmentsActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Snackbar.make(view, R.string.you_need_a_phone, Snackbar.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun editPrfile(){
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun storageToken(){
//        val jwt = preferences["jwt", ""]
//        val authHeader= "Bearer $jwt"

        //sericios de firebase message = >cod de fireabse deviceToken
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val deviceToken = instanceIdResult.token

            val call = apiService.postToken(authHeader, deviceToken)
            call.enqueue(object: Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    toast(t.localizedMessage)
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful) {
                        Log.d(TAG, "Token registrado correctamente")
                    } else {
                        Log.d(TAG, "Hubo un problema al registro del Token")
                    }
                }

            })
        }
    }

    private fun performLogout(){
        val call = apiService.postLogout(authHeader)
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

    // esto se segera por el problema del const alt + enter = Move
    companion object {
        // constante para debugear de service message de fireabse
        private const val TAG = "MenuActivity"
    }
}
