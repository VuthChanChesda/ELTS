package com.elts

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.elts.databinding.ActivityHomeScreenBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController


class HomeSCreenActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.topAppBar)

        // Set up drawer toggle
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.topAppBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.white)

        // Open drawer when navigation icon is clicked
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle navigation item clicks
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // TODO: Show home content
                }
                R.id.nav_profile -> {
                    // TODO: Show profile content
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navController = this.findNavController(R.id.fragmentContainer)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)


    }
}