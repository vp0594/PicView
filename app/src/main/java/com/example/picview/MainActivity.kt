package com.example.picview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var homePageTabLayout: TabLayout
    private lateinit var homePageViewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homePageTabLayout = findViewById(R.id.homePageTabLayout)
        homePageViewPager = findViewById(R.id.homePageViewPage)

        homePageViewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(homePageTabLayout, homePageViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "All Photos"
                1 -> tab.text = "Albums"
            }

        }.attach()
    }
}