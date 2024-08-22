package com.example.reactiveex

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                }
        }

        searchEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                }

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

        staffAdapter.setOnItemClickListener { staff ->
            val intent = Intent(this, StaffDetailsActivity::class.java)
            intent.putExtra("staff", staff)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        staffList.clear()
        staffList.addAll(db.getAllStaff())
        staffAdapter.notifyDataSetChanged()
    }
}
