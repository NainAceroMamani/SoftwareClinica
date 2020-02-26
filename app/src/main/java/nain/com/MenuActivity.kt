package nain.com

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_create_appoinment.*
import kotlinx.android.synthetic.main.activity_menu.*
import nain.com.PreferenceHelper.set

class MenuActivity : AppCompatActivity() {

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
            clearSessionPreferences()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun clearSessionPreferences() {
/*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", false) // le damos el dalor de false => cerrar sesion
        editor.apply() // para guardar cambios
*/
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["session"] = false
    }
}
