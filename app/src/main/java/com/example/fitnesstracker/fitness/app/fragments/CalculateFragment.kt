//////package com.example.fitnesstracker.fitness.app.fragments
//////
//////import android.os.Bundle
//////import android.view.LayoutInflater
//////import android.view.View
//////import android.view.ViewGroup
//////import android.widget.*
//////import androidx.fragment.app.Fragment
//////import androidx.lifecycle.lifecycleScope
//////import com.example.fitnesstracker.R
//////import com.example.fitnesstracker.network.RetrofitClient
//////import com.example.fitnesstracker.network.SessionManager
//////import kotlinx.coroutines.launch
//////import kotlin.math.roundToInt
//////
//////class CalculateFragment : Fragment() {
//////    private val activities = listOf("Running", "Walking", "Cycling", "Weightlifting", "Swimming", "Yoga", "HIIT")
//////    private val mets = mapOf(
//////        "Running" to 9.8,
//////        "Walking" to 3.8,
//////        "Cycling" to 7.5,
//////        "Weightlifting" to 6.0,
//////        "Swimming" to 8.0,
//////        "Yoga" to 3.0,
//////        "HIIT" to 10.0
//////    )
//////
//////    private lateinit var sessionManager: SessionManager
//////
//////    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//////        return inflater.inflate(R.layout.fragment_calculate, container, false)
//////    }
//////
//////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//////        super.onViewCreated(view, savedInstanceState)
//////        sessionManager = SessionManager(requireContext())
//////
//////        val spinner = view.findViewById<Spinner>(R.id.spinnerActivity)
//////        val etTime = view.findViewById<EditText>(R.id.etTime)
//////        val etNotes = view.findViewById<EditText>(R.id.etNotes)
//////        val btnCalc = view.findViewById<Button>(R.id.btnCalculate)
//////        val btnSave = view.findViewById<Button>(R.id.btnSaveWorkout)
//////        val tvResult = view.findViewById<TextView>(R.id.tvResult)
//////
//////        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, activities)
//////
//////        var calculatedCals = 0
//////
//////        btnCalc.setOnClickListener {
//////            val time = etTime.text.toString().toIntOrNull()
//////            if (time == null) {
//////                Toast.makeText(requireContext(), "Enter workout duration", Toast.LENGTH_SHORT).show()
//////            } else {
//////                val activity = spinner.selectedItem.toString()
//////                val met = mets[activity] ?: 5.0
//////                val calories = (met * sessionManager.getWeight() * time / 60.0).roundToInt()
//////                calculatedCals = calories
//////                tvResult.text = "$activity for $time mins\nBurned: ${calories} kcal"
//////            }
//////        }
//////
//////        btnSave.setOnClickListener {
//////            val time = etTime.text.toString().toIntOrNull()
//////            val userId = sessionManager.getUserId()
//////            if (time == null || userId == null) {
//////                Toast.makeText(requireContext(), "Need duration and login", Toast.LENGTH_SHORT).show()
//////                return@setOnClickListener
//////            }
//////            if (calculatedCals == 0) {
//////                Toast.makeText(requireContext(), "Calculate calories first", Toast.LENGTH_SHORT).show()
//////                return@setOnClickListener
//////            }
//////            lifecycleScope.launch {
//////                try {
//////                    val response = RetrofitClient.instance.logWorkout(
//////                        userId = userId,
//////                        activity = spinner.selectedItem.toString(),
//////                        durationMinutes = time,
//////                        calories = calculatedCals,
//////                        notes = etNotes.text.toString().ifEmpty { null }
//////                    )
//////                    val body = response.body()
//////                    if (response.isSuccessful && body?.success == true) {
//////                        Toast.makeText(requireContext(), body.message, Toast.LENGTH_SHORT).show()
//////                    } else {
//////                        Toast.makeText(requireContext(), body?.message ?: "Unable to save", Toast.LENGTH_SHORT).show()
//////                    }
//////                } catch (e: Exception) {
//////                    Toast.makeText(requireContext(), e.localizedMessage ?: "Error", Toast.LENGTH_SHORT).show()
//////                }
//////            }
//////        }
//////    }
//////}
////
////package com.example.fitnesstracker.fitness.app
////
////import android.os.Bundle
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.Button
////import android.widget.TextView
////import android.widget.Toast
////import androidx.appcompat.app.AppCompatActivity
////import androidx.lifecycle.lifecycleScope
////import androidx.recyclerview.widget.LinearLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////import com.example.fitnesstracker.R
////import com.example.fitnesstracker.model.User
////import com.example.fitnesstracker.network.RetrofitClient
////import kotlinx.coroutines.launch
////
////class AdminActivity : AppCompatActivity() {
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_admin)
////        try { RetrofitClient.init(this) } catch (e: Exception) {}
////
////        val rv = findViewById<RecyclerView>(R.id.rvAdminUsers)
////        rv.layoutManager = LinearLayoutManager(this)
////        loadUsers(rv)
////    }
////
////    private fun loadUsers(rv: RecyclerView) {
////        lifecycleScope.launch {
////            try {
////                val res = RetrofitClient.instance.getAllUsers()
////                if (res.isSuccessful && res.body() != null) {
////                    rv.adapter = AdminAdapter(res.body()!!)
////                }
////            } catch (e: Exception) { Toast.makeText(this@AdminActivity, "Error", Toast.LENGTH_SHORT).show() }
////        }
////    }
////
////    inner class AdminAdapter(private val users: List<User>) : RecyclerView.Adapter<AdminAdapter.ViewHolder>() {
////        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
////            val tvName = v.findViewById<TextView>(R.id.tvAdminName)
////            val tvEmail = v.findViewById<TextView>(R.id.tvAdminEmail)
////            val btnDel = v.findViewById<Button>(R.id.btnAdminDelete)
////        }
////
////        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
////            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_admin_user, parent, false))
////        }
////        override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
////            val u = users[pos]
////            holder.tvName.text = "${u.name} (${u.role})"
////            holder.tvEmail.text = u.email
////            holder.btnDel.setOnClickListener { deleteUser(u.id) }
////        }
////        override fun getItemCount() = users.size
////    }
////
////    private fun deleteUser(id: Int) {
////        lifecycleScope.launch {
////            try {
////                // Fixed: ID to String
////                val res = RetrofitClient.instance.adminDeleteUser(mapOf("user_id" to id.toString()))
////                if (res.isSuccessful) {
////                    Toast.makeText(this@AdminActivity, "Deleted", Toast.LENGTH_SHORT).show()
////                    loadUsers(findViewById(R.id.rvAdminUsers))
////                }
////            } catch (e: Exception) {}
////        }
////    }
////}
//
//package com.example.fitnesstracker.fitness.app.fragments
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Spinner
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.network.RetrofitClient
//import kotlinx.coroutines.launch
//
//class CalculateFragment : Fragment() {
//    private val activities = listOf("Running", "Walking", "Cycling", "Weightlifting", "Swimming", "Yoga", "HIIT")
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_calculate, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val spinner = view.findViewById<Spinner>(R.id.spinnerActivity)
//        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, activities)
//
//        val btnCalc = view.findViewById<Button>(R.id.btnCalculate)
//        val btnSave = view.findViewById<Button>(R.id.btnSaveWorkout)
//        val etTime = view.findViewById<EditText>(R.id.etTime)
//        val tvResult = view.findViewById<TextView>(R.id.tvResult)
//
//        btnCalc.setOnClickListener {
//            val time = etTime.text.toString().toIntOrNull()
//            if (time != null) {
//                lifecycleScope.launch {
//                    try {
//                        val res = RetrofitClient.instance.calculateCalories(
//                            mapOf<String, String>(
//                                "activity" to spinner.selectedItem.toString(),
//                                "time_minutes" to time.toString()
//                            )
//                        )
//                        if (res.isSuccessful && res.body() != null) {
//                            val data = res.body()!!
//                            tvResult.text = "Burned: ${data.burned_calories} kcal\nMax HR: ${data.max_heart_rate}"
//                        }
//                    } catch (e: Exception) { Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show() }
//                }
//            }
//        }
//
//        btnSave.setOnClickListener {
//            val time = etTime.text.toString().toIntOrNull()
//            if (time != null) {
//                lifecycleScope.launch {
//                    try {
//                        val res = RetrofitClient.instance.createWorkout(
//                            mapOf<String, String>(
//                                "activity" to spinner.selectedItem.toString(),
//                                "time_minutes" to time.toString()
//                            )
//                        )
//                        if (res.isSuccessful && res.body() != null) {
//                            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
//                        }
//                    } catch (e: Exception) { Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show() }
//                }
//            }
//        }
//    }
//}


