package com.swu.filetest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val fileButton = findViewById<Button>(R.id.filewriteread)
        val fileToSD = findViewById<Button>(R.id.filetosd)
        fileButton.setOnClickListener(this)
        fileToSD.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.filewriteread -> {
                val intent = Intent(this, FileWriteRead::class.java)
                startActivity(intent)
            }
            R.id.filetosd -> {
                val intent = Intent(this, FileWriteReadToSD::class.java)
                startActivity(intent)
            }
        }
    }
}