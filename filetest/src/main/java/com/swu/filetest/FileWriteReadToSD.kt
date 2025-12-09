package com.swu.filetest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class FileWriteReadToSD : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE = 100
    }

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_write_read_to_sd)
        textView = findViewById(R.id.textView)

        // 动态申请写权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                200
            )
        }

        if (isSDCardAvailable()) {
            val sdCardPath = getSDCardPath()
            Toast.makeText(this, "$sdCardPath/example.txt", Toast.LENGTH_SHORT).show()
            writeFileToSDCard("$sdCardPath/example.txt", "测试数据，写入SD卡的文件中")
            readFileFromSDCard("$sdCardPath/example.txt")
            Toast.makeText(this, "SD卡可用", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "SD卡不可用", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() 
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val sdCardPath = getSDCardPath()
            readFileFromSDCard("$sdCardPath/example.txt")
        }
    }

    /**
     * 判断SD卡是否可用
     */
    private fun isSDCardAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    /**
     * 获得SD卡的文件的路径
     */
    private fun getSDCardPath(): String {
        val sdCardFile = Environment.getExternalStorageDirectory()
        return sdCardFile.absolutePath
    }

    /**
     * 读取SD卡指定的文件
     */
    private fun readFileFromSDCard(filePath: String) {
        // 申请读权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
            return
        }
        try {
            val file = File(filePath)
            val fis = FileInputStream(file)
            val br = BufferedReader(InputStreamReader(fis))
            val sb = StringBuilder()
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            br.close()
            fis.close()
            textView.text = sb.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 把数据写入SD卡中指定的文件
     * @param filePath 文件的路径和名称
     * @param content 文件的内容
     */
    fun writeFileToSDCard(filePath: String, content: String) {
        try {
            val file = File(filePath)
            val fos = FileOutputStream(file)
            val bw = BufferedWriter(OutputStreamWriter(fos))
            bw.write(content)
            bw.close()
            fos.close()
        } catch (e: IOException) {
            Toast.makeText(this, "无法写入文件", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
