package com.swu.cameraalbumtest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnTakePhoto: Button
    private lateinit var btnChooseFromAlbum: Button
    private lateinit var imageView: ImageView
    private lateinit var tvImagePath: TextView

    private var imageUri: Uri? = null
    private var outputImage: File? = null

    // 相机拍照的启动器
    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // 显示拍摄的照片
            displayImage(imageUri)
            Toast.makeText(this, "拍照成功", Toast.LENGTH_SHORT).show()
        }
    }

    // 从相册选择图片的启动器
    private val chooseFromAlbumLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                imageUri = uri
                displayImage(uri)
                tvImagePath.text = "图片路径: $uri"
                Toast.makeText(this, "选择图片成功", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 权限请求启动器
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val storageGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false ||
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

        if (cameraGranted && storageGranted) {
            Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "需要相机和存储权限", Toast.LENGTH_SHORT).show()
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
        btnTakePhoto = findViewById(R.id.btn_take_photo)
        btnChooseFromAlbum = findViewById(R.id.btn_choose_from_album)
        imageView = findViewById(R.id.image_view)
        tvImagePath = findViewById(R.id.tv_image_path)

        btnTakePhoto.setOnClickListener(this)
        btnChooseFromAlbum.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_take_photo -> {
                takePhoto()
            }
            R.id.btn_choose_from_album -> {
                chooseFromAlbum()
            }
        }
    }

    /**
     * 检查并申请权限
     */
    private fun checkPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // 相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        // 存储权限（根据Android版本不同）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用 READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // Android 12 及以下使用 READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    /**
     * 打开相机拍照
     */
    private fun takePhoto() {
        // 检查相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "需要相机权限", Toast.LENGTH_SHORT).show()
            checkPermissions()
            return
        }

        // 创建File对象，用于存储拍照后的图片
        // 使用内部缓存目录，更可靠
        val imageFile = File(cacheDir, "output_image.jpg")
        try {
            // 确保父目录存在
            cacheDir.mkdirs()
            // 如果文件已存在，先删除
            if (imageFile.exists()) {
                imageFile.delete()
            }
            // 创建新文件
            imageFile.createNewFile()
            outputImage = imageFile
        } catch (e: IOException) {
            Toast.makeText(this, "创建文件失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            return
        }

        // 获取图片的Uri
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0及以上使用FileProvider
            try {
                FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    outputImage
                )
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "FileProvider配置错误: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                return
            }
        } else {
            // Android 7.0以下直接使用Uri.fromFile
            Uri.fromFile(outputImage)
        }

        // 启动相机
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        takePhotoLauncher.launch(intent)
    }

    /**
     * 从相册选择图片
     */
    private fun chooseFromAlbum() {
        // 检查存储权限
        val hasPermission: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        if (hasPermission.not()) {
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show()
            checkPermissions()
            return
        }

        // 启动相册选择
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        chooseFromAlbumLauncher.launch(intent)
    }

    /**
     * 显示图片
     */
    private fun displayImage(uri: Uri?) {
        if (uri == null) return

        try {
            // 读取图片
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Android 9.0及以上使用ImageDecoder
                try {
                    val source = android.graphics.ImageDecoder.createSource(contentResolver, uri)
                    android.graphics.ImageDecoder.decodeBitmap(source)
                } catch (e: Exception) {
                    // 如果ImageDecoder失败，回退到BitmapFactory
                    val inputStream = contentResolver.openInputStream(uri)
                    BitmapFactory.decodeStream(inputStream)
                }
            } else {
                // Android 9.0以下使用BitmapFactory
                val inputStream = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            }

            // 显示图片
            bitmap?.let {
                imageView.setImageBitmap(it)
                // 显示路径信息
                tvImagePath.text = "图片路径: $uri"
            } ?: run {
                Toast.makeText(this, "无法读取图片", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "显示图片失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
