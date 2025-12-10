package com.swu.filetest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    companion object {
        private const val REQUEST_CODE = 100
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val fileButton = findViewById<Button>(R.id.filewriteread)
        val fileToSD = findViewById<Button>(R.id.filetosd)
        fileButton.setOnClickListener(this)
        fileToSD.setOnClickListener(this)
        
        // 检查并申请读写SD卡权限
        checkAndRequestPermissions()
    }

    /**
     * 检查并申请读写SD卡权限
     */
    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        
        // 检查写权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        // 检查读权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        // 如果有未授予的权限，则申请
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                REQUEST_CODE
            )
        } else {
            Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            var allPermissionsGranted = true
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
            
            if (allPermissionsGranted) {
                Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "权限被拒绝，可能无法正常使用SD卡功能", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.filewriteread -> {
                val intent = Intent(this, FileWriteRead::class.java)
                startActivity(intent)
            }
            R.id.filetosd -> {
                // 检查权限后再启动Activity
                if (hasStoragePermissions()) {
                    val intent = Intent(this, FileWriteReadToSD::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "请先授予SD卡读写权限", Toast.LENGTH_SHORT).show()
                    checkAndRequestPermissions()
                }
            }
        }
    }
    
    /**
     * 检查是否已授予存储权限
     */
    private fun hasStoragePermissions(): Boolean {
        val writeGranted = ContextCompat.checkSelfPermission(
            this, 
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        
        val readGranted = ContextCompat.checkSelfPermission(
            this, 
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        
        return writeGranted && readGranted
    }
}