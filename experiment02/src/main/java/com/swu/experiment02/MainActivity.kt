package com.swu.experiment02

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var intent = Intent()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val lineLayout = findViewById<Button>(R.id.line_layout)
        val relativeLayout = findViewById<Button>(R.id.relative_Layout)
        val gridLayout = findViewById<Button>(R.id.grid_Layout)
        val tableLayout = findViewById<Button>(R.id.table_Layout)
        val constraintLayout = findViewById<Button>(R.id.constraint_layout)
        val frameLayout = findViewById<Button>(R.id.frame_Layout)

        lineLayout.setOnClickListener(this)
        relativeLayout.setOnClickListener(this)
        gridLayout.setOnClickListener(this)
        tableLayout.setOnClickListener(this)
        constraintLayout.setOnClickListener(this)
        frameLayout.setOnClickListener(this)

        initView()
    }


    private fun initView() {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.line_layout -> {
                intent.setClass(this@MainActivity, LineActivity::class.java)
                startActivity(intent)
            }

            R.id.relative_Layout -> {
                intent.setClass(this@MainActivity, RelativeLayout::class.java)
                startActivity(intent)
            }

            R.id.grid_Layout -> {
                intent.setClass(this@MainActivity, GridLayoutActivity::class.java)
                startActivity(intent)
            }

            R.id.table_Layout -> {
                intent.setClass(this@MainActivity, TableLayoutActivity::class.java)
                startActivity(intent)
            }

            R.id.constraint_layout -> {
                intent.setClass(this@MainActivity, ConstraintlayoutActivity::class.java)
                startActivity(intent)
            }

            R.id.frame_Layout -> {
                intent.setClass(this@MainActivity, FrameLayoutActivity::class.java)
                startActivity(intent)
            }
        }
    }
}