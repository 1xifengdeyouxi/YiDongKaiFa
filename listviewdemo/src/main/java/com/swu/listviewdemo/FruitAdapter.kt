package com.swu.listviewdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class FruitAdapter(
    context: Context,
    private val resourceId: Int,
    objects: List<Fruit>
) : ArrayAdapter<Fruit>(context, resourceId, objects) {

    inner class ViewHolder(
        val fruitImage: ImageView,
        val fruitName: TextView
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val fruit = getItem(position)
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resourceId, parent, false)
            val fruitImage = view.findViewById<ImageView>(R.id.fruit_image)
            val fruitName = view.findViewById<TextView>(R.id.fruit_name)
            viewHolder = ViewHolder(fruitImage, fruitName)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        fruit?.let {
            viewHolder.fruitImage.setImageResource(it.imageId)
            viewHolder.fruitName.text = it.name
        }
        return view
    }
}