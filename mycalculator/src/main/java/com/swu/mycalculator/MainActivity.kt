package com.swu.mycalculator

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var tv_result_calculator: TextView

    // 第一个操作数
    private var firstNum: String = ""

    // 运算符
    private var operator: String = ""

    // 第二个操作数
    private var secondNum: String = ""

    // 当前计算结果
    private var result: String = ""

    // 显示的文本内容
    private var showText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 布局文件中获取 tv_result_calculator 文本视图
        tv_result_calculator = findViewById(R.id.tv_result_calculator)

        // 为每个按钮注册点击监听器
        findViewById<View>(R.id.btn_cancel).setOnClickListener(this)
        findViewById<View>(R.id.btn_divide).setOnClickListener(this)
        findViewById<View>(R.id.btn_multiply).setOnClickListener(this)
        findViewById<View>(R.id.btn_clear).setOnClickListener(this)
        findViewById<View>(R.id.btn_seven).setOnClickListener(this)
        findViewById<View>(R.id.btn_eight).setOnClickListener(this)
        findViewById<View>(R.id.btn_nine).setOnClickListener(this)
        findViewById<View>(R.id.btn_add).setOnClickListener(this)
        findViewById<View>(R.id.btn_four).setOnClickListener(this)
        findViewById<View>(R.id.btn_five).setOnClickListener(this)
        findViewById<View>(R.id.btn_six).setOnClickListener(this)
        findViewById<View>(R.id.btn_minus).setOnClickListener(this)
        findViewById<View>(R.id.btn_one).setOnClickListener(this)
        findViewById<View>(R.id.btn_two).setOnClickListener(this)
        findViewById<View>(R.id.btn_three).setOnClickListener(this)
        findViewById<View>(R.id.btn_reciprocal).setOnClickListener(this)
        findViewById<View>(R.id.btn_zero).setOnClickListener(this)
        findViewById<View>(R.id.btn_dot).setOnClickListener(this)
        findViewById<View>(R.id.btn_equal).setOnClickListener(this)
        findViewById<View>(R.id.btn_sqrt).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val inputText: String = if (v.id == R.id.btn_sqrt) {
            "√"
        } else {
            // 其他按钮：把 View 当作 TextView 处理以读取文本
            (v as TextView).text.toString()
        }

        when (v.id) {
            // 清除按钮
            R.id.btn_clear -> {
                clear()
                refreshText("0")
            }

            // 取消按钮（退格）
            R.id.btn_cancel -> {
                if (operator.isEmpty()) {
                    if (firstNum.length == 1) {
                        firstNum = "0"
                    } else if (firstNum.length > 1) {
                        firstNum = firstNum.substring(0, firstNum.length - 1)
                    } else {
                        Toast.makeText(this, "0", Toast.LENGTH_SHORT).show()
                    }
                    showText = firstNum
                    refreshText(showText)
                } else {
                    if (secondNum.length == 1) {
                        secondNum = ""
                    } else if (secondNum.length > 1) {
                        secondNum = secondNum.substring(0, secondNum.length - 1)
                    } else if (secondNum.length == 0) {
                        Toast.makeText(this, "0", Toast.LENGTH_SHORT).show()
                    }
                    if (showText.isNotEmpty()) {
                        showText = showText.substring(0, showText.length - 1)
                    }
                    refreshText(showText)
                }
            }

            // 运算符：加 减 乘 除
            R.id.btn_add, R.id.btn_minus, R.id.btn_multiply, R.id.btn_divide -> {
                operator = inputText
                refreshText(showText + operator)
            }

            // 等号
            R.id.btn_equal -> {
                val calculate_result = calculateFour()
                refreshOperate(calculate_result.toString())
                refreshText(showText + "=" + result)
            }

            // 开根号
            R.id.btn_sqrt -> {
                val sqrt_result = sqrt(firstNum.toDouble())
                refreshOperate(sqrt_result.toString())
                refreshText(showText + "√=" + result)
            }

            // 求倒数
            R.id.btn_reciprocal -> {
                val reciprocal = 1.0 / firstNum.toDouble()
                refreshOperate(reciprocal.toString())
                refreshText(showText + "/=" + result)
            }

            // 其他（数字、小数点）
            else -> {
                if (result.isNotEmpty() && operator.isEmpty()) {
                    clear()
                }
                if (operator.isEmpty()) {
                    // 无运算符时追加到第一个操作数
                    firstNum = firstNum + inputText
                } else {
                    // 有运算符时追加到第二个操作数
                    secondNum = secondNum + inputText
                }

                // 整数不需要前导 0 和 .
                if (showText == "0" && inputText == ".") {
                    refreshText(inputText)
                } else {
                    refreshText(showText + inputText)
                }
            }
        }
    }

    // 四则运算，返回结果
    private fun calculateFour(): Double {
        return when (operator) {
            "+" -> firstNum.toDouble() + secondNum.toDouble()
            "-" -> firstNum.toDouble() - secondNum.toDouble()
            "×" -> firstNum.toDouble() * secondNum.toDouble()
            else -> firstNum.toDouble() / secondNum.toDouble()
        }
    }

    // 清空并初始化
    private fun clear() {
        refreshOperate("")
        refreshText("")
    }

    // 刷新运算结果
    private fun refreshOperate(new_result: String) {
        result = new_result
        // 连续计算：下一次的结果作为第一个操作数
        firstNum = result
        secondNum = ""
        operator = ""
    }

    // 刷新显示文本
    private fun refreshText(text: String) {
        showText = text
        tv_result_calculator.text = showText
    }
}
