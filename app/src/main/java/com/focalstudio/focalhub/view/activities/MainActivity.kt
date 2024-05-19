package com.focalstudio.focalhub.view.activities

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.focalstudio.focalhub.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val packageManager = packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { packageManager.getLaunchIntentForPackage(it.packageName) != null }
            .map { Pair(packageManager.getApplicationLabel(it).toString(), it.packageName) }

        recyclerView.adapter = AppListAdapter(apps) { packageName ->
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            launchIntent?.let { startActivity(it) }
        }
    }
}
