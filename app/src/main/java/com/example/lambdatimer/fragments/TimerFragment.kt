package com.example.lambdatimer.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lambdatimer.MViewModel
import com.example.lambdatimer.R
import com.example.lambdatimer.SavedTimersEntity
import com.example.lambdatimer.TimerActivity
import com.example.lambdatimer.databinding.FragmentTimerBinding
import java.text.SimpleDateFormat
import java.util.*

class TimerFragment : Fragment() {
    private lateinit var b: FragmentTimerBinding
    private lateinit var get: SharedPreferences
    private lateinit var set: SharedPreferences.Editor
    private var valuesM: Array<String> = arrayOf("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55",
        "60", "65", "70", "75", "80", "85", "90", "95", "100", "105", "110", "115", "120")
    private var valuesS: Array<String> = arrayOf("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")

    override fun onPause() {
        super.onPause()
        b.mainTxt.alpha = 0f
    }
    override fun onResume() {
        super.onResume()
        b.save.visibility = View.VISIBLE
        b.mainTxt.startAnimation(AnimationUtils.loadAnimation(context, R.anim.seek))
        b.mainTxt.alpha = 1f
    }
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        b = FragmentTimerBinding.inflate(inflater, container, false)
        get = this.requireActivity().getSharedPreferences("value", Context.MODE_PRIVATE)
        set = get.edit()
        val toast = Toast.makeText(this.requireActivity(), R.string.not_choose, Toast.LENGTH_LONG)
        b.apply {
            if (bbPickerMinute.value == 0 && bbPickerSecond.value == 0) {
                afterPicker.value = 0
                afterPicker.isEnabled = false
                afterTxt.isEnabled = false
            }
            val time = SimpleDateFormat("HH", Locale.getDefault()).format(Date())
            val text = if (time.toInt() in 5..11) getString(R.string.good_m)
            else if (time.toInt() in 12..16) getString(R.string.good_a)
            else if (time.toInt() in 17..23) getString(R.string.good_e)
            else getString(R.string.good_n)
            mainTxt.text = text + getString(R.string.main_txt)

            workPickerMinute.setSettings(true)
            workPickerSecond.setSettings(false)
            breakPickerMinute.setSettings(true)
            breakPickerSecond.setSettings(false)
            bbPickerMinute.setSettings(true)
            bbPickerSecond.setSettings(false)

            afterPicker.minValue = 0
            afterPicker.maxValue = 19

            workPickerMinute.value = get.getInt("workPickerMinute", 9)/5
            workPickerSecond.value = get.getInt("workPickerSecond", 0)/5
            breakPickerMinute.value = get.getInt("breakPickerMinute", 3)/5
            breakPickerSecond.value = get.getInt("breakPickerSecond", 0)/5

            afterPicker.value = get.getInt("afterPicker", 0)
            if (afterPicker.value != 0) {
                bbPickerMinute.value = get.getInt("bbPickerMinute", 0)/5
                bbPickerSecond.value = get.getInt("bbPickerMinute", 0)/5
            }

            bbPickerMinute.setOnValueChangedListener { _, _, num -> newNum(num) }
            bbPickerSecond.setOnValueChangedListener { _, _, num -> newNum(num) }

            start.setOnClickListener {
                if ((workPickerMinute.value == 0 && workPickerSecond.value == 0)
                    || (breakPickerMinute.value == 0 && breakPickerSecond.value == 0)
                    || (afterPicker.isEnabled && afterPicker.value == 0)) {
                    toast.show()
                    return@setOnClickListener
                }; toast.cancel()
                save.visibility = View.GONE
                set.run {
                    putInt("workPickerMinute", workPickerMinute.value*5)
                    putInt("workPickerSecond", workPickerSecond.value*5)
                    putInt("breakPickerMinute", breakPickerMinute.value*5)
                    putInt("breakPickerSecond", breakPickerSecond.value*5)
                    putInt("bbPickerMinute", bbPickerMinute.value*5)
                    putInt("bbPickerSecond", bbPickerSecond.value*5)
                    putInt("afterPicker", afterPicker.value)
                    apply()
                }
                mainTxt.startAnimation(AnimationUtils.loadAnimation(context, R.anim.hide))
                startActivity(Intent(context, TimerActivity::class.java))
            }
            save.setOnClickListener {
                if ((workPickerMinute.value == 0 && workPickerSecond.value == 0)
                    || (breakPickerMinute.value == 0 && breakPickerSecond.value == 0)
                    || (afterPicker.isEnabled && afterPicker.value == 0)) {
                    toast.show()
                    return@setOnClickListener
                }; toast.cancel()
                val model = ViewModelProvider(this@TimerFragment.requireActivity())[MViewModel::class.java]
                val v = layoutInflater.inflate(R.layout.save_timer, null)
                val s = AlertDialog.Builder(this@TimerFragment.requireActivity())
                s.setView(v)
                s.setTitle(R.string.enter_tag)
                s.setPositiveButton(getString(R.string.save)) { _, _ ->
                    val tag = v.findViewById<EditText>(R.id.tag)
                    if (tag.text.isNullOrEmpty()) tag.setText("Saved Timer")
                    model.add(
                        SavedTimersEntity(
                            tag.text.toString(),
                            workPickerMinute.value*5,
                            workPickerSecond.value*5,
                            breakPickerMinute.value*5,
                            breakPickerSecond.value*5,
                            bbPickerMinute.value*5,
                            bbPickerSecond.value*5,
                            afterPicker.value,
                        )
                    )
                }
                s.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                s.show()
            }
        }; return b.root
    }

    private fun newNum(num: Int) {
        b.apply {
            if (num == 0 && bbPickerSecond.value == 0 && bbPickerMinute.value == 0) {
                afterPicker.isEnabled = false
                afterTxt.isEnabled = false
                afterPicker.value = 0
            } else {
                afterPicker.isEnabled = true
                afterTxt.isEnabled = true
            }
        }
    }

    private fun android.widget.NumberPicker.setSettings(thisMinutes: Boolean) {
        if (thisMinutes) {
            this.minValue = 0
            this.maxValue = valuesM.size - 1
            this.displayedValues = valuesM
        } else {
            this.minValue = 0
            this.maxValue = valuesS.size - 1
            this.displayedValues = valuesS
        }
    }

}