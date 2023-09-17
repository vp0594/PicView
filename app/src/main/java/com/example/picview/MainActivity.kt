package com.example.picview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.picview.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)

        setUpTabLayout()
    }

    private fun setUpTabLayout() {
        binding.homePageViewPage.adapter = TabLayoutAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.homePageTabLayout, binding.homePageViewPage) { tab, position ->
            when (position) {
                0 -> tab.text = "All Photos"
                1 -> tab.text = "Albums"
            }

        }.attach()
    }
}
