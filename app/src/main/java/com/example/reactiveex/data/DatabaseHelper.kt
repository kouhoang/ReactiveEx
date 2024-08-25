package com.example.reactiveex.data

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

        // Insert default data if the table is blank
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
        val database = db ?: writableDatabase // Use writableDatabase if db is null
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
        val database = db ?: writableDatabase // Use writableDatabase if db is null

        // Check if the table has data or not
        val cursor = database.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        cursor?.use {
            it.moveToFirst()
            val count = it.getInt(0)
            if (count == 0) {
                // If the table is blank, insert default data
                getDefaultStaffList().forEach { staff ->
                    insertStaff(staff, database)
                }
            }
        }
    }

    fun getDefaultStaffList(): List<Staff> =
        listOf(
            Staff(id = -1, name = "Trần Bình An", yearOfBirth = "1994", hometown = "Quảng Ninh"),
            Staff(id = -1, name = "Ninh Diêu", yearOfBirth = "1994", hometown = "Hà Nội"),
            Staff(id = -1, name = "Nguyễn Tú", yearOfBirth = "2000", hometown = "Lào Cai"),
            Staff(id = -1, name = "Tề Tĩnh Xuân", yearOfBirth = "1960", hometown = "Nghệ An"),
            Staff(id = -1, name = "Lưu Tiện Dương", yearOfBirth = "1992", hometown = "Quảng Ninh"),
            Staff(id = -1, name = "Cố Xán", yearOfBirth = "1998", hometown = "Quảng Ninh"),
            Staff(id = -1, name = "Trĩ Khuê", yearOfBirth = "1996", hometown = "Hải Dương"),
            Staff(id = -1, name = "Tống Tập Tân", yearOfBirth = "1993", hometown = "Hà Nội"),
            Staff(id = -1, name = "Thôi Đông Sơn", yearOfBirth = "1984", hometown = "Hồ Chí Minh"),
            Staff(id = -1, name = "Bùi Tiền", yearOfBirth = "1999", hometown = "Đà Nẵng"),
            Staff(id = -1, name = "Mao Tiểu Đồng", yearOfBirth = "1990", hometown = "Kon Tum"),
            Staff(id = -1, name = "A Lương", yearOfBirth = "1970", hometown = "Thanh Hóa"),
            Staff(id = -1, name = "Bạch Trạch", yearOfBirth = "1986", hometown = "Hồ Chí Minh"),
            Staff(id = -1, name = "Thôi Thành", yearOfBirth = "1989", hometown = "Đà Nẵng"),
            Staff(id = -1, name = "Tú Hổ", yearOfBirth = "1981", hometown = "Hà Nội"),
            Staff(id = -1, name = "Quách Trúc Tửu", yearOfBirth = "1997", hometown = "Hải Phòng"),
            Staff(id = -1, name = "Tào Tình Lang", yearOfBirth = "1992", hometown = "Nam Định"),
            Staff(id = -1, name = "Lý Nhị", yearOfBirth = "1977", hometown = "Nam Định"),
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

    fun getDistinctYears(): List<String> {
        val years = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_YEAR_OF_BIRTH FROM $TABLE_NAME", null)
        cursor?.use {
            while (it.moveToNext()) {
                years.add(it.getString(0))
            }
        }
        return years
    }

    fun getDistinctHometowns(): List<String> {
        val hometowns = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_HOMETOWN FROM $TABLE_NAME", null)
        cursor?.use {
            while (it.moveToNext()) {
                hometowns.add(it.getString(0))
            }
        }
        return hometowns
    }

    fun searchStaffByYear(year: String): List<Staff> {
        val staffList = mutableListOf<Staff>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_YEAR_OF_BIRTH = ?", arrayOf(year))

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val staff =
                        Staff(
                            id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                            name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                            yearOfBirth = it.getString(it.getColumnIndexOrThrow(COLUMN_YEAR_OF_BIRTH)),
                            hometown = it.getString(it.getColumnIndexOrThrow(COLUMN_HOMETOWN)),
                        )
                    staffList.add(staff)
                } while (it.moveToNext())
            }
        }
        return staffList
    }

    fun searchStaffByHometown(hometown: String): List<Staff> {
        val staffList = mutableListOf<Staff>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_HOMETOWN = ?", arrayOf(hometown))

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val staff =
                        Staff(
                            id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                            name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                            yearOfBirth = it.getString(it.getColumnIndexOrThrow(COLUMN_YEAR_OF_BIRTH)),
                            hometown = it.getString(it.getColumnIndexOrThrow(COLUMN_HOMETOWN)),
                        )
                    staffList.add(staff)
                } while (it.moveToNext())
            }
        }
        return staffList
    }
}
