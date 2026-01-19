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
import com.example.fitnesstracker.network.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject


class CalculateFragment : Fragment() {

    private val activities = listOf("Running", "Walking", "Cycling", "Weightlifting", "Swimming", "Yoga", "HIIT")

    // Common Views
    private lateinit var etUserWeight: EditText
    private lateinit var etDuration: EditText
    private lateinit var spinnerActivity: Spinner
    private lateinit var tvCalories: TextView
    private lateinit var tvDetails: TextView

    // Activity-specific Inputs
    private lateinit var etPace: EditText
    private lateinit var etSpeed: EditText
    private lateinit var etWalkSpeed: EditText

    private lateinit var etSets: EditText
    private lateinit var etReps: EditText
    private lateinit var etLiftingWeight: EditText

    private lateinit var spinnerIntensity: Spinner
    private lateinit var spinnerStroke: Spinner

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
            etUserWeight = view.findViewById(R.id.etUserWeight)
            etDuration = view.findViewById(R.id.etDuration)
            spinnerActivity = view.findViewById(R.id.spinnerActivity)
            tvCalories = view.findViewById(R.id.tvCalories)
            tvDetails = view.findViewById(R.id.tvDetails)

            etPace = view.findViewById(R.id.etPace)
            etSpeed = view.findViewById(R.id.etSpeed)
            etWalkSpeed = view.findViewById(R.id.etWalkSpeed)

            etSets = view.findViewById(R.id.etSets)
            etReps = view.findViewById(R.id.etReps)
            etLiftingWeight = view.findViewById(R.id.etLiftingWeight)

            spinnerIntensity = view.findViewById(R.id.spinnerIntensity)
            spinnerStroke = view.findViewById(R.id.spinnerStroke)

