package nain.com

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_appointment.view.*
import nain.com.model.Appointment

class AppointmentAdapter(private val appointments: ArrayList<Appointment>) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        // en itemView llegan los id de todas las vista y modificamos su valor
        fun bind(appointment: Appointment) = with(itemView) {
            tvAppointmentsId.text = context.getString(R.string.item_appointment_id, appointment.id)
            tvDoctorName.text = appointment.doctorName
            tvScheduledDate.text = context.getString(R.string.item_appointment_date, appointment.scheduledDate)
            tvScheduledTime.text = context.getString(R.string.item_appointment_time, appointment.scheduledTime)
        }
    }

    // Inflates XML items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        )
    }

    // binds data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }

    // Number of element
    override fun getItemCount(): Int {
        return appointments.size
    }
}