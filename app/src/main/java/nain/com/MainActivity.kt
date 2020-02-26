package nain.com

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

// necesario para que funcione
import nain.com.PreferenceHelper.get
import nain.com.PreferenceHelper.set
// Fin

class MainActivity : AppCompatActivity() {

    // snackbar => by lazy => sino nos sale error cuando entramos al app lo cargamos de forma peresosa porque aun no a cargado el mainLayout
    private val snackBar by lazy {
        Snackbar.make(mainLayout, R.string.press_back_again, Snackbar.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // shared preferences
//        val preferences = getSharedPreferences("geneal", Context.MODE_PRIVATE)
//        val session = preferences.getBoolean("session", false) // el false se pondra cuando no exista sesion en el storage

//        if (session) {
        val preferences = PreferenceHelper.defaultPrefs(this)
        if(preferences["session", false]){
            goToMenuActivity()
        }

        btnLogin.setOnClickListener {
            createSessionPreferences()
            goToMenuActivity()
        }

        textViewGoToRegister.setOnClickListener {
            Toast.makeText(this, getString(R.string.plis_fill_your_register), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToMenuActivity() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun createSessionPreferences() {
/*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", true) // le damos el dalor de true
        editor.apply() // para guardar cambios
        // para activar la sesion
*/
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["session"] = true
    }

    // crtl + o => function para darle doble click cu8ando se cierra el app
    override fun onBackPressed() {
        if (snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()
    }
}
