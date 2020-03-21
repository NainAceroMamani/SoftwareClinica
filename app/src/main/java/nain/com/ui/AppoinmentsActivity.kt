package nain.com.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_appoinments.*
import nain.com.R
import nain.com.io.ApiService
import nain.com.model.Appointment
import nain.com.util.PreferenceHelper
import nain.com.util.PreferenceHelper.get
import nain.com.util.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppoinmentsActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }

    private val appointmentAdapter = AppointmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appoinments)

        loadAppointments()

//        val appointments = ArrayList<Appointment>()
//        appointments.add(
//            Appointment(1,"Nain","12/02/2020","3:00 PM")
//        )

        rvAppointments.layoutManager = LinearLayoutManager(this)
//        rvAppointments.adapter = AppointmentAdapter(appointments)
//        rvAppointments.adapter = AppointmentAdapter()
        rvAppointments.adapter = appointmentAdapter

    }

    private fun loadAppointments() {
        val jwt = preferences["jwt", ""]
        val call = apiService.getAppointments("Bearer $jwt")
        call.enqueue(object: Callback<ArrayList<Appointment>> {
            override fun onFailure(call: Call<ArrayList<Appointment>>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(
                call: Call<ArrayList<Appointment>>,
                response: Response<ArrayList<Appointment>>
            ) {
                if(response.isSuccessful) {
                    // appointments es un Arraylist<>
//                    val appointments = response.body()
                    response.body()?.let {
                        appointmentAdapter.appointments = it
                        appointmentAdapter.notifyDataSetChanged() // notiificar a la interfaz de los cambios
                    }
                }
            }

        })
    }
}
