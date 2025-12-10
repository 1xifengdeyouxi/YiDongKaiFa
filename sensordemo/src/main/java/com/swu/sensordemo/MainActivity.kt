package com.swu.sensordemo

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener, View.OnClickListener {
    
    private lateinit var sensorManager: SensorManager
    private lateinit var tvAccelerometer: TextView
    private lateinit var tvOrientation: TextView
    private lateinit var tvLight: TextView
    private lateinit var tvProximity: TextView
    private lateinit var tvGyroscope: TextView
    private lateinit var tvMagnetic: TextView
    private lateinit var btnClear: Button
    
    // 传感器对象
    private var accelerometerSensor: Sensor? = null
    private var orientationSensor: Sensor? = null
    private var lightSensor: Sensor? = null
    private var proximitySensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null
    private var magneticSensor: Sensor? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        initSensors()
        registerSensors()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        tvAccelerometer = findViewById(R.id.tv_accelerometer)
        tvOrientation = findViewById(R.id.tv_orientation)
        tvLight = findViewById(R.id.tv_light)
        tvProximity = findViewById(R.id.tv_proximity)
        tvGyroscope = findViewById(R.id.tv_gyroscope)
        tvMagnetic = findViewById(R.id.tv_magnetic)
        btnClear = findViewById(R.id.btn_clear)
        
        btnClear.setOnClickListener(this)
    }
    
    /**
     * 初始化传感器管理器
     */
    private fun initSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        
        // 获取各种传感器
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        
        // 检查传感器是否可用
        checkSensorAvailability()
    }
    
    /**
     * 检查传感器可用性
     */
    private fun checkSensorAvailability() {
        val unavailableSensors = mutableListOf<String>()
        
        if (accelerometerSensor == null) unavailableSensors.add("加速度传感器")
        if (orientationSensor == null) unavailableSensors.add("方向传感器")
        if (lightSensor == null) unavailableSensors.add("光线传感器")
        if (proximitySensor == null) unavailableSensors.add("距离传感器")
        if (gyroscopeSensor == null) unavailableSensors.add("陀螺仪传感器")
        if (magneticSensor == null) unavailableSensors.add("磁力传感器")
        
        if (unavailableSensors.isNotEmpty()) {
            Toast.makeText(
                this,
                "以下传感器不可用: ${unavailableSensors.joinToString(", ")}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    /**
     * 注册传感器监听器
     */
    private fun registerSensors() {
        // 注册加速度传感器，使用正常延迟
        accelerometerSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        // 注册方向传感器
        orientationSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        // 注册光线传感器
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        // 注册距离传感器
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        // 注册陀螺仪传感器
        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        // 注册磁力传感器
        magneticSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    
    /**
     * 取消注册传感器监听器
     */
    private fun unregisterSensors() {
        sensorManager.unregisterListener(this)
    }
    
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_clear -> {
                clearAllData()
            }
        }
    }
    
    /**
     * 清空所有数据显示
     */
    private fun clearAllData() {
        tvAccelerometer.text = "X: 0.0\nY: 0.0\nZ: 0.0"
        tvOrientation.text = "方位角: 0.0\n倾斜角: 0.0\n滚动角: 0.0"
        tvLight.text = "光线强度: 0.0"
        tvProximity.text = "距离: 0.0"
        tvGyroscope.text = "X: 0.0\nY: 0.0\nZ: 0.0"
        tvMagnetic.text = "X: 0.0\nY: 0.0\nZ: 0.0"
        Toast.makeText(this, "数据已清空", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 传感器数据变化回调
     */
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    // 加速度传感器 (m/s²)
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    tvAccelerometer.text = "X: ${String.format("%.2f", x)}\nY: ${String.format("%.2f", y)}\nZ: ${String.format("%.2f", z)}"
                }
                
                Sensor.TYPE_ORIENTATION -> {
                    // 方向传感器 (度)
                    val azimuth = it.values[0]  // 方位角
                    val pitch = it.values[1]    // 倾斜角
                    val roll = it.values[2]     // 滚动角
                    tvOrientation.text = "方位角: ${String.format("%.2f", azimuth)}\n倾斜角: ${String.format("%.2f", pitch)}\n滚动角: ${String.format("%.2f", roll)}"
                }
                
                Sensor.TYPE_LIGHT -> {
                    // 光线传感器 (lux)
                    val light = it.values[0]
                    tvLight.text = "光线强度: ${String.format("%.2f", light)}"
                }
                
                Sensor.TYPE_PROXIMITY -> {
                    // 距离传感器 (cm)
                    val distance = it.values[0]
                    tvProximity.text = "距离: ${String.format("%.2f", distance)}"
                }
                
                Sensor.TYPE_GYROSCOPE -> {
                    // 陀螺仪传感器 (rad/s)
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    tvGyroscope.text = "X: ${String.format("%.4f", x)}\nY: ${String.format("%.4f", y)}\nZ: ${String.format("%.4f", z)}"
                }
                
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    // 磁力传感器 (μT)
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    tvMagnetic.text = "X: ${String.format("%.2f", x)}\nY: ${String.format("%.2f", y)}\nZ: ${String.format("%.2f", z)}"
                }
            }
        }
    }
    
    /**
     * 传感器精度变化回调
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val accuracyText = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "高精度"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "中精度"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "低精度"
            SensorManager.SENSOR_STATUS_UNRELIABLE -> "不可靠"
            else -> "未知"
        }
        
        // 可以在这里处理精度变化，这里只是示例
        // Toast.makeText(this, "${sensor?.name} 精度: $accuracyText", Toast.LENGTH_SHORT).show()
    }
    
    override fun onPause() {
        super.onPause()
        // 暂停时取消注册传感器，节省电量
        unregisterSensors()
    }
    
    override fun onResume() {
        super.onResume()
        // 恢复时重新注册传感器
        registerSensors()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 销毁时取消注册传感器
        unregisterSensors()
    }
}
