package com.example.reactiveex.data

import java.io.Serializable

data class Staff(
    var id: Long = -1L,
    var name: String = "",
    var yearOfBirth: String = "",
    var hometown: String = "",
) : Serializable
