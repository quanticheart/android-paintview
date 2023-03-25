package com.quanticheart.paint

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.quanticheart.paint.databinding.ItemColorBinding

// Created by Jonn Alves on 24/03/23.
//
class ColorAdapter(
    mContext: Context,
    algorithmList: ArrayList<ColorItem>
) : ArrayAdapter<ColorItem>(mContext, 0, algorithmList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position)
    }

    private fun initView(position: Int): View {
        val view = ItemColorBinding.inflate(LayoutInflater.from(context))
        val currentItem = getItem(position)
        view.textView.text = currentItem?.name
        view.imageView.setBackgroundColor(currentItem?.color ?: 0)
        return view.root
    }
}