package com.example.project00

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class UserListAdapter(context: Context, users: List<User>) :
    ArrayAdapter<User>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_users, parent, false)
        }

        val textUserName: TextView = itemView!!.findViewById(R.id.textUserName)
        val textUserPoints: TextView = itemView.findViewById(R.id.textUserPoints)

        val user = getItem(position)

        textUserName.text = user?.userName


        return itemView
    }
}
