package com.swu.recycleviewdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class FruitAdapter(private val mFruitList: List<Fruit>) :
    RecyclerView.Adapter<FruitAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fruit_item_1, parent, false)
        val holder = ViewHolder(view)

        // 名称单击事件
        holder.fruitName.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val fruit = mFruitList[position]
                Toast.makeText(view.context, "您点击的布局为：${fruit.name}", Toast.LENGTH_LONG).show()
            }
        }

        // 图片单击事件
        holder.fruitImage.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val fruit = mFruitList[position]
                Toast.makeText(view.context, "您点击的图片为：${fruit.name}", Toast.LENGTH_SHORT).show()
            }
        }

        // 价格单击事件（补充）
        holder.fruitPrice.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val fruit = mFruitList[position]
                Toast.makeText(view.context, "您点击的价格为：${fruit.price}", Toast.LENGTH_SHORT).show()
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fruit = mFruitList[position]
        holder.fruitImage.setImageResource(fruit.imageID)
        holder.fruitName.text = fruit.name
        holder.fruitPrice.text = fruit.price
    }

    override fun getItemCount(): Int = mFruitList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fruitImage: ImageView = view.findViewById(R.id.fruit_image)
        val fruitName: TextView = view.findViewById(R.id.fruit_name)
        val fruitPrice: TextView = view.findViewById(R.id.fruit_price)
    }
}