            cardRunning = view.findViewById(R.id.cardRunning)
            cardCycling = view.findViewById(R.id.cardCycling)
            cardWalking = view.findViewById(R.id.cardWalking)
            cardWeights = view.findViewById(R.id.cardWeights)
            cardIntensity = view.findViewById(R.id.cardIntensity)
            cardSwimStroke = view.findViewById(R.id.cardSwimStroke)

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, activities)
            spinnerActivity.adapter = adapter
            spinnerActivity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    showHideFields(activities[position])
                }
            }

            setupDropdowns()

            view.findViewById<Button>(R.id.btnCalculate).setOnClickListener { calculateCalories() }
            view.findViewById<Button>(R.id.btnSaveWorkout).setOnClickListener { saveWorkout() }

            showHideFields(activities[0])
        } catch (e: Exception) {
            Log.e("CalcFrag", "Setup Error", e)
            Toast.makeText(requireContext(), "Error loading screen", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupDropdowns() {
        val intensityLevels = listOf("Light", "Moderate", "Heavy")
        spinnerIntensity.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, intensityLevels)

        val strokes = listOf("Freestyle", "Breaststroke", "Butterfly", "Backstroke")
        spinnerStroke.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, strokes)
    }

    private fun showHideFields(activity: String) {
        cardRunning.visibility = View.GONE
        cardCycling.visibility = View.GONE
        cardWalking.visibility = View.GONE
        cardWeights.visibility = View.GONE
        cardIntensity.visibility = View.GONE
        cardSwimStroke.visibility = View.GONE

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
                val label = if (activity == "Yoga") "Yoga Style" else "Intensity Level"
                view?.findViewById<TextView>(R.id.tvIntensityLabel)?.text = label
            }
            "Swimming" -> cardSwimStroke.visibility = View.VISIBLE
        }
    }

    private fun calculateCalories() {
        val weight = etUserWeight.text.toString().toDoubleOrNull() ?: 0.0
        val duration = etDuration.text.toString().toIntOrNull() ?: 0
        val activity = spinnerActivity.selectedItem.toString()

        if (weight <= 0 || duration <= 0) {
            Toast.makeText(requireContext(), "Please enter weight and duration", Toast.LENGTH_SHORT).show()
            return
        }

        tvDetails.text = "Calculating..."

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.calculateCalories(
                    mapOf(
                        "activity" to activity,
                        "time_minutes" to duration.toString(),
                        "weight" to weight.toString(),
                        "level" to getServerLevelKey()
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    currentCalories = response.body()!!.burned_calories ?: 0.0
                    tvCalories.text = "Estimated: ${currentCalories.toInt()} kcal"
                    tvDetails.text = "Calculated on Server"
                } else {
                    calculateLocalCalories(weight, duration, activity)
                }
            } catch (e: Exception) {
                calculateLocalCalories(weight, duration, activity)
            }
        }
    }

    private fun saveWorkout() {
        val activity = spinnerActivity.selectedItem.toString()
        val duration = etDuration.text.toString().toIntOrNull() ?: 0
        if (duration <= 0) {
            Toast.makeText(requireContext(), "Invalid duration", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.createWorkout(
                    mapOf(
                        "activity" to activity,
                        "time_minutes" to duration.toString(),
                        "burned_calories" to currentCalories.toString(),
                        "level" to getServerLevelKey()
                    )
                )

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Workout Saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to save", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error saving", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateLocalCalories(weight: Double, duration: Int, activity: String) {
        val met = getMETValue(activity)
        val cals = met * weight * (duration / 60.0)
        currentCalories = cals
        tvCalories.text = "Estimated: ${cals.toInt()} kcal"
        tvDetails.text = "Based on MET: $met (Local)"
    }

    private fun getMETValue(activity: String): Double {
        val intensityText = if (::spinnerIntensity.isInitialized) spinnerIntensity.selectedItem.toString() else "Moderate"
        val strokeText = if (::spinnerStroke.isInitialized) spinnerStroke.selectedItem.toString() else "Freestyle"
        val pace = etPace.text.toString().toDoubleOrNull()
        val speed = etSpeed.text.toString().toDoubleOrNull()
        val walkSpeed = etWalkSpeed.text.toString().toDoubleOrNull()

        return when (activity) {
            "Running" -> pace?.let { 60.0 / it }?.let { if (it < 8) 8.0 else if (it <= 10) 8.3 else if (it <= 12) 10.0 else 11.5 } ?: 10.0
            "Cycling" -> speed?.let { if (it < 18) 6.8 else if (it < 24) 8.0 else 11.0 } ?: 8.0
            "Walking" -> walkSpeed?.let { if (it < 4) 2.8 else if (it < 6) 3.8 else 5.0 } ?: 3.8
            "Swimming" -> when(strokeText) {"Freestyle" -> 8.0; "Breaststroke" -> 10.0; "Butterfly" -> 13.8; else -> 8.0}
            "Weightlifting" -> when(intensityText) {"Light" -> 3.0; "Moderate" -> 6.0; "Heavy" -> 8.0; else -> 6.0}
            "Yoga" -> when(intensityText) {"Light" -> 2.5; "Moderate" -> 4.0; "Heavy" -> 4.0; else -> 2.5}
            "HIIT" -> when(intensityText) {"Light" -> 8.0; "Moderate" -> 11.0; "Heavy" -> 14.0; else -> 8.0}
            else -> 4.0
        }
    }

    private fun getServerLevelKey(): String {
        val activity = spinnerActivity.selectedItem.toString()
        val intensityText = if (::spinnerIntensity.isInitialized) spinnerIntensity.selectedItem.toString().lowercase() else "moderate"
        val pace = etPace.text.toString().toDoubleOrNull()
        val speed = etSpeed.text.toString().toDoubleOrNull()
        val walkSpeed = etWalkSpeed.text.toString().toDoubleOrNull()
        val stroke = if (::spinnerStroke.isInitialized) spinnerStroke.selectedItem.toString().lowercase() else "freestyle"

        return when (activity) {
            "Running" -> pace?.let { val s = 60.0/it; if (s < 9) "8" else if (s<11) "10" else "12" } ?: "10"
            "Cycling" -> speed?.let { if(it<18) "16" else if(it<23) "20" else "25" } ?: "20"
            "Walking" -> walkSpeed?.let { if(it<4) "3" else if(it<6) "5" else "6.5" } ?: "5"
            "Weightlifting" -> if(intensityText=="heavy") "vigorous" else intensityText
            "Yoga" -> if(intensityText=="heavy") "power" else "hatha"
            "HIIT" -> if(intensityText=="heavy") "extreme" else intensityText
            "Swimming" -> stroke
            else -> "moderate"
        }
    }

//    private fun saveWorkout() {
//        val activity = spinnerActivity.selectedItem.toString()
//        val duration = etDuration.text.toString().toIntOrNull() ?: 0
//        if (duration <= 0) { Toast.makeText(requireContext(), "Invalid duration", Toast.LENGTH_SHORT).show(); return }
//
//        lifecycleScope.launch {
//            try {
//                val response = RetrofitClient.instance.createWorkout(
//                    mapOf(
//                        "activity" to activity,
//                        "time_minutes" to duration.toString(),
//                        "burned_calories" to currentCalories.toString(),
//                        "level" to getServerLevelKey()
//                    )
//                )
//
//                if (response.isSuccessful) {
//                    Toast.makeText(requireContext(), "Workout Saved", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(requireContext(), "Failed to save", Toast.LENGTH_SHORT).show()
//                }
//
//            } catch (e: Exception) { Toast.makeText(requireContext(), "Error saving", Toast.LENGTH_SHORT).show() }
//        }
//    }
}
