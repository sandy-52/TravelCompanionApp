package com.example.travelcompanionapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var editTextValue: EditText
    private lateinit var buttonConvert: Button
    private lateinit var textViewResult: TextView

    private val categories = arrayOf("Currency", "Fuel", "Temperature")
    private val currencyUnits = arrayOf("USD", "AUD", "EUR", "JPY", "GBP")
    private val fuelUnits = arrayOf("mpg", "km/L")
    private val temperatureUnits = arrayOf("Celsius", "Fahrenheit", "Kelvin")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        editTextValue = findViewById(R.id.editTextValue)
        buttonConvert = findViewById(R.id.buttonConvert)
        textViewResult = findViewById(R.id.textViewResult)

        spinnerCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        updateUnitSpinners("Currency")

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                updateUnitSpinners(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        buttonConvert.setOnClickListener {
            performConversion()
        }
    }

    private fun updateUnitSpinners(category: String) {
        val units = when (category) {
            "Currency" -> currencyUnits
            "Fuel" -> fuelUnits
            "Temperature" -> temperatureUnits
            else -> currencyUnits
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter
    }

    private fun performConversion() {
        val inputText = editTextValue.text.toString().trim()

        if (inputText.isEmpty()) {
            editTextValue.error = "Please enter a value"
            return
        }

        val inputValue = inputText.toDoubleOrNull()
        if (inputValue == null) {
            editTextValue.error = "Please enter a valid number"
            return
        }

        val category = spinnerCategory.selectedItem.toString()
        val fromUnit = spinnerFrom.selectedItem.toString()
        val toUnit = spinnerTo.selectedItem.toString()

        if (fromUnit == toUnit) {
            textViewResult.text = "Result: $inputValue $toUnit"
            Toast.makeText(this, "Same unit selected", Toast.LENGTH_SHORT).show()
            return
        }

        if (category == "Fuel" && inputValue < 0) {
            editTextValue.error = "Fuel value cannot be negative"
            return
        }

        val result = convertValue(category, fromUnit, toUnit, inputValue)
        textViewResult.text = "Result: %.2f %s".format(result, toUnit)
    }

    private fun convertValue(category: String, from: String, to: String, value: Double): Double {
        return when (category) {
            "Currency" -> convertCurrency(from, to, value)
            "Fuel" -> convertFuel(from, to, value)
            "Temperature" -> convertTemperature(from, to, value)
            else -> value
        }
    }

    private fun convertCurrency(from: String, to: String, value: Double): Double {
        val usdValue = when (from) {
            "USD" -> value
            "AUD" -> value / 1.55
            "EUR" -> value / 0.92
            "JPY" -> value / 148.50
            "GBP" -> value / 0.78
            else -> value
        }
        return when (to) {
            "USD" -> usdValue
            "AUD" -> usdValue * 1.55
            "EUR" -> usdValue * 0.92
            "JPY" -> usdValue * 148.50
            "GBP" -> usdValue * 0.78
            else -> usdValue
        }
    }
    private fun convertFuel(from: String, to: String, value: Double): Double {
        return when {
            from == "mpg" && to == "km/L" -> value * 0.425
            from == "km/L" && to == "mpg" -> value / 0.425
            else -> value
        }
    }
    private fun convertTemperature(from: String, to: String, value: Double): Double {
        return when {
            from == "Celsius" && to == "Fahrenheit" -> (value * 1.8) + 32
            from == "Fahrenheit" && to == "Celsius" -> (value - 32) / 1.8
            from == "Celsius" && to == "Kelvin" -> value + 273.15
            else -> value
        }
    }
}