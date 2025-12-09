package com.swu.experiment03

import java.io.Serializable

data class Person(
    var name: String = "",
    var age: Int = 0,
    var flag: Boolean = false
) : Serializable