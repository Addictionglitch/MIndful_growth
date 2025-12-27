package com.example.mindfulgrowth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.mindfulgrowth.service.ScreenStateService


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This loads the XML layout which contains the Navigation Host and Bottom Bar
        setContentView(R.layout.activity_main)

        // 1. Find the Navigation Controller (The container that swaps the fragments)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 2. Find the Bottom Navigation Bar
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // 3. Connect them!
        // This automatically handles highlighting the correct tab and switching screens
        bottomNav.setupWithNavController(navController)
    }
}