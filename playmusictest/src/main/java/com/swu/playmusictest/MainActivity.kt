package com.swu.playmusictest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    private lateinit var btnPlayPause: Button
    private lateinit var btnStop: Button
    private lateinit var btnChooseMusic: Button
    private lateinit var seekBar: SeekBar
    private lateinit var tvMusicInfo: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var musicUri: Uri? = null
    private val handler = Handler(Looper.getMainLooper())
    private var updateSeekBarRunnable: Runnable? = null
    
    // 从文件选择器选择音乐的启动器
    private val chooseMusicLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                musicUri = uri
                loadMusic(uri)
            }
        }
    }
    
    // 权限请求启动器
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val storageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_AUDIO] ?: false
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }
        
        if (storageGranted) {
            Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "需要存储权限才能选择音乐文件", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        checkPermissions()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        btnPlayPause = findViewById(R.id.btn_play_pause)
        btnStop = findViewById(R.id.btn_stop)
        btnChooseMusic = findViewById(R.id.btn_choose_music)
        seekBar = findViewById(R.id.seek_bar)
        tvMusicInfo = findViewById(R.id.tv_music_info)
        tvCurrentTime = findViewById(R.id.tv_current_time)
        tvTotalTime = findViewById(R.id.tv_total_time)
        
        btnPlayPause.setOnClickListener(this)
        btnStop.setOnClickListener(this)
        btnChooseMusic.setOnClickListener(this)
        
        // 设置进度条监听
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && mediaPlayer != null) {
                    val position = (progress * mediaPlayer!!.duration) / 100
                    mediaPlayer?.seekTo(position)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_play_pause -> {
                if (mediaPlayer == null) {
                    Toast.makeText(this, "请先选择音乐文件", Toast.LENGTH_SHORT).show()
                    return
                }
                if (isPlaying) {
                    pauseMusic()
                } else {
                    playMusic()
                }
            }
            R.id.btn_stop -> {
                stopMusic()
            }
            R.id.btn_choose_music -> {
                chooseMusic()
            }
        }
    }
    
    /**
     * 检查并申请权限
     */
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用 READ_MEDIA_AUDIO
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_AUDIO))
            }
        } else {
            // Android 12 及以下使用 READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }
    }
    
    /**
     * 选择音乐文件
     */
    private fun chooseMusic() {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        
        if (!hasPermission) {
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show()
            checkPermissions()
            return
        }
        
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        chooseMusicLauncher.launch(intent)
    }
    
    /**
     * 加载音乐
     */
    private fun loadMusic(uri: Uri) {
        try {
            // 释放之前的MediaPlayer
            releaseMediaPlayer()
            
            // 创建新的MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@MainActivity, uri)
                prepare()
                
                // 设置完成监听
                setOnCompletionListener {
                    stopMusic()
                    Toast.makeText(this@MainActivity, "播放完成", Toast.LENGTH_SHORT).show()
                }
                
                // 设置错误监听
                setOnErrorListener { _, what, extra ->
                    Toast.makeText(this@MainActivity, "播放错误: what=$what, extra=$extra", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            
            // 设置进度条最大值
            seekBar.max = 100
            seekBar.progress = 0
            
            // 更新UI
            tvMusicInfo.text = "音乐已加载"
            tvTotalTime.text = formatTime(mediaPlayer!!.duration)
            tvCurrentTime.text = "00:00"
            
            Toast.makeText(this, "音乐加载成功", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "加载音乐失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(this, "加载音乐失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    /**
     * 播放音乐
     */
    private fun playMusic() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                isPlaying = true
                btnPlayPause.text = "暂停"
                startUpdateSeekBar()
            }
        }
    }
    
    /**
     * 暂停音乐
     */
    private fun pauseMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
                btnPlayPause.text = "播放"
                stopUpdateSeekBar()
            }
        }
    }
    
    /**
     * 停止音乐
     */
    private fun stopMusic() {
        mediaPlayer?.let {
            it.stop()
            try {
                it.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            it.seekTo(0)
            isPlaying = false
            btnPlayPause.text = "播放"
            seekBar.progress = 0
            tvCurrentTime.text = "00:00"
            stopUpdateSeekBar()
        }
    }
    
    /**
     * 开始更新进度条
     */
    private fun startUpdateSeekBar() {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        val currentPosition = it.currentPosition
                        val duration = it.duration
                        
                        if (duration > 0) {
                            val progress = (currentPosition * 100) / duration
                            seekBar.progress = progress
                            tvCurrentTime.text = formatTime(currentPosition)
                        }
                        
                        handler.postDelayed(this, 100)
                    }
                }
            }
        }
        handler.post(updateSeekBarRunnable!!)
    }
    
    /**
     * 停止更新进度条
     */
    private fun stopUpdateSeekBar() {
        updateSeekBarRunnable?.let {
            handler.removeCallbacks(it)
            updateSeekBarRunnable = null
        }
    }
    
    /**
     * 格式化时间（毫秒转 mm:ss）
     */
    private fun formatTime(milliseconds: Int): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
    
    /**
     * 释放MediaPlayer资源
     */
    private fun releaseMediaPlayer() {
        stopUpdateSeekBar()
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        isPlaying = false
    }
    
    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }
    
    override fun onPause() {
        super.onPause()
        // 可选：在Activity暂停时暂停播放
        // pauseMusic()
    }
}
