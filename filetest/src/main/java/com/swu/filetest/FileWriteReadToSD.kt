package com.swu.filetest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_write_read_to_sd)
        textView = findViewById(R.id.textView)

        // 直接执行文件操作（权限已在MainActivity中申请）
        performFileOperations()
    }

    /**
     * 执行文件读写操作
     */
    private fun performFileOperations() {
        if (isSDCardAvailable()) {
            val sdCardPath = getSDCardPath()
            val filePath = "$sdCardPath/example.txt"
            Toast.makeText(this, "$filePath", Toast.LENGTH_SHORT).show()
            writeFileToSDCard(filePath, "测试数据，写入SD卡的文件中")
            readFileFromSDCard(filePath)
            Toast.makeText(this, "SD卡可用", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "SD卡不可用", Toast.LENGTH_SHORT).show()
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
        // 检查读权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有读取权限", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val file = File(filePath)
            if (!file.exists()) {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show()
                return
            }
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
            Toast.makeText(this, "文件读取成功", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "读取文件失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    /**
     * 把数据写入SD卡中指定的文件
     * @param filePath 文件的路径和名称
     * @param content 文件的内容
     */
    fun writeFileToSDCard(filePath: String, content: String) {
        // 检查写权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有写入权限", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val file = File(filePath)
            // 确保父目录存在
            val parentDir = file.parentFile
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
            }
            val fos = FileOutputStream(file)
            val bw = BufferedWriter(OutputStreamWriter(fos))
            bw.write(content)
            bw.close()
            fos.close()
            Toast.makeText(this, "文件写入成功", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "无法写入文件: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
