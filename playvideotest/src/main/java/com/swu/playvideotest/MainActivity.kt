package com.swu.playvideotest

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
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    private lateinit var videoView: VideoView
    private lateinit var btnPlayPause: Button
    private lateinit var btnStop: Button
    private lateinit var btnChooseVideo: Button
    private lateinit var seekBar: SeekBar
    private lateinit var tvVideoInfo: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    
    private var videoUri: Uri? = null
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())
    private var updateSeekBarRunnable: Runnable? = null
    
    // 从文件选择器选择视频的启动器
    private val chooseVideoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                videoUri = uri
                loadVideo(uri)
            }
        }
    }
    
    // 权限请求启动器
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val storageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_VIDEO] ?: false
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }
        
        if (storageGranted) {
            Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "需要存储权限才能选择视频文件", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupVideoView()
        checkPermissions()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        videoView = findViewById(R.id.video_view)
        btnPlayPause = findViewById(R.id.btn_play_pause)
        btnStop = findViewById(R.id.btn_stop)
        btnChooseVideo = findViewById(R.id.btn_choose_video)
        seekBar = findViewById(R.id.seek_bar)
        tvVideoInfo = findViewById(R.id.tv_video_info)
        tvCurrentTime = findViewById(R.id.tv_current_time)
        tvTotalTime = findViewById(R.id.tv_total_time)
        
        btnPlayPause.setOnClickListener(this)
        btnStop.setOnClickListener(this)
        btnChooseVideo.setOnClickListener(this)
        
        // 设置进度条监听
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && videoView.isPlaying) {
                    val position = (progress * videoView.duration) / 100
                    videoView.seekTo(position)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    /**
     * 设置VideoView监听器
     */
    private fun setupVideoView() {
        videoView.setOnPreparedListener { mediaPlayer ->
            // 视频准备完成
            seekBar.max = 100
            seekBar.progress = 0
            tvTotalTime.text = formatTime(mediaPlayer.duration)
            tvCurrentTime.text = "00:00"
            
            // 确保VideoView正确显示
            videoView.requestLayout()
            videoView.invalidate()
            
            Toast.makeText(this, "视频加载成功", Toast.LENGTH_SHORT).show()
        }
        
        videoView.setOnCompletionListener {
            // 视频播放完成
            stopVideo()
            Toast.makeText(this, "播放完成", Toast.LENGTH_SHORT).show()
        }
        
        videoView.setOnErrorListener { _, what, extra ->
            Toast.makeText(this, "播放错误: what=$what, extra=$extra", Toast.LENGTH_SHORT).show()
            true
        }
        
        videoView.setOnInfoListener { _, what, extra ->
            when (what) {
                android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                    // 视频开始渲染
                    Toast.makeText(this, "视频开始播放", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_play_pause -> {
                if (videoUri == null) {
                    Toast.makeText(this, "请先选择视频文件", Toast.LENGTH_SHORT).show()
                    return
                }
                if (isPlaying) {
                    pauseVideo()
                } else {
                    playVideo()
                }
            }
            R.id.btn_stop -> {
                stopVideo()
            }
            R.id.btn_choose_video -> {
                chooseVideo()
            }
        }
    }
    
    /**
     * 检查并申请权限
     */
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用 READ_MEDIA_VIDEO
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_VIDEO))
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
     * 选择视频文件
     */
    private fun chooseVideo() {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
        }
        
        if (!hasPermission) {
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show()
            checkPermissions()
            return
        }
        
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        chooseVideoLauncher.launch(intent)
    }
    
    /**
     * 加载视频
     */
    private fun loadVideo(uri: Uri) {
        try {
            // 停止当前播放
            stopVideo()
            
            // 设置视频URI
            videoView.setVideoURI(uri)
            tvVideoInfo.text = "视频已加载"
            
            // 确保VideoView可见并获取焦点
            videoView.visibility = View.VISIBLE
            videoView.requestFocus()
            
            // 强制刷新布局
            videoView.requestLayout()
            videoView.invalidate()
        } catch (e: Exception) {
            Toast.makeText(this, "加载视频失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    /**
     * 播放视频
     */
    private fun playVideo() {
        if (!videoView.isPlaying) {
            // 确保VideoView可见
            videoView.visibility = View.VISIBLE
            videoView.requestFocus()
            
            videoView.start()
            isPlaying = true
            btnPlayPause.text = "暂停"
            startUpdateSeekBar()
        }
    }
    
    /**
     * 暂停视频
     */
    private fun pauseVideo() {
        if (videoView.isPlaying) {
            videoView.pause()
            isPlaying = false
            btnPlayPause.text = "播放"
            stopUpdateSeekBar()
        }
    }
    
    /**
     * 停止视频
     */
    private fun stopVideo() {
        videoView.stopPlayback()
        isPlaying = false
        btnPlayPause.text = "播放"
        seekBar.progress = 0
        tvCurrentTime.text = "00:00"
        stopUpdateSeekBar()
    }
    
    /**
     * 开始更新进度条
     */
    private fun startUpdateSeekBar() {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (videoView.isPlaying && videoView.duration > 0) {
                    val currentPosition = videoView.currentPosition
                    val duration = videoView.duration
                    
                    val progress = (currentPosition * 100) / duration
                    seekBar.progress = progress
                    tvCurrentTime.text = formatTime(currentPosition)
                    
                    handler.postDelayed(this, 100)
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
    
    override fun onPause() {
        super.onPause()
        // Activity暂停时暂停播放
        if (videoView.isPlaying) {
            pauseVideo()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopUpdateSeekBar()
        videoView.stopPlayback()
    }
}
