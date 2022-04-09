package com.example.testproject

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.testproject.databinding.ActivityMainBinding
import com.example.testproject.game.fragments.utils.Constants
import com.example.testproject.game.fragments.utils.Constants.APP
import com.example.testproject.game.fragments.utils.Variables.engorru
import com.example.testproject.game.fragments.utils.Variables.exit
import com.example.testproject.game.fragments.utils.Variables.retry


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        engorru = false
        exit = false
        retry = false
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        APP = this
        navController = Navigation.findNavController(this, R.id.nav_fragment)
        binding.bottomNavigation.setupWithNavController(navController)


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.e("TAG", "item")
        binding.bottomNavigation.setupWithNavController(navController)
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item)
    }

}