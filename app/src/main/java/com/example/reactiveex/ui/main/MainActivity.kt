package com.example.reactiveex.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reactiveex.R
import com.example.reactiveex.data.DatabaseHelper
import com.example.reactiveex.data.Staff
import com.example.reactiveex.ui.staffdetails.StaffAdapter
import com.example.reactiveex.ui.staffdetails.StaffDetailsActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var staffAdapter: StaffAdapter
    private lateinit var db: DatabaseHelper
    private lateinit var staffList: MutableList<Staff>
    private lateinit var searchEditText: EditText

    private val searchFlow = MutableSharedFlow<String>(replay = 1) // SharedFlow for search input

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = DatabaseHelper(this)
        staffList = db.getAllStaff()
        staffAdapter = StaffAdapter(staffList)
        recyclerView.adapter = staffAdapter

        searchEditText = findViewById(R.id.searchEditText)

        lifecycleScope.launch {
            searchFlow
                .debounce(1000)
                .collect { query ->
                    staffList.clear()
                    staffList.addAll(db.searchStaff(query))
                    staffAdapter.notifyDataSetChanged()
                    updateFilterOptions() // Cập nhật các tùy chọn bộ lọc dựa trên kết quả tìm kiếm hiện tại
                }
        }

        searchEditText.addTextChangedListener(
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
                    lifecycleScope.launch {
                        searchFlow.emit(editable.toString())
                    }
                }
            },
        )

        findViewById<AppCompatButton>(R.id.addStaff).setOnClickListener {
            val intent = Intent(this, StaffDetailsActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.byYearButton).setOnClickListener {
            val years = db.getDistinctYears()
            showFilterDialog("year", years)
        }

        findViewById<Button>(R.id.byHometownButton).setOnClickListener {
            val hometowns = db.getDistinctHometowns()
            showFilterDialog("hometown", hometowns)
        }

        staffAdapter.setOnItemClickListener { staff ->
            val intent = Intent(this, StaffDetailsActivity::class.java)
            intent.putExtra("staff", staff)
            startActivity(intent)
        }
    }

    private fun showFilterDialog(
        type: String,
        options: List<String>,
    ) {
        val allOptions = mutableListOf("Clear Filter")
        allOptions.addAll(options)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select $type")

        builder.setItems(allOptions.toTypedArray()) { _, which ->
            val selectedOption = allOptions[which]
            if (selectedOption == "Clear Filter") {
                showAllStaff() // Show staff according to the current search query
            } else {
                filterStaffBy(type, selectedOption)
            }
        }

        builder.show()
    }

    private fun filterStaffBy(
        type: String,
        selectedOption: String,
    ) {
        lifecycleScope.launch {
            val filteredStaff =
                when (type) {
                    "year" -> db.searchStaffByYear(selectedOption)
                    "hometown" -> db.searchStaffByHometown(selectedOption)
                    else -> listOf()
                }
            staffList.clear()
            staffList.addAll(filteredStaff)
            staffAdapter.notifyDataSetChanged()
            updateFilterOptions() // Cập nhật các tùy chọn bộ lọc sau khi lọc
        }
    }

    override fun onResume() {
        super.onResume()
        staffList.clear()
        staffList.addAll(db.getAllStaff())
        staffAdapter.notifyDataSetChanged()
    }

    private fun showAllStaff() {
        lifecycleScope.launch {
            val searchQuery = searchEditText.text.toString()
            val staffToShow =
                if (searchQuery.isNotEmpty()) {
                    db.searchStaff(searchQuery) // Get staff that matches the search query
                } else {
                    db.getAllStaff() // Get all staff if no search query
                }
            staffList.clear()
            staffList.addAll(staffToShow)
            staffAdapter.notifyDataSetChanged()
        }
    }

    private fun updateFilterOptions() {
        lifecycleScope.launch {
            val searchQuery = searchEditText.text.toString()

            // Lấy danh sách nhân viên đã lọc dựa trên truy vấn tìm kiếm hiện tại
            val filteredStaff =
                if (searchQuery.isNotEmpty()) {
                    db.searchStaff(searchQuery)
                } else {
                    db.getAllStaff()
                }

            // Lấy các năm và quê quán khác nhau từ danh sách nhân viên đã lọc
            val years = filteredStaff.map { it.yearOfBirth }.distinct()
            val hometowns = filteredStaff.map { it.hometown }.distinct()

            // Cập nhật các nút bộ lọc
            findViewById<Button>(R.id.byYearButton).setOnClickListener {
                showFilterDialog("year", years)
            }

            findViewById<Button>(R.id.byHometownButton).setOnClickListener {
                showFilterDialog("hometown", hometowns)
            }
        }
    }
}
