package org.nahap.library.catalog.ui

import android. os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import org.nahap.library.R
import org.nahap.library.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(LibraryFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_library -> {
                    loadFragment(LibraryFragment())
                    true
                }
                R.id. nav_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R. id.fragmentContainer, fragment)
            .commit()
    }
}