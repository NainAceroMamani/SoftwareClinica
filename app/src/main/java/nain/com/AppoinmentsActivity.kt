package nain.com

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_appoinments.*
import nain.com.model.Appointment

class AppoinmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appoinments)

        val appointments = ArrayList<Appointment>()
        appointments.add(Appointment(1,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(2,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(3,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(1,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(2,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(3,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(1,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(2,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(3,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(1,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(2,"Nain","12/02/2020","3:00 PM"))
        appointments.add(Appointment(3,"Nain","12/02/2020","3:00 PM"))

        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter = AppointmentAdapter(appointments)
    }
}
