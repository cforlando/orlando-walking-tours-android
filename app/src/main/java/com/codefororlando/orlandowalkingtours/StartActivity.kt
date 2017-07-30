package com.codefororlando.orlandowalkingtours

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.codefororlando.orlandowalkingtours.dashboard.DashboardActivity

class StartActivity : AppCompatActivity() {

    lateinit var getStartedButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        getStartedButton = findViewById(R.id.start_getStartedButton) as Button

        getStartedButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}
