package android.app.barcafanprofileapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val INCH_VALUE = 2.54

    private lateinit var inchesInput: EditText
    private lateinit var convertButton: Button
    private lateinit var textViewCentimetres: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        inchesInput = findViewById(R.id.editTextInches)
        convertButton = findViewById(R.id.buttonConvert)
        textViewCentimetres = findViewById(R.id.textViewConvert)

        // 1 inch -> 2.54 cm

        convertButton.setOnClickListener {
            if (!inchesInput.text.toString().isEmpty()) {
                val result = inchesInput.text.toString().toDouble() * INCH_VALUE
                textViewCentimetres.text = "Result: $result"
            } else {
                textViewCentimetres.text = getString(R.string.text)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}