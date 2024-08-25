package com.example.reactiveex.ui.staffdetails

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reactiveex.R
import com.example.reactiveex.data.DatabaseHelper
import com.example.reactiveex.data.Staff
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class StaffDetailsActivity : AppCompatActivity() {
    private lateinit var editName: TextInputEditText
    private lateinit var titleEditName: TextInputLayout
    private lateinit var editYearOfBirth: TextInputEditText
    private lateinit var titleEditYearOfBirth: TextInputLayout
    private lateinit var editHometown: TextInputEditText
    private lateinit var titleEditHometown: TextInputLayout
    private lateinit var db: DatabaseHelper
    private var staff: Staff? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.staff_details)

        // Initialize views
        editName = findViewById(R.id.editName)
        titleEditName = findViewById(R.id.titleEditName)
        editYearOfBirth = findViewById(R.id.editYearOfBirth)
        titleEditYearOfBirth = findViewById(R.id.titleEditYearOfBirth)
        editHometown = findViewById(R.id.editHometown)
        titleEditHometown = findViewById(R.id.titleEditHometown)
        db = DatabaseHelper(this)

        // Load staff data if available
        staff = intent.getSerializableExtra("staff") as? Staff
        staff?.let {
            editName.setText(it.name)
            editYearOfBirth.setText(it.yearOfBirth)
            editHometown.setText(it.hometown)
        }

        // Set up button listeners
        findViewById<Button>(R.id.doneButton).setOnClickListener {
            if (validateFields()) {
                saveOrUpdateStaff()
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.deleteNoteButton).setOnClickListener {
            deleteStaff()
        }

        // Add text change listeners to update helper text
        editName.addTextChangedListener(createTextWatcher(titleEditName))
        editYearOfBirth.addTextChangedListener(createTextWatcher(titleEditYearOfBirth))
        editHometown.addTextChangedListener(createTextWatcher(titleEditHometown))

        // Update helper text based on initial state
        updateHelperText(titleEditName, editName)
        updateHelperText(titleEditYearOfBirth, editYearOfBirth)
        updateHelperText(titleEditHometown, editHometown)
    }

    private fun validateFields(): Boolean {
        val name = editName.text.toString().trim()
        val yearOfBirth = editYearOfBirth.text.toString().trim()
        val hometown = editHometown.text.toString().trim()

        return name.isNotEmpty() && yearOfBirth.isNotEmpty() && hometown.isNotEmpty()
    }

    private fun saveOrUpdateStaff() {
        if (staff == null) {
            staff = Staff()
        }

        staff?.apply {
            name = editName.text.toString()
            yearOfBirth = editYearOfBirth.text.toString()
            hometown = editHometown.text.toString()
        }

        staff?.let {
            if (it.id == -1L) {
                db.insertStaff(it)
            } else {
                db.updateStaff(it)
            }
            finish()
        }
    }

    private fun deleteStaff() {
        staff?.let {
            db.deleteStaff(it.id)
            finish()
        }
    }

    private fun updateHelperText(
        inputLayout: TextInputLayout,
        editText: TextInputEditText,
    ) {
        val text = editText.text.toString().trim()
        inputLayout.helperText = if (text.isEmpty()) "Required*" else null
    }

    private fun createTextWatcher(inputLayout: TextInputLayout): TextWatcher =
        object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) {}

            override fun afterTextChanged(editable: Editable?) {
                when (inputLayout.id) {
                    R.id.titleEditName -> updateHelperText(inputLayout, editName)
                    R.id.titleEditYearOfBirth -> updateHelperText(inputLayout, editYearOfBirth)
                    R.id.titleEditHometown -> updateHelperText(inputLayout, editHometown)
                }
            }
        }
}
