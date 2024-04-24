package com.example.lambdatimer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.lambdatimer.fragments.CustomFragment
import com.example.lambdatimer.fragments.TimerFragment
import com.example.lambdatimer.fragments.SavedFragment
import com.example.lambdatimer.fragments.SettingsFragment
import com.example.lambdatimer.fragments.StatisticsFragment

class ViewPageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StatisticsFragment()
            1 -> CustomFragment()
            3 -> SavedFragment()
            4 -> SettingsFragment()
            else -> TimerFragment()
        }
    }
    override fun getItemCount(): Int {
        return 5
    }
}