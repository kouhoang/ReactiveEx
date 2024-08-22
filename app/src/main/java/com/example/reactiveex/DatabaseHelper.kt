package com.example.reactiveex

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(
    context: Context,
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_YEAR_OF_BIRTH TEXT, " +
                "$COLUMN_HOMETOWN TEXT)"
        db?.execSQL(createTable)

        // Chèn dữ liệu mặc định nếu bảng trống
        insertDefaultData(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int,
    ) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertStaff(
        staff: Staff,
        db: SQLiteDatabase? = null,
    ): Long {
        val database = db ?: writableDatabase // Sử dụng writableDatabase nếu db là null
        val values =
            ContentValues().apply {
                put(COLUMN_NAME, staff.name)
                put(COLUMN_YEAR_OF_BIRTH, staff.yearOfBirth)
                put(COLUMN_HOMETOWN, staff.hometown)
            }
        return database.insert(TABLE_NAME, null, values)
    }

    fun updateStaff(staff: Staff): Int {
        val db = writableDatabase
        val values =
            ContentValues().apply {
                put(COLUMN_NAME, staff.name)
                put(COLUMN_YEAR_OF_BIRTH, staff.yearOfBirth)
                put(COLUMN_HOMETOWN, staff.hometown)
            }
        return db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(staff.id.toString()))
    }

    fun deleteStaff(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun getAllStaff(): MutableList<Staff> {
        val staffList = mutableListOf<Staff>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val staff =
                    Staff(
                        id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                        yearOfBirth = it.getString(it.getColumnIndexOrThrow(COLUMN_YEAR_OF_BIRTH)),
                        hometown = it.getString(it.getColumnIndexOrThrow(COLUMN_HOMETOWN)),
                    )
                staffList.add(staff)
            }
        }
        return staffList
    }

    fun searchStaff(query: String): List<Staff> {
        val staffList = mutableListOf<Staff>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME LIKE ?", arrayOf("%$query%"))

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                    val name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME))
                    val yearOfBirth = it.getString(it.getColumnIndexOrThrow(COLUMN_YEAR_OF_BIRTH))
                    val hometown = it.getString(it.getColumnIndexOrThrow(COLUMN_HOMETOWN))

                    val staff = Staff(id, name, yearOfBirth, hometown)
                    staffList.add(staff)
                } while (it.moveToNext())
            }
        }
        return staffList
    }

    private fun insertDefaultData(db: SQLiteDatabase?) {
        val database = db ?: writableDatabase // Sử dụng writableDatabase nếu db là null

        // Kiểm tra xem bảng có dữ liệu chưa
        val cursor = database.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        cursor?.use {
            it.moveToFirst()
            val count = it.getInt(0)
            if (count == 0) {
                // Nếu bảng trống, chèn dữ liệu mẫu
                getDefaultStaffList().forEach { staff ->
                    insertStaff(staff, database)
                }
            }
        }
    }

    fun getDefaultStaffList(): List<Staff> =
        listOf(
            Staff(id = -1, name = "John Doe", yearOfBirth = "1990", hometown = "New York"),
            Staff(id = -1, name = "Jane Smith", yearOfBirth = "1985", hometown = "Los Angeles"),
            Staff(id = -1, name = "Emily Johnson", yearOfBirth = "1988", hometown = "Chicago"),
            Staff(id = -1, name = "Michael Brown", yearOfBirth = "1992", hometown = "Houston"),
            Staff(id = -1, name = "Jessica Davis", yearOfBirth = "1991", hometown = "Philadelphia"),
            Staff(id = -1, name = "David Wilson", yearOfBirth = "1989", hometown = "Phoenix"),
            Staff(id = -1, name = "Laura Miller", yearOfBirth = "1993", hometown = "San Antonio"),
            Staff(id = -1, name = "James Moore", yearOfBirth = "1987", hometown = "San Diego"),
            Staff(id = -1, name = "Olivia Taylor", yearOfBirth = "1994", hometown = "Dallas"),
            Staff(id = -1, name = "Daniel Anderson", yearOfBirth = "1986", hometown = "San Jose"),
            Staff(id = -1, name = "Sophia Thomas", yearOfBirth = "1995", hometown = "Austin"),
            Staff(id = -1, name = "William Martinez", yearOfBirth = "1984", hometown = "San Francisco"),
            Staff(id = -1, name = "Mia Jackson", yearOfBirth = "1996", hometown = "Columbus"),
            Staff(id = -1, name = "Alexander White", yearOfBirth = "1983", hometown = "Fort Worth"),
            Staff(id = -1, name = "Charlotte Harris", yearOfBirth = "1989", hometown = "Indianapolis"),
            Staff(id = -1, name = "Ethan Clark", yearOfBirth = "1987", hometown = "Charlotte"),
            Staff(id = -1, name = "Amelia Lewis", yearOfBirth = "1990", hometown = "San Francisco"),
            Staff(id = -1, name = "Lucas Walker", yearOfBirth = "1992", hometown = "Seattle"),
        )

    companion object {
        const val DATABASE_NAME = "staff_db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "staff"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_YEAR_OF_BIRTH = "year_of_birth"
        const val COLUMN_HOMETOWN = "hometown"
    }
}
