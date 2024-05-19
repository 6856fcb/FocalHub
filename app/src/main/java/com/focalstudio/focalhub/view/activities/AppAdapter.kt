package com.focalstudio.focalhub.view.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.focalstudio.focalhub.R

class AppListAdapter(
    private val appList: List<Pair<String, String>>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (appName, _) = appList[position]
        holder.appNameTextView.text = appName
        holder.itemView.setOnClickListener {
            onItemClick(appList[position].second)
        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appNameTextView: TextView = itemView.findViewById(R.id.appNameTextView)
    }
}
