package com.swu.filetest

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class SDFileHelper(private val context: Context) {

    /**
     * 往SD卡写入文件的方法
     */
    @Throws(Exception::class)
    fun saveFileToSD(filename: String, filecontent: String) {
        // 如果手机已插入sd卡,且app具有读写sd卡的权限
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val fullPath = Environment.getExternalStorageDirectory().canonicalPath + "/" + filename
            // 将String字符串以字节流的形式写入到输出流中
            val output = FileOutputStream(fullPath)
            output.write(filecontent.toByteArray())
            output.close()
        } else {
            Toast.makeText(context, "SD卡不存在或者不可读写", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 读取SD卡中文件的方法
     */
    @Throws(IOException::class)
    fun readFromSD(filename: String): String {
        val sb = StringBuilder("")
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val fullPath = Environment.getExternalStorageDirectory().canonicalPath + "/" + filename
            // 打开文件输入流
            val input = FileInputStream(fullPath)
            val temp = ByteArray(1024)
            var len: Int
            // 读取文件内容
            while (input.read(temp).also { len = it } > 0) {
                sb.append(String(temp, 0, len))
            }
            input.close()
        }
        return sb.toString()
    }
}
