package com.swu.recycleviewdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)

        val fruitList = ArrayList<Fruit>().apply {
            repeat(2) {
                add(Fruit(R.drawable.pineapple_pic, "菠萝", "¥16.9元/kg"))
                add(Fruit(R.drawable.mango_pic, "芒果", "¥29.9 元/kg"))
                add(Fruit(R.drawable.strawberry_pic, "草莓", "¥15元/kg"))
                add(Fruit(R.drawable.grape_pic, "葡萄", "¥19.9 元/kg"))
                add(Fruit(R.drawable.apple_pic, "苹果", "¥20 元/kg"))
                add(Fruit(R.drawable.orange_pic, "橙子", "¥18.8 元/kg"))
                add(Fruit(R.drawable.watermelon_pic, "西瓜", "¥28.8元/kg"))
            }
        }

        val adapter = FruitAdapter(fruitList)

        // 普通垂直列表
//        val layoutManager = LinearLayoutManager(this)
//        recyclerView.layoutManager = layoutManager
//        recyclerView.adapter = adapter

        // 若需瀑布流（StaggeredGrid），替换为：
        val staggeredLayoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = staggeredLayoutManager
        recyclerView.adapter = adapter
    }
}
