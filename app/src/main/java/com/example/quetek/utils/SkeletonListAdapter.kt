//package com.example.quetek.util
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseAdapter
//import com.example.quetek.R
//
//class ShimmerListAdapter(private val context: Context) : BaseAdapter() {
//    private val itemCount = 5 // Number of skeleton rows
//    override fun getCount(): Int = itemCount
//    override fun getItem(position: Int): Any = position
//    override fun getItemId(position: Int): Long = position.toLong()
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        return LayoutInflater.from(context).inflate(R.layout.skeleton_list_item, parent, false)
//    }
//}
