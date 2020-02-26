package nain.com

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

// necesario para que funcione
import nain.com.PreferenceHelper.get
import nain.com.PreferenceHelper.set
// Fin

class MainActivity : AppCompatActivity() {

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
}
