package com.swu.mpandroidcharttest

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class MainActivity : AppCompatActivity() {
    
    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupLineChart()
        setupBarChart()
        setupPieChart()
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews() {
        lineChart = findViewById(R.id.line_chart)
        barChart = findViewById(R.id.bar_chart)
        pieChart = findViewById(R.id.pie_chart)
    }
    
    /**
     * 设置折线图
     */
    private fun setupLineChart() {
        // 创建数据点
        val entries = mutableListOf<Entry>()
        entries.add(Entry(1f, 20f))
        entries.add(Entry(2f, 30f))
        entries.add(Entry(3f, 25f))
        entries.add(Entry(4f, 40f))
        entries.add(Entry(5f, 35f))
        entries.add(Entry(6f, 50f))
        entries.add(Entry(7f, 45f))
        
        val dataSet = LineDataSet(entries, "温度变化")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.setCircleColor(Color.BLUE)
        dataSet.circleRadius = 5f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 10f
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // 平滑曲线
        
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        
        // 配置图表
        lineChart.description.isEnabled = true
        lineChart.description.text = "一周温度变化"
        lineChart.setTouchEnabled(true)
        lineChart.setDragEnabled(true)
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)
        
        // 配置X轴
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.textSize = 10f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "第${value.toInt()}天"
            }
        }
        
        // 配置Y轴
        val leftAxis = lineChart.axisLeft
        leftAxis.textColor = Color.BLACK
        leftAxis.axisMinimum = 0f
        
        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false
        
        // 配置图例
        lineChart.legend.isEnabled = true
        lineChart.legend.textColor = Color.BLACK
        
        // 刷新图表
        lineChart.invalidate()
    }
    
    /**
     * 设置柱状图
     */
    private fun setupBarChart() {
        // 创建数据点
        val entries = mutableListOf<BarEntry>()
        entries.add(BarEntry(1f, 100f))
        entries.add(BarEntry(2f, 150f))
        entries.add(BarEntry(3f, 120f))
        entries.add(BarEntry(4f, 180f))
        entries.add(BarEntry(5f, 200f))
        entries.add(BarEntry(6f, 160f))
        
        val dataSet = BarDataSet(entries, "销售额")
        dataSet.color = Color.GREEN
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f
        
        val barData = BarData(dataSet)
        barData.barWidth = 0.5f // 设置柱状图宽度
        barChart.data = barData
        
        // 配置图表
        barChart.description.isEnabled = true
        barChart.description.text = "月度销售额"
        barChart.setTouchEnabled(true)
        barChart.setDragEnabled(true)
        barChart.setScaleEnabled(true)
        barChart.setPinchZoom(true)
        
        // 配置X轴
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.textSize = 10f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    1 -> "1月"
                    2 -> "2月"
                    3 -> "3月"
                    4 -> "4月"
                    5 -> "5月"
                    6 -> "6月"
                    else -> ""
                }
            }
        }
        
        // 配置Y轴
        val leftAxis = barChart.axisLeft
        leftAxis.textColor = Color.BLACK
        leftAxis.axisMinimum = 0f
        
        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false
        
        // 配置图例
        barChart.legend.isEnabled = true
        barChart.legend.textColor = Color.BLACK
        
        // 刷新图表
        barChart.invalidate()
    }
    
    /**
     * 设置饼图
     */
    private fun setupPieChart() {
        // 创建数据点
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(30f, "Android"))
        entries.add(PieEntry(25f, "iOS"))
        entries.add(PieEntry(20f, "Web"))
        entries.add(PieEntry(15f, "其他"))
        entries.add(PieEntry(10f, "桌面"))
        
        val dataSet = PieDataSet(entries, "平台分布")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 12f
        
        val pieData = PieData(dataSet)
        pieChart.data = pieData
        
        // 配置图表
        pieChart.description.isEnabled = true
        pieChart.description.text = "移动平台使用分布"
        pieChart.setUsePercentValues(true) // 显示百分比
        pieChart.setDrawHoleEnabled(true) // 显示中心圆
        pieChart.holeRadius = 40f // 中心圆半径
        pieChart.transparentCircleRadius = 45f // 透明圆半径
        pieChart.setHoleColor(Color.WHITE)
        
        // 配置图例
        pieChart.legend.isEnabled = true
        pieChart.legend.textColor = Color.BLACK
        
        // 配置标签
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
        
        // 刷新图表
        pieChart.invalidate()
    }
}