//package com.example.fitnesstracker.fitness.app.fragments
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.network.RetrofitClient
//import kotlinx.coroutines.launch
//import org.json.JSONObject
//
//class CalculateFragment : Fragment() {
//    private val activities = listOf("Running", "Walking", "Cycling", "Weightlifting", "Swimming", "Yoga", "HIIT")
//
//    // Common Views
//    private lateinit var etUserWeight: EditText
//    private lateinit var etDuration: EditText
//    private lateinit var tvResult: TextView // Matches XML ID
//    private lateinit var tvDetails: TextView
//    private lateinit var tvIntensitylabel: TextView
//
//    // Specific Inputs
//    private lateinit var etPace: EditText
//    private lateinit var etSpeed: EditText
//    private lateinit var etWalkSpeed: EditText
//    private lateinit var spinnerIntensity: Spinner
//    private lateinit var spinnerStroke: Spinner
//
//    // Cards
//    private lateinit var cardRunning: View
//    private lateinit var cardCycling: View
//    private lateinit var cardWalking: View
//    private lateinit var cardIntensity: View
//    private lateinit var cardSwimStroke: View
//
//    private var currentCalories = 0.0
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_calculate, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        try {
//            // 1. Bind Views
//            etUserWeight = view.findViewById(R.id.etUserWeight)
//            etDuration = view.findViewById(R.id.etDuration)
//            tvResult = view.findViewById(R.id.tvResult) // MATCHES XML
//            tvDetails = view.findViewById(R.id.tvDetails)
//
//            etPace = view.findViewById(R.id.etPace)
//            etSpeed = view.findViewById(R.id.etSpeed)
//            etWalkSpeed = view.findViewById(R.id.etWalkSpeed)
//
//            val spinner = view.findViewById<Spinner>(R.id.spinnerActivity)
//            spinnerIntensity = view.findViewById(R.id.spinnerIntensity)
//            spinnerStroke = view.findViewById(R.id.spinnerStroke)
//
//            cardRunning = view.findViewById(R.id.cardRunning)
//            cardCycling = view.findViewById(R.id.cardCycling)
//            cardWalking = view.findViewById(R.id.cardWalking)
//            cardIntensity = view.findViewById(R.id.cardIntensity)
//            cardSwimStroke = view.findViewById(R.id.cardSwimStroke)
//
//            // 2. Setup Adapters
//            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, activities)
//            spinner.adapter = adapter
//
//            val intensityLevels = listOf("Light", "Moderate", "Heavy", "Extreme")
//            val intensityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, intensityLevels)
//            spinnerIntensity.adapter = intensityAdapter
//
//            val strokes = listOf("Freestyle", "Breaststroke", "Butterfly", "Backstroke")
//            val strokeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, strokes)
//            spinnerStroke.adapter = strokeAdapter
//
//            // 3. Listeners
//            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                    showHideFields(activities[position])
//                }
//                override fun onNothingSelected(parent: AdapterView<*>?) {}
//            }
//
//            view.findViewById<Button>(R.id.btnCalculate).setOnClickListener { calculateCalories() }
//            view.findViewById<Button>(R.id.btnSaveWorkout).setOnClickListener { saveWorkout() }
//
//            // 4. Set Initial State
//            showHideFields(activities[0])
//
//        } catch (e: Exception) {
//            Log.e("CalculateFragment", "Setup Error", e)
//        }
//    }
//
//    private fun showHideFields(activity: String) {
//        cardRunning.visibility = View.GONE
//        cardCycling.visibility = View.GONE
//        cardWalking.visibility = View.GONE
//        cardIntensity.visibility = View.GONE
//        cardSwimStroke.visibility = View.GONE
//
//        when (activity) {
//            "Running" -> cardRunning.visibility = View.VISIBLE
//            "Cycling" -> cardCycling.visibility = View.VISIBLE
//            "Walking" -> cardWalking.visibility = View.VISIBLE
//            "Weightlifting", "Yoga", "HIIT" -> {
//                cardIntensity.visibility = View.VISIBLE
//                val label = when(activity) {
//                    "Weightlifting" -> "Load Intensity"
//                    "Yoga" -> "Pose Intensity"
//                    else -> "Intensity Level"
//                }
//                tvIntensitylabel.text = label
//            }
//            "Swimming" -> cardSwimStroke.visibility = View.VISIBLE
//        }
//    }
//
//    private fun calculateCalories() {
//        try {
//            val weight = etUserWeight.text.toString().toDoubleOrNull() ?: 0.0
//            val duration = etDuration.text.toString().toIntOrNull() ?: 0
//            val activity = view?.findViewById<Spinner>(R.id.spinnerActivity)?.selectedItem.toString()
//
//            if (weight <= 0 || duration <= 0) {
//                Toast.makeText(requireContext(), "Please enter weight and duration", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            val met = getMETValue(activity)
//            val durationHours = duration / 60.0
//            val cals = met * weight * durationHours
//
//            currentCalories = cals
//            tvResult.text = "Estimated: ${cals.toInt()} kcal"
//            tvDetails.text = "Based on MET: $met"
//
//        } catch (e: Exception) {
//            Log.e("CalculateFragment", "Calc Error", e)
//        }
//    }
//
//    private fun getMETValue(activity: String): Double {
//        val intensity = spinnerIntensity.selectedItem.toString()
//        val stroke = spinnerStroke.selectedItem.toString()
//
//        return when (activity) {
//            "Running" -> {
//                val paceMin = etPace.text.toString().toDoubleOrNull()
//                if (paceMin != null && paceMin > 0) {
//                    val speed = 60 / paceMin
//                    when {
//                        speed <= 8 -> 8.3
//                        speed <= 10 -> 10.0
//                        speed <= 12 -> 11.5
//                        else -> 13.0
//                    }
//                } else 10.0
//            }
//            "Cycling" -> {
//                val speed = etSpeed.text.toString().toDoubleOrNull() ?: 20.0
//                when {
//                    speed <= 16 -> 6.8
//                    speed <= 20 -> 8.0
//                    else -> 11.0
//                }
//            }
//            "Walking" -> {
//                val speed = etWalkSpeed.text.toString().toDoubleOrNull() ?: 5.0
//                when {
//                    speed <= 3 -> 2.8
//                    speed <= 5 -> 3.8
//                    else -> 5.0
//                }
//            }
//            "Swimming" -> {
//                when (stroke) {
//                    "Freestyle" -> 8.0
//                    "Breaststroke" -> 10.0
//                    "Butterfly" -> 13.8
//                    "Backstroke" -> 11.0
//                    else -> 9.5
//                }
//            }
//            else -> { // Weights, Yoga, HIIT
//                when (intensity) {
//                    "Light" -> if (activity == "Yoga") 2.5 else if (activity == "HIIT") 8.0 else 3.0
//                    "Moderate" -> if (activity == "Yoga") 4.0 else if (activity == "HIIT") 11.0 else 6.0
//                    "Heavy", "Vigorous", "Intense" -> if (activity == "Yoga") 4.0 else if (activity == "HIIT") 14.0 else 8.0
//                    "Extreme" -> 14.0
//                    else -> 6.0
//                }
//            }
//        }
//    }
//
//    private fun saveWorkout() {
//        val activity = view?.findViewById<Spinner>(R.id.spinnerActivity)?.selectedItem.toString()
//        val duration = etDuration.text.toString().toIntOrNull() ?: 0
//
//        if (duration <= 0) {
//            Toast.makeText(requireContext(), "Duration required", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val notesObj = JSONObject()
//        notesObj.put("estimated_cals", currentCalories)
//
//        if (activity == "Running" && etPace.text.isNotEmpty()) {
//            notesObj.put("pace", etPace.text.toString())
//        }
//        if ((activity == "Cycling" || activity == "Walking") && etSpeed.text.isNotEmpty()) {
//            notesObj.put("speed", etSpeed.text.toString())
//        }
//        if (activity in listOf("Weightlifting", "Yoga", "HIIT")) {
//            notesObj.put("intensity", spinnerIntensity.selectedItem.toString())
//        }
//        if (activity == "Swimming") {
//            notesObj.put("stroke", spinnerStroke.selectedItem.toString())
//        }
//
//        lifecycleScope.launch {
//            try {
//                val res = RetrofitClient.instance.createWorkout(
//                    mapOf(
//                        "activity" to activity,
//                        "time_minutes" to duration.toString(),
//                        "notes" to notesObj.toString()
//                    )
//                )
//                if (res.isSuccessful && res.body() != null) {
//                    Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(requireContext(), "Failed to Save", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}

//
//package com.example.fitnesstracker.fitness.app.fragments
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import com.example.fitnesstracker.R
//import com.example.fitnesstracker.model.Workout
//import com.example.fitnesstracker.network.RetrofitClient
//import kotlinx.coroutines.launch
//import org.json.JSONObject
//import java.util.*
//
//class CalculateFragment : Fragment() {
//
//    // Activities
//    private val activities = listOf("Running", "Walking", "Cycling", "Weightlifting", "Swimming", "Yoga", "HIIT")
//
//    // Common Views
//    private lateinit var etUserWeight: EditText
//    private lateinit var etDuration: EditText
//    private lateinit var spinnerActivity: Spinner
//    private lateinit var tvCalories: TextView
//    private lateinit var tvDetails: TextView
//
//    // Specific Inputs
//    private lateinit var etPace: EditText          // Running
//    private lateinit var etSpeed: EditText          // Cycling
//    private lateinit var etWalkSpeed: EditText     // Walking
//    private lateinit var spinnerIntensity: Spinner // Weights, Yoga, HIIT
//    private lateinit var spinnerStroke: Spinner     // Swimming
//
//    // Cards
//    private lateinit var cardRunning: View
//    private lateinit var cardCycling: View
//    private lateinit var cardWalking: View
//    private lateinit var cardIntensity: View
//    private lateinit var cardSwimStroke: View
//
//    // State
//    private var currentCalories = 0.0
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_calculate, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        try {
//            // 1. Bind Common Views
//            etUserWeight = view.findViewById(R.id.etUserWeight)
//            etDuration = view.findViewById(R.id.etDuration)
//            tvCalories = view.findViewById(R.id.tvCalories)
//            tvDetails = view.findViewById(R.id.tvDetails)
//            spinnerActivity = view.findViewById(R.id.spinnerActivity)
//
//            // 2. Bind Specific Views
//            cardRunning = view.findViewById(R.id.cardRunning)
//            cardCycling = view.findViewById(R.id.cardCycling)
//            cardWalking = view.findViewById(R.id.cardWalking)
//            cardIntensity = view.findViewById(R.id.cardIntensity)
//            cardSwimStroke = view.findViewById(R.id.cardSwimStroke)
//
//            etPace = view.findViewById(R.id.etPace)
//            etSpeed = view.findViewById(R.id.etSpeed)
//            etWalkSpeed = view.findViewById(R.id.etWalkSpeed)
//            spinnerIntensity = view.findViewById(R.id.spinnerIntensity)
//            spinnerStroke = view.findViewById(R.id.spinnerStroke)
//
//            // 3. Setup Spinner
//            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, activities)
//            spinnerActivity.adapter = adapter
//
//            setupDropdowns()
//
//            // 4. Set Listeners
//            view.findViewById<Button>(R.id.btnCalculate).setOnClickListener { calculateCalories() }
//            view.findViewById<Button>(R.id.btnSaveWorkout).setOnClickListener { saveWorkout() }
//
//            // 5. Set Initial State
//            showHideFields(activities[0])
//
//        } catch (e: Exception) {
//            Log.e("CalcFrag", "Setup Error", e)
//        }
//    }
//
//    private fun setupDropdowns() {
//        // Intensity: Light, Moderate, Heavy, Extreme
//        val intensityLevels = listOf("Light", "Moderate", "Heavy", "Extreme")
//        val intensityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, intensityLevels)
//        spinnerIntensity.adapter = intensityAdapter
//
//        // Strokes: Freestyle, Breaststroke, Butterfly, Backstroke
//        val strokes = listOf("Freestyle", "Breaststroke", "Butterfly", "Backstroke")
//        val strokeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, strokes)
//        spinnerStroke.adapter = strokeAdapter
//    }
//
//    private fun showHideFields(activity: String) {
//        // Hide all specific cards
//        cardRunning.visibility = View.GONE
//        cardCycling.visibility = View.GONE
//        cardWalking.visibility = View.GONE
//        cardIntensity.visibility = View.GONE
//        cardSwimStroke.visibility = View.GONE
//
//        // Show relevant card
//        when (activity) {
//            "Running" -> cardRunning.visibility = View.VISIBLE
//            "Cycling" -> cardCycling.visibility = View.VISIBLE
//            "Walking" -> cardWalking.visibility = View.VISIBLE
//            "Weightlifting", "Yoga", "HIIT" -> {
//                cardIntensity.visibility = View.VISIBLE
//                val label = when(activity) {
//                    "Weightlifting" -> "Load Intensity"
//                    "Yoga" -> "Pose Intensity"
//                    else -> "Intensity Level"
//                }
//                view?.findViewById<TextView>(R.id.tvIntensityLabel)?.text = label
//            }
//            "Swimming" -> cardSwimStroke.visibility = View.VISIBLE
//        }
//    }
//
//    private fun calculateCalories() {
//        try {
//            val weight = etUserWeight.text.toString().toDoubleOrNull() ?: 0.0
//            val duration = etDuration.text.toString().toIntOrNull() ?: 0
//            val activity = spinnerActivity.selectedItem.toString()
//
//            if (weight <= 0 || duration <= 0) {
//                Toast.makeText(requireContext(), "Please enter weight and duration", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            // 1. Determine MET based on Activity & Inputs
//            val met = getMETValue(activity)
//
//            // 2. Calculate Calories: MET * Weight * Hours
//            val durationHours = duration / 60.0
//            val cals = met * weight * durationHours
//            currentCalories = cals
//
//            // 3. Update UI
//            tvCalories.text = "Estimated: ${cals.toInt()} kcal"
//            tvDetails.text = "Based on MET: $met"
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "Calculation Error", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun getMETValue(activity: String): Double {
//        // 1. Get Intensity Inputs
//        val intensityText = if (::spinnerIntensity.isInitialized) spinnerIntensity.selectedItem.toString() else "Moderate"
//        val strokeText = if (::spinnerStroke.isInitialized) spinnerStroke.selectedItem.toString() else "Freestyle"
//
//        // 2. Get Numeric Inputs
//        val paceMin = if (::etPace.isInitialized) etPace.text.toString().toDoubleOrNull() else 0.0
//        val speedKmh = if (::etSpeed.isInitialized) etSpeed.text.toString().toDoubleOrNull() else 0.0
//        val walkSpeed = if (::etWalkSpeed.isInitialized) etWalkSpeed.text.toString().toDoubleOrNull() else 0.0
//
//        if (walkSpeed != null) {
//            return when (activity) {
//                "Running" -> {
//                    // Input is Pace (min/km), Logic needs Speed (km/h).
//                    // Speed = 60 / Pace. If Pace is 0 or empty, assume 10km/h
//                    val runningSpeed = paceMin?.let { if (it > 0) 60 / paceMin else 10.0 }
//
//                    when {
//                        runningSpeed!! <= 8 -> 8.3
//                        runningSpeed <= 10 -> 10.0
//                        runningSpeed <= 12 -> 11.5
//                        else -> 13.0
//                    }
//                }
//                "Cycling" -> {
//                    when (intensityText) {
//                        "Light" -> 6.8
//                        "Moderate" -> 8.0
//                        "Vigorous" -> 11.0
//                        else -> 8.0
//                    }
//                }
//                "Walking" -> {
//                    when {
//                        walkSpeed <= 3 -> 2.8
//                        walkSpeed <= 5 -> 3.8
//                        else -> 5.0
//                    }
//                }
//                "Swimming" -> {
//                    when (strokeText) {
//                        "Freestyle" -> 8.0
//                        "Breaststroke" -> 10.0
//                        "Butterfly" -> 13.8
//                        "Backstroke" -> 11.0
//                        else -> 9.5
//                    }
//                }
//                else -> { // Weightlifting, Yoga, HIIT
//                    when (intensityText) {
//                        "Light" -> if (activity == "HIIT") 8.0 else if (activity == "Yoga") 2.5 else 3.0
//                        "Moderate" -> if (activity == "HIIT") 11.0 else if (activity == "Yoga") 4.0 else 6.0
//                        "Heavy", "Vigorous", "Intense" -> if (activity == "HIIT") 14.0 else if (activity == "Yoga") 4.0 else 8.0
//                        "Extreme" -> 14.0
//                        else -> 6.0
//                    }
//                }
//            }
//        }
//        return TODO("Provide the return value")
//    }
//
//    private fun saveWorkout() {
//        val activity = spinnerActivity.selectedItem.toString()
//        val duration = etDuration.text.toString().toIntOrNull() ?: 0
//        val weight = etUserWeight.text.toString().toDoubleOrNull() ?: 0.0
//
//        if (duration <= 0) {
//            Toast.makeText(requireContext(), "Duration required", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Build JSON Notes for Specific Data
//        val notesObj = JSONObject()
//        notesObj.put("estimated_cals", currentCalories) // Save calculated value for reference
//
//        // Handle Specific Inputs
//        if (activity == "Running") {
//            val pace = etPace.text.toString()
//            if (pace.isNotEmpty()) notesObj.put("pace_min_km", pace)
//        }
//        if (activity == "Cycling") {
//            val speed = etSpeed.text.toString()
//            if (speed.isNotEmpty()) notesObj.put("speed_kmh", speed)
//        }
//        if (activity == "Walking") {
//            val speed = etWalkSpeed.text.toString()
//            if (speed.isNotEmpty()) notesObj.put("walk_speed_kmh", speed)
//        }
//        if (activity in listOf("Weightlifting", "Yoga", "HIIT")) {
//            notesObj.put("intensity", spinnerIntensity.selectedItem.toString())
//        }
//        if (activity == "Swimming") {
//            notesObj.put("stroke_type", spinnerStroke.selectedItem.toString())
//        }
//
//        lifecycleScope.launch {
//            try {
//                // Send calculated value or raw inputs. Backend handles MET logic.
//                // We send 'estimated_cals' just for display, or let backend calc.
//                // But backend expects the specific fields (pace, speed) to exist in JSON to perform MET calc.
//
//                val res = RetrofitClient.instance.createWorkout(
//                    mapOf<String, String>(
//                        "activity" to activity,
//                        "time_minutes" to duration.toString(),
//                        "notes" to notesObj.toString()
//                    )
//                )
//                if (res.isSuccessful && res.body() != null) {
//                    Toast.makeText(requireContext(), "Workout Saved!", Toast.LENGTH_SHORT).show()
//                    res.body()!!.challenge?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
//
//                    // Clear inputs
//                    etDuration.text?.clear()
//                } else {
//                    Toast.makeText(requireContext(), "Save Failed", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}

package com.example.fitnesstracker.fitness.app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.model.Workout
import com.example.fitnesstracker.network.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class CalculateFragment : Fragment() {

    private val activities = listOf("Running", "Walking", "Cycling", "Weightlifting", "Swimming", "Yoga", "HIIT")

    // Common Views
    private lateinit var etUserWeight: EditText
    private lateinit var etDuration: EditText
    private lateinit var spinnerActivity: Spinner
    private lateinit var tvCalories: TextView
    private lateinit var tvDetails: TextView

    // Specific Inputs
    private lateinit var etPace: EditText          // Running
    private lateinit var etSpeed: EditText          // Cycling
    private lateinit var etWalkSpeed: EditText     // Walking

    // Weightlifting Inputs
    private lateinit var etSets: EditText
    private lateinit var etReps: EditText
    private lateinit var etLiftingWeight: EditText

    private lateinit var spinnerIntensity: Spinner // Weights, Yoga, HIIT
    private lateinit var spinnerStroke: Spinner     // Swimming

    // Cards
    private lateinit var cardRunning: View
    private lateinit var cardCycling: View
    private lateinit var cardWalking: View
    private lateinit var cardWeights: View
    private lateinit var cardIntensity: View
    private lateinit var cardSwimStroke: View

    private var currentCalories = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calculate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            // 1. Bind Common Views
            etUserWeight = view.findViewById(R.id.etUserWeight)
            etDuration = view.findViewById(R.id.etDuration)
            tvCalories = view.findViewById(R.id.tvCalories)
            tvDetails = view.findViewById(R.id.tvDetails)
            spinnerActivity = view.findViewById(R.id.spinnerActivity)

            // 2. Bind Specific Views
            cardRunning = view.findViewById(R.id.cardRunning)
            cardCycling = view.findViewById(R.id.cardCycling)
            cardWalking = view.findViewById(R.id.cardWalking)
            cardWeights = view.findViewById(R.id.cardWeights)
            cardIntensity = view.findViewById(R.id.cardIntensity)
            cardSwimStroke = view.findViewById(R.id.cardSwimStroke)

            etPace = view.findViewById(R.id.etPace)
            etSpeed = view.findViewById(R.id.etSpeed)
            etWalkSpeed = view.findViewById(R.id.etWalkSpeed)

            etSets = view.findViewById(R.id.etSets)
            etReps = view.findViewById(R.id.etReps)
            etLiftingWeight = view.findViewById(R.id.etLiftingWeight)

            spinnerIntensity = view.findViewById(R.id.spinnerIntensity)
            spinnerStroke = view.findViewById(R.id.spinnerStroke)

            // 3. Setup Spinner
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, activities)
            spinnerActivity.adapter = adapter

            // Set Listener to change UI
            spinnerActivity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    showHideFields(activities[position])
                }
            }

            setupDropdowns()

            // 4. Set Listeners
            view.findViewById<Button>(R.id.btnCalculate).setOnClickListener { calculateCalories() }
            view.findViewById<Button>(R.id.btnSaveWorkout).setOnClickListener { saveWorkout() }

            // 5. Set Initial State
            showHideFields(activities[0])

        } catch (e: Exception) {
            Log.e("CalcFrag", "Setup Error", e)
        }
    }

    private fun setupDropdowns() {
        val intensityLevels = listOf("Light", "Moderate", "Heavy")
        val intensityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, intensityLevels)
        spinnerIntensity.adapter = intensityAdapter

        val strokes = listOf("Freestyle", "Breaststroke", "Butterfly", "Backstroke")
        val strokeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, strokes)
        spinnerStroke.adapter = strokeAdapter
    }

    private fun showHideFields(activity: String) {
        // Hide all specific cards first
        cardRunning.visibility = View.GONE
        cardCycling.visibility = View.GONE
        cardWalking.visibility = View.GONE
        cardWeights.visibility = View.GONE
        cardIntensity.visibility = View.GONE
        cardSwimStroke.visibility = View.GONE

        // Show relevant card
        when (activity) {
            "Running" -> cardRunning.visibility = View.VISIBLE
            "Cycling" -> cardCycling.visibility = View.VISIBLE
            "Walking" -> cardWalking.visibility = View.VISIBLE
            "Weightlifting" -> {
                cardWeights.visibility = View.VISIBLE
                cardIntensity.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.tvIntensityLabel)?.text = "Intensity"
            }
            "Yoga", "HIIT" -> {
                cardIntensity.visibility = View.VISIBLE
                val label = if (activity == "Yoga") "Pose Intensity" else "Intensity Level"
                view?.findViewById<TextView>(R.id.tvIntensityLabel)?.text = label
            }
            "Swimming" -> cardSwimStroke.visibility = View.VISIBLE
        }
    }

    private fun calculateCalories() {
        try {
            val weight = etUserWeight.text.toString().toDoubleOrNull() ?: 0.0
            val duration = etDuration.text.toString().toIntOrNull() ?: 0
            val activity = spinnerActivity.selectedItem.toString()

            if (weight <= 0 || duration <= 0) {
                Toast.makeText(requireContext(), "Please enter weight and duration", Toast.LENGTH_SHORT).show()
                return
            }

            val met = getMETValue(activity)
            val durationHours = duration / 60.0
            val cals = met * weight * durationHours
            currentCalories = cals

            tvCalories.text = "Estimated: ${cals.toInt()} kcal"
            tvDetails.text = "Based on MET: $met"
        } catch (e: Exception) {
            Log.e("CalcFrag", "Calculation error", e)
            Toast.makeText(requireContext(), "Calculation Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMETValue(activity: String): Double {
        val intensityText = if (::spinnerIntensity.isInitialized) spinnerIntensity.selectedItem.toString() else "Moderate"
        val strokeText = if (::spinnerStroke.isInitialized) spinnerStroke.selectedItem.toString() else "Freestyle"

        // Input defaults
        val paceMin = if (::etPace.isInitialized) etPace.text.toString().toDoubleOrNull() else null
        val speedKmh = if (::etSpeed.isInitialized) etSpeed.text.toString().toDoubleOrNull() else null
        val walkSpeed = if (::etWalkSpeed.isInitialized) etWalkSpeed.text.toString().toDoubleOrNull() else null

        return when (activity) {
            "Running" -> {
                // Pace min/km -> Speed km/h = 60 / pace
                val runningSpeed = if (paceMin != null && paceMin > 0) 60.0 / paceMin else 9.7 // Default ~9.7km/h

                when {
                    runningSpeed < 8.0 -> 6.0       // Slow jog
                    runningSpeed < 10.0 -> 8.3     // Jogging
                    runningSpeed < 12.0 -> 9.8     // Running
                    else -> 11.8                   // Fast running
                }
            }
            "Cycling" -> {
                val speed = speedKmh ?: 20.0
                when {
                    speed < 16.0 -> 4.0   // Leisure
                    speed < 20.0 -> 8.0   // Moderate
                    speed < 25.0 -> 10.0  // Vigorous
                    else -> 12.0          // Racing
                }
            }
            "Walking" -> {
                val speed = walkSpeed ?: 5.0
                when {
                    speed < 4.0 -> 3.0    // Slow
                    speed < 5.5 -> 4.0    // Brisk
                    else -> 5.0           // Very Brisk
                }
            }
            "Swimming" -> {
                when (strokeText) {
                    "Freestyle" -> 8.0
                    "Breaststroke" -> 10.0
                    "Butterfly" -> 13.0
                    "Backstroke" -> 7.0
                    else -> 8.0
                }
            }
            "Weightlifting" -> {
                when (intensityText) {
                    "Light" -> 3.0
                    "Moderate" -> 5.0
                    "Heavy" -> 6.0
                    else -> 5.0
                }
            }
            "Yoga" -> {
                when (intensityText) {
                    "Light" -> 2.5
                    "Moderate" -> 4.0
                    else -> 3.0
                }
            }
            "HIIT" -> {
                when (intensityText) {
                    "Light" -> 8.0
                    "Moderate" -> 11.0
                    "Heavy" -> 14.0
                    else -> 11.0
                }
            }
            else -> 4.0
        }
    }

    private fun saveWorkout() {
        val activity = spinnerActivity.selectedItem.toString()
        val duration = etDuration.text.toString().toIntOrNull() ?: 0
        val weight = etUserWeight.text.toString().toDoubleOrNull() ?: 0.0

        if (duration <= 0) {
            Toast.makeText(requireContext(), "Duration required", Toast.LENGTH_SHORT).show()
            return
        }

        // Build JSON Notes for Specific Data
        val notesObj = JSONObject()

        // We include the calculated calories here in notes, and also send it to backend
        // so the backend stores the correct value.
        notesObj.put("estimated_cals", currentCalories)

        if (activity == "Running") {
            etPace.text.toString().takeIf { it.isNotEmpty() }?.let { notesObj.put("pace_min_km", it) }
        }
        if (activity == "Cycling") {
            etSpeed.text.toString().takeIf { it.isNotEmpty() }?.let { notesObj.put("speed_kmh", it) }
        }
        if (activity == "Walking") {
            etWalkSpeed.text.toString().takeIf { it.isNotEmpty() }?.let { notesObj.put("walk_speed_kmh", it) }
        }
        if (activity == "Weightlifting") {
            notesObj.put("sets", etSets.text.toString())
            notesObj.put("reps", etReps.text.toString())
            notesObj.put("weight_kg", etLiftingWeight.text.toString())
            notesObj.put("intensity", spinnerIntensity.selectedItem.toString())
        }
        if (activity in listOf("Yoga", "HIIT")) {
            notesObj.put("intensity", spinnerIntensity.selectedItem.toString())
        }
        if (activity == "Swimming") {
            notesObj.put("stroke_type", spinnerStroke.selectedItem.toString())
        }

        lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.createWorkout(
                    mapOf<String, String>(
                        "activity" to activity,
                        "time_minutes" to duration.toString(),
                        // We send the calculated calories directly to be saved by PHP
                        "burned_calories" to currentCalories.toString(),
                        "notes" to notesObj.toString()
                    )
                )
                if (res.isSuccessful && res.body() != null) {
                    Toast.makeText(requireContext(), "Workout Saved!", Toast.LENGTH_SHORT).show()
                    res.body()!!.challenge?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
                    etDuration.text?.clear()
                } else {
                    Toast.makeText(requireContext(), "Save Failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}