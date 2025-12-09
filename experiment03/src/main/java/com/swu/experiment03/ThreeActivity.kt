package com.swu.experiment03

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ThreeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three)

        val textView = findViewById<TextView>(R.id.tv)
        val person = intent.getSerializableExtra("person") as Person
        textView.text = person.toString()
    }
}