package com.codefororlando.orlandowalkingtours.dashboard

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.codefororlando.orlandowalkingtours.PlaceholderFragment
import com.codefororlando.orlandowalkingtours.R

class DashboardActivity : AppCompatActivity() {


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_browse -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.dashboard_fragmentContainer, BrowseFragment())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            else -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.dashboard_fragmentContainer, PlaceholderFragment())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportFragmentManager.beginTransaction()
                .add(R.id.dashboard_fragmentContainer, BrowseFragment())
                .commit()

        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

}
