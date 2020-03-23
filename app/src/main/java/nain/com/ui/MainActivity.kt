package nain.com.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import nain.com.util.PreferenceHelper

// necesario para que funcione
import nain.com.util.PreferenceHelper.get
import nain.com.util.PreferenceHelper.set
import nain.com.R
import nain.com.io.ApiService
import nain.com.io.response.LoginResponse
import nain.com.util.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Fin

class MainActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        // iniciamos el api servuice de forma perezosa
        ApiService.create()
    }

    // snackbar => by lazy => sino nos sale error cuando entramos al app lo cargamos de forma peresosa porque aun no a cargado el mainLayout
    private val snackBar by lazy {
        Snackbar.make(mainLayout, R.string.press_back_again, Snackbar.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //sericios de firebase message
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) {instanceIdResult ->
            val deviceToken = instanceIdResult.token
            Log.d("FCMService", deviceToken)
        }
        // shared preferences
//        val preferences = getSharedPreferences("geneal", Context.MODE_PRIVATE)
//        val session = preferences.getBoolean("session", false) // el false se pondra cuando no exista sesion en el storage
//        if (session) {
        val preferences = PreferenceHelper.defaultPrefs(this)
//        if(preferences["success", false]){
        if(preferences["jwt", ""].contains(".")){
            // se el jwt almacenado contiene un punto entonces es un token valido
            goToMenuActivity()
        }

        btnLogin.setOnClickListener {
            performLogin()
//            createSessionPreferences()
//            goToMenuActivity()
        }

        textViewGoToRegister.setOnClickListener {
            Toast.makeText(this, getString(R.string.plis_fill_your_register), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if(email.trim().isEmpty() || password.trim().isEmpty()) {
            toast(getString(R.string.error_empty_credentials))
            return
        }

        val call = apiService.postLogin(email, password)
        call.enqueue(object: Callback<LoginResponse> {
            // en call solo le ponemos defrente la clase ya que no retorna un array solo un objeto
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // llamamos extension function
                // t es lo que retrofic nos envia por defecto sus errores
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful) {
                    val loginResponse = response.body()
                    // llamamos extension function
                    if(loginResponse === null) {
                        // si no se pudo mapear
                        toast(getString(R.string.error_login_response))
                        return
                    }
                    if(loginResponse.success) {
                        // si nos devuelve true el servidor
                        createSessionPreferences(loginResponse.jwt)
                        // con la informacion del usuario almacenarla o imprimirla
                        toast(getString(R.string.welcome_name, loginResponse.user.name))
                        goToMenuActivity()
                    }else {
                        toast(getString(R.string.error_invalid_credentials))
                    }
                }else {
                    // si no se pudo mapear
                    toast(getString(R.string.error_login_response))
                }
            }

        })
    }

    private fun goToMenuActivity() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

//    private fun createSessionPreferences() {
    private fun createSessionPreferences(jwt: String) {
    /*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", true) // le damos el dalor de true
        editor.apply() // para guardar cambios
        // para activar la sesion
*/
    // aqui tambien podemos almacenar la informacion del usuario como el email o el nombre o lo que nos devuelve el servidor
        val preferences = PreferenceHelper.defaultPrefs(this)
//        preferences["session"] = true
        preferences["jwt"] = jwt
    }

    // crtl + o => function para darle doble click cu8ando se cierra el app
    override fun onBackPressed() {
        if (snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()
    }
}
