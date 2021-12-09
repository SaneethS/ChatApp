package com.yml.chatapp.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.yml.chatapp.R
import com.yml.chatapp.ui.authentication.MainActivity
import com.yml.chatapp.databinding.ActivityHomeBinding
import com.yml.chatapp.ui.home.adapter.ViewPagerAdapter
import com.yml.chatapp.ui.profile.ProfileActivity
import com.yml.chatapp.ui.wrapper.User

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private var currentUser: User = User("","","",image = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        homeViewModel = ViewModelProvider(this@HomeActivity)[HomeViewModel::class.java]
        setSupportActionBar(binding.homeActivityToolbar)
        homeViewModel.getUserData(this@HomeActivity)
        allObservers()
        initViewPager()
    }

    private fun initViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,lifecycle)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> {
                    tab.text = "Chats"
                }
                1 -> {
                    tab.text = "Groups"
                }
            }
        }.attach()
    }

    private fun allObservers() {
        homeViewModel.getUserDataStatus.observe(this@HomeActivity) {
            currentUser = it
            Log.i("HomeData", "user = $currentUser")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.profile_button -> {
                val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.logout_button -> {
                homeViewModel.logOut(this@HomeActivity)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("showSplash", false)
                startActivity(intent)
                finish()
            }
        }
        return true
    }
}