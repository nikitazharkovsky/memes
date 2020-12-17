package com.zharkovsky.memes.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zharkovsky.memes.R

class MainActivity : AppCompatActivity() {
    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initViewProperties()
    }

    private fun initView() {
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
    }

    private fun initViewProperties() {
        navView.itemIconTintList = null
        navView.setupWithNavController(navController)
    }
}