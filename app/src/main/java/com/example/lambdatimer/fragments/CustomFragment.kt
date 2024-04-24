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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lambdatimer.R
import com.example.lambdatimer.TimerActivity
import com.example.lambdatimer.databinding.FragmentCustomBinding
import com.example.lambdatimer.MViewModel
import com.example.lambdatimer.SavedTimersEntity
import java.text.SimpleDateFormat
import java.util.*

class CustomFragment : Fragment() {
    private lateinit var b: FragmentCustomBinding
    private lateinit var get: SharedPreferences
    private lateinit var set: SharedPreferences.Editor

    override fun onPause() {
        super.onPause()
        b.mainTxt.alpha = 0f
    }
    override fun onResume() {
        super.onResume()
        b.save.visibility = View.VISIBLE
        b.mainTxt.startAnimation(AnimationUtils.loadAnimation(context, R.anim.seek))
        b.mainTxt.alpha = 1f
        Toast.makeText(this.requireActivity(), R.string.warning_toast, Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentCustomBinding.inflate(inflater, container, false)
        get = this.requireActivity().getSharedPreferences("value", Context.MODE_PRIVATE)
        set = get.edit()
        val toast = Toast.makeText(this.requireActivity(), R.string.error_toast, Toast.LENGTH_LONG)
        b.apply {
            val time = SimpleDateFormat("HH", Locale.getDefault()).format(Date())
            val text = if (time.toInt() in 5..11) getString(R.string.productive_m)
            else if (time.toInt() in 12..16) getString(R.string.productive_a)
            else if (time.toInt() in 17..23) getString(R.string.productive_e)
            else getString(R.string.productive_n)
            mainTxt.append(text)

            workEdit.addTextChangedListener { check(workEdit) }
            breakEdit.addTextChangedListener { check(breakEdit) }
            bbEdit.addTextChangedListener { check(bbEdit)
                afterEdit.isEnabled = !bbEdit.text.isNullOrEmpty()
                afterTxt.isEnabled = !bbEdit.text.isNullOrEmpty()
                if (!bbEdit.text.isNullOrEmpty()) {
                    afterEdit.error = if (afterEdit.text.isNullOrEmpty()) getString(R.string.error_n)
                    else if (afterEdit.value() == 0) getString(R.string.error_n)
                    else null
                } else afterEdit.error = null; afterEdit.text = null
            }
            afterEdit.addTextChangedListener {
                if (!bbEdit.text.isNullOrEmpty()) {
                    afterEdit.error = if (afterEdit.text.isNullOrEmpty()) getString(R.string.error_n)
                    else if (afterEdit.value() == 0) getString(R.string.error_n)
                    else null
                }
            }
            start.setOnClickListener {
                if (workEdit.error != null || breakEdit.error != null || bbEdit.error != null || afterEdit.error != null) {
                    toast.show()
                    return@setOnClickListener
                }; toast.cancel()
                save.visibility = View.GONE
                set.run {
                    putInt("workPickerMinute", if (workEdit.text.isNullOrEmpty()) 45 else workEdit.getMinute())
                    putInt("workPickerSecond", if (workEdit.text.isNullOrEmpty()) 0 else workEdit.getSecond())
                    putInt("breakPickerMinute", if (breakEdit.text.isNullOrEmpty()) 15 else breakEdit.getMinute())
                    putInt("breakPickerSecond", if (breakEdit.text.isNullOrEmpty()) 0 else breakEdit.getSecond())
                    putInt("bbPickerMinute", if (bbEdit.text.isNullOrEmpty()) 0 else bbEdit.getMinute())
                    putInt("bbPickerSecond", if (bbEdit.text.isNullOrEmpty()) 0 else bbEdit.getSecond())
                    putInt("afterPicker", if (afterEdit.text.isNullOrEmpty()) 0 else afterEdit.value())
                    apply()
                }
                mainTxt.startAnimation(AnimationUtils.loadAnimation(context, R.anim.hide))
                startActivity(Intent(context, TimerActivity::class.java))
            }
            save.setOnClickListener {
                if (workEdit.error != null || breakEdit.error != null || bbEdit.error != null || afterEdit.error != null) {
                    toast.show()
                    return@setOnClickListener
                }; toast.cancel()
                val model = ViewModelProvider(this@CustomFragment.requireActivity())[MViewModel::class.java]
                val v = layoutInflater.inflate(R.layout.save_timer, null)
                val s = AlertDialog.Builder(this@CustomFragment.requireActivity())
                s.setView(v)
                s.setTitle(R.string.enter_tag)
                s.setPositiveButton(getString(R.string.save)) { _, _ ->
                    val tag = v.findViewById<EditText>(R.id.tag)
                    if (tag.text.isNullOrEmpty()) tag.setText("Saved Timer")
                    model.add(
                        SavedTimersEntity(
                        tag.text.toString(),
                        if (workEdit.text.isNullOrEmpty()) 45 else workEdit.getMinute(),
                        if (workEdit.text.isNullOrEmpty()) 0 else workEdit.getSecond(),
                        if (breakEdit.text.isNullOrEmpty()) 15 else breakEdit.getMinute(),
                        if (breakEdit.text.isNullOrEmpty()) 0 else breakEdit.getSecond(),
                        if (bbEdit.text.isNullOrEmpty()) 0 else bbEdit.getMinute(),
                        if (bbEdit.text.isNullOrEmpty()) 0 else bbEdit.getSecond(),
                        if (afterEdit.text.isNullOrEmpty()) 0 else afterEdit.value(),
                            )
                    )
                }
                s.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                s.show()
            }
        }; return b.root
    }

    private fun EditText.value(): Int {
        return this.text.toString().toInt()
    }

    private fun check(view: EditText) {
        var colons = 0
        var index = view.text.indexOf(":")
        while (index != -1) {
            colons++; index = view.text.indexOf(":", index + 1)
        }
        if (view.text.isNullOrEmpty()) view.error = null
        else if (colons > 0) {
            if (view.text.toString().substring(0, 1) == ":") view.error = getString(R.string.error)
            else if (view.text.toString().substring(1, 2) == ":") {
                if (view.text.length != 4 || colons != 1) view.error = getString(R.string.error)
                else view.error = null
            }
            else if (view.text.toString().substring(2, 3) == ":") {
                if (view.text.length != 5 || colons != 1) view.error = getString(R.string.error)
                else view.error = null
            }
            else view.error = getString(R.string.error) }
        else if (view.value() == 0) view.error = getString(R.string.error_n)
        else if (view.value() > 512) view.error = getString(R.string.error_i)
        else view.error = null
    }

    private fun EditText.getMinute(): Int {
        return if (this.text.indexOf(":") == 1) {
            this.text.toString().substring(0, 1).toInt()
        } else if (this.text.indexOf(":") == 2){
            this.text.toString().substring(0, 2).toInt()
        } else this.value()
    }

    private fun EditText.getSecond(): Int {
        return if (this.text.indexOf(":") == 1) {
            this.text.toString().substring(2).toInt()
        } else if (this.text.indexOf(":") == 2){
            this.text.toString().substring(3).toInt()
        } else 0
    }

}