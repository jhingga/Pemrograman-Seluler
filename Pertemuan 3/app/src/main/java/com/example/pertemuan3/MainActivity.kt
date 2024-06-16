package com.example.pertemuan3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lengthInput = findViewById<EditText>(R.id.length_input)
        val widthInput = findViewById<EditText>(R.id.width_input)
        val heightInput = findViewById<EditText>(R.id.height_input)
        val calculateButton = findViewById<Button>(R.id.calculate_button)
        val resultText = findViewById<TextView>(R.id.result_text)
        val sampleFragmentButton = findViewById<Button>(R.id.sample_fragment_button)

        calculateButton.setOnClickListener {
            val length = lengthInput.text.toString().toDoubleOrNull()
            val width = widthInput.text.toString().toDoubleOrNull()
            val height = heightInput.text.toString().toDoubleOrNull()

            if (length != null && width != null && height != null) {
                val volume = length * width * height
                resultText.text = "Volume: $volume"
            } else {
                resultText.text = "Mohon masukkan nilai yang valid."
            }
        }

        sampleFragmentButton.setOnClickListener {
            val fragment = ActivityFragment.newInstance("Ini diklik dari Activity")
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }
}