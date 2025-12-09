package com.swu.filetest

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class FileWriteRead : AppCompatActivity(), View.OnClickListener {
    private lateinit var editname: EditText
    private lateinit var editdetail: EditText
    private lateinit var btnsave: Button
    private lateinit var btnclean: Button
    private lateinit var btnread: Button
    private lateinit var fileContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_write_read)
        bindViews()
    }

    private fun bindViews() {
        editname = findViewById(R.id.editname)
        editdetail = findViewById(R.id.editdetail)
        btnsave = findViewById(R.id.btnsave)
        btnclean = findViewById(R.id.btnclean)
        btnread = findViewById(R.id.btnread)
        fileContent = findViewById(R.id.fileContent)
        
        btnsave.setOnClickListener(this)
        btnclean.setOnClickListener(this)
        btnread.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnclean -> {
                editdetail.setText("")
                editname.setText("")
            }
            R.id.btnsave -> {
                val fHelper = FileHelper(this)
                val filename = editname.text.toString()
                val filedetail = editdetail.text.toString()
                try {
                    fHelper.save(filename, filedetail)
                    Toast.makeText(applicationContext, "数据写入成功", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "数据写入失败", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnread -> {
                var detail = ""
                val fHelper2 = FileHelper(applicationContext)
                try {
                    val fname = editname.text.toString()
                    detail = fHelper2.read(fname)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                fileContent.text = detail
            }
        }
    }
}
