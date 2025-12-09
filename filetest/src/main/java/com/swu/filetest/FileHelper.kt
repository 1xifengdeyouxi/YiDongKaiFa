package com.swu.filetest

import android.content.Context
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class FileHelper(private val mContext: Context) {
    
    /**
     * 保存文件的方法，采用字节输出流
     * @param filename 文件名称
     * @param filecontent 写入的文件内容
     */
    @Throws(Exception::class)
    fun save(filename: String, filecontent: String) {
        // 使用私有模式创建文件,创建出来的文件只能被本应用访问,还会覆盖原文件
        val output: FileOutputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE)
        // 将String字符串以字节流的形式写入到输出流中
        output.write(filecontent.toByteArray())
        output.close()
    }

    /**
     * 读文件的方法，使用字节输入流
     */
    @Throws(IOException::class)
    fun read(filename: String): String {
        // 打开文件输入流
        val input: FileInputStream = mContext.openFileInput(filename)
        val temp = ByteArray(1024)
        val sb = StringBuilder("")
        var len: Int
        // 读取文件内容
        while (input.read(temp).also { len = it } > 0) {
            sb.append(String(temp, 0, len))
        }
        // 关闭输入流
        input.close()
        return sb.toString()
    }
}
