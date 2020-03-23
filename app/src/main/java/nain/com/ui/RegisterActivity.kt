package nain.com.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*
import nain.com.R
import nain.com.io.ApiService
import nain.com.io.response.LoginResponse
import nain.com.util.PreferenceHelper
import nain.com.util.PreferenceHelper.set
import nain.com.util.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    val apiService by lazy {
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        textViewGoToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnConfirmRegister.setOnClickListener {
            performRegister()
        }
    }

    private fun performRegister() {
        val name = etRegisterName.text.toString().trim()
        val email = etRegisterEmail.text.toString().trim()
        val password = etRegisterPassword.text.toString()
        val passwordConfirmation = etRegisterPasswordConfirmation.text.toString()

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            toast(getString(R.string.error_register_empty_fields))
            return
        }

        if(password != passwordConfirmation) {
            toast(getString(R.string.error_register_passwords_do_not_match))
            return
        }

        val call = apiService.postRegister(name, email, password, passwordConfirmation)
        call.enqueue(object: Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if(loginResponse == null) {
                        toast(getString(R.string.error_register_validation))
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
                    toast(getString(R.string.error_register_validation))
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
}
