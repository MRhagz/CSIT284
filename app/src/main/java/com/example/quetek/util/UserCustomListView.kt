package com.example.quetek.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.quetek.R
import com.example.quetek.app.DataManager

class UserCustomListView(private val context : Context,
                         private val size : TextView,
                         private val userList: MutableList<String>,
                         private val dataManager: DataManager,
                         private val onClick: (String) -> Unit,
                         private val onLongClick: (String) -> Unit,) : BaseAdapter(){
    var isLoading = true
    override fun getCount(): Int = userList.size

    override fun getItem(position: Int): Any = userList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
//        if (isLoading) {
//            return inflater.inflate(R.layout.skeleton_list_item, parent, false)
//        }

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.activity_custom_list, parent, false)

        val username = view.findViewById<TextView>(R.id.username)
        val image = view.findViewById<ImageView>(R.id.image)
        image.setImageResource(R.drawable.user)
//        val data = dataManager.usersHistory
        val user = userList[position]
        username.setText(user)

        view.setOnClickListener {
            onClick(user)
        }

        view.setOnLongClickListener {
            onLongClick(user)
            removeItem(position)
            true
        }
        return view;
    }

    fun removeItem(position: Int) {
        if (position in userList.indices) {
            userList.removeAt(position)
            size.text = if (userList.size < 10) "0${userList.size}" else "${userList.size}"
            notifyDataSetChanged()
        }
    }

}