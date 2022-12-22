package com.reselling.visionary.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.reselling.visionary.R
import com.reselling.visionary.databinding.ActivityMainBinding
import com.reselling.visionary.utils.changeStatusBarColor
import com.reselling.visionary.utils.visiblity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigation)
        navController = findNavController(R.id.nav_host_fragment)

        bottomNavigation.setupWithNavController(navController)

        val noBottomNavigationFragmentsList = arrayListOf(
            R.id.mapsFragment,
            R.id.gpsFragment,
            R.id.manualLocation
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (noBottomNavigationFragmentsList.contains(destination.id)) {
                binding.bottomNavigation.visiblity(false)
            } else {
                binding.bottomNavigation.visiblity(true)
                this.changeStatusBarColor()
            }


        }

    }


}