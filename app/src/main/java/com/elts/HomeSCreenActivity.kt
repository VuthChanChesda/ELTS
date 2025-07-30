package com.elts

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.elts.databinding.ActivityHomeScreenBinding


class HomeSCreenActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_explore -> {
                    // Handle Home tab click
                    // e.g., switch fragment or start activity
                    Toast.makeText(this, "Explore", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_prepare -> {
                    Toast.makeText(this, "Prepare", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_results -> {
                    Toast.makeText(this, "Results", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }



    }
}