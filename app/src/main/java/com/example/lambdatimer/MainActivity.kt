package com.example.lambdatimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.lambdatimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        b.viewPager.adapter = ViewPageAdapter(this)

        b.bottomNav.selectedItemId = R.id.ic_timer
        b.viewPager.currentItem = 2

        b.bottomNav.setOnItemSelectedListener { id ->
            when (id.itemId) {
                R.id.ic_stats -> b.viewPager.currentItem = 0
                R.id.ic_custom -> b.viewPager.currentItem = 1
                R.id.ic_timer -> b.viewPager.currentItem = 2
                R.id.ic_saved -> b.viewPager.currentItem = 3
                R.id.ic_settings -> b.viewPager.currentItem = 4
            }
            return@setOnItemSelectedListener false
        }
        b.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> b.bottomNav.menu.findItem(R.id.ic_stats).isChecked = true
                    1 -> b.bottomNav.menu.findItem(R.id.ic_custom).isChecked = true
                    2 -> b.bottomNav.menu.findItem(R.id.ic_timer).isChecked = true
                    3 -> b.bottomNav.menu.findItem(R.id.ic_saved).isChecked = true
                    4 -> b.bottomNav.menu.findItem(R.id.ic_settings).isChecked = true
                }
                super.onPageSelected(position)
            }
        })
        setContentView(b.root)
    }
}
