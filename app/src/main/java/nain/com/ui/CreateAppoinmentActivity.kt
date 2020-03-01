package nain.com.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_appoinment.*
import kotlinx.android.synthetic.main.card_view_step_one.*
import kotlinx.android.synthetic.main.card_view_step_three.*
import kotlinx.android.synthetic.main.card_view_step_two.*
import nain.com.R
import nain.com.io.ApiService
import nain.com.model.Doctor
import nain.com.model.Specialty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CreateAppoinmentActivity : AppCompatActivity() {

    // cargamos de manera perezosa => by lazy
    val apiService: ApiService by lazy {
        ApiService.create()
    }

    private val selectedCalendar = Calendar.getInstance()
    private var selectedTimeRadioButton: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appoinment)

        btnNext.setOnClickListener {
            if(etDescription.text.toString().length < 3) {
                etDescription.error = getString(R.string.validate_appointment_description)
            } else {
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
        }

        btnNext2.setOnClickListener {
            when {
                etScheduledDate.text.toString().isEmpty() -> {
                    etScheduledDate.error = getString(R.string.validate_appointment_date)
                }
                selectedTimeRadioButton == null -> {
                    Snackbar.make(createAppointmentLinearLayout,
                        R.string.validate_appointment_time, Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    showAppointmentDataToConfirm()
                    cvStep2.visibility = View.GONE
                    cvStep3.visibility = View.VISIBLE
                }
            }
        }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "Cita registrada correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }

//        // Cargamos especialty
//        val specialtyOptions = arrayOf("Specialty A", "Specialty B", "Specialty C")
//        spinerSpecialties.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, specialtyOptions)

        // cargamos del api
        loadSpecialties()
        listenSpecialtyChanges()
//
//        val doctorOptions = arrayOf("Specialty A", "Specialty B", "Specialty C")
//        spinerDoctors.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, doctorOptions)
    }

    private fun loadSpecialties() {
        val call = apiService.getSpecialties()

        // cargamos de forma asyncrona => enquene
        call.enqueue(object: retrofit2.Callback<ArrayList<Specialty>> {

            // funcion que ejecuta cuando exite un error
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(this@CreateAppoinmentActivity, getString(R.string.error_loading_specialties), Toast.LENGTH_SHORT).show()
                finish()
            }

            // function que se llama cuando se ejecuto correctamente
            override fun onResponse(call: Call<ArrayList<Specialty>>, response: Response<ArrayList<Specialty>>) {
                if(response.isSuccessful) { // 200..300
                    val specialties = response.body()
//
//                    val specialtyOptions = ArrayList<String>()
//                    specialties?.forEach {
//                        specialtyOptions.add(it.name)
//                    }
                    // cuando estamos deltro de un object expresion this debe ser mas especifico
                    // antes definir el metodo string del specialties para que muestre solo el name
                    spinerSpecialties.adapter = specialties?.let {
                        ArrayAdapter<Specialty>(this@CreateAppoinmentActivity, android.R.layout.simple_list_item_1, it
                        )
                    }
                }
            }
        })
    }

    private fun listenSpecialtyChanges() {
     // verificamos que si o si se tenga una especialidad seleccionada
        spinerSpecialties.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            // si tenemos un item selecionado entra
            override fun onItemSelected( adapter: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                // obtenemos el id del item seleccionado => adapter te devulve la info
                val specialty = adapter?.getItemAtPosition(position) as Specialty
                // Toast.makeText(this@CreateAppoinmentActivity, specialty.id, Toast.LENGTH_SHORT).show()
                loadDoctors(specialty.id)
            }

        }
    }

    private fun loadDoctors(specialtyId: Int) {
        val call = apiService.getDoctor(specialtyId)
        call.enqueue(object: Callback<ArrayList<Doctor>> {
            override fun onFailure(call: Call<ArrayList<Doctor>>, t: Throwable) {
                Toast.makeText(this@CreateAppoinmentActivity, getString(R.string.error_loading_doctors), Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onResponse(call: Call<ArrayList<Doctor>>, response: Response<ArrayList<Doctor>>
            ) {
                if(response.isSuccessful) { // 200..300
                    val doctors = response.body()

                    // antes definir el metodo string del doctor para que muestre solo el name
                    spinerDoctors.adapter = doctors?.let {
                        ArrayAdapter<Doctor>(this@CreateAppoinmentActivity, android.R.layout.simple_list_item_1, it)
                    }
                }
            }

        })
    }

    private fun showAppointmentDataToConfirm(){
        tvConfirmDescription.text = etDescription.text.toString()
        tvConfirmSpecialty.text = spinerSpecialties.selectedItem.toString()

        val selectedRadioBtnId = radioGroupType.checkedRadioButtonId
        val selectedRadioType = radioGroupType.findViewById<RadioButton>(selectedRadioBtnId)

        tvConfirmType.text = selectedRadioType.text.toString()
        tvConfirmDate.text = etScheduledDate.text.toString()
        // selectedRadioButton?.text.toString() => lo controlamos a partior de la variable que creamos
        tvConfirmTime.text = selectedTimeRadioButton?.text.toString()
    }

    fun OnClickScheduledDate(v: View?){
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
            //Toast.makeText(this,"$y-$m-$d", Toast.LENGTH_SHORT).show()
            // etScheduledDate.setText("$y-$m-$d")
            selectedCalendar.set(y,m,d)
            etScheduledDate.setText(
                resources.getString(
                    R.string.date_format,
                      y,
//                    if(m <= 9) m.toString() else "0$m",
//                    if(d <= 9) d.toString() else "0$d"
                      m.twoDigits(),
                      d.twoDigits()
                )
            )
            // le damos null al error para que se quite => por el focusable no se puede mostrar el mensaje
            etScheduledDate.error = null
            displayRadioButtons()
        }

        // new dialog
        val datePickerDialog =  DatePickerDialog(this, listener, year, month, dayOfMonth)

        // set limits
        val datePicker = datePickerDialog.datePicker
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        datePicker.minDate = calendar.timeInMillis // +1
        calendar.add(Calendar.DAY_OF_MONTH, 29)
        datePicker.maxDate = calendar.timeInMillis // +29

        // show dialog
        datePickerDialog.show()
    }

    private fun displayRadioButtons() {
//        radioGroup.clearCheck()
//        radioGroup.removeAllViews()
        // Sin radioGroup mostrar en dos columnas

        selectedTimeRadioButton = null
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        val hours = arrayOf("3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM")
        var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            radioButton.setOnClickListener { view ->
                selectedTimeRadioButton?.isChecked = false

                selectedTimeRadioButton = view as RadioButton
                selectedTimeRadioButton?.isChecked = true
            }

            if(goToLeft)
                radioGroupLeft.addView(radioButton)
            else
                radioGroupRight.addView(radioButton)
            goToLeft = !goToLeft
        }
    }

    private fun Int.twoDigits() = if (this>=10) this.toString() else "0$this"

    override fun onBackPressed() {
        when {
            cvStep3.visibility == View.VISIBLE -> {
                cvStep3.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
            cvStep2.visibility == View.VISIBLE -> {
                cvStep2.visibility = View.GONE
                cvStep1.visibility = View.VISIBLE
            }
            cvStep1.visibility == View.VISIBLE -> {
                val buiilder = AlertDialog.Builder(this)
                buiilder.setTitle(getString(R.string.dialog_create_appointment_exit_title))
                buiilder.setMessage(getString(R.string.dialog_create_appointment_exit_message))
                buiilder.setPositiveButton(getString(R.string.positive_button)) { _, _ ->
                    finish()
                }
                buiilder.setNegativeButton(getString(R.string.negative_button)) { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog = buiilder.create()
                dialog.show()
            }
        }
    }
}
