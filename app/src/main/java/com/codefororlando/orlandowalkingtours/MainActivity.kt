package com.codefororlando.orlandowalkingtours

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.codefororlando.orlandowalkingtours.dashboard.DashboardActivity
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())

        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
