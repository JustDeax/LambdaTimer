package com.example.lambdatimer.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lambdatimer.databinding.FragmentSavedBinding
import com.example.lambdatimer.MViewModel
import com.example.lambdatimer.RecyclerAdapter

class SavedFragment : Fragment() {
    private lateinit var b: FragmentSavedBinding
    private lateinit var mViewModel: MViewModel
    private val adapter = RecyclerAdapter()

    override fun onResume() {
        super.onResume()
        mViewModel = ViewModelProvider(this)[MViewModel::class.java]
        mViewModel.readAllData.observe(viewLifecycleOwner) { entity ->
            adapter.setData(entity)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentSavedBinding.inflate(inflater, container, false)

        val recycler = b.recycleView
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())

        mViewModel = ViewModelProvider(this)[MViewModel::class.java]
        mViewModel.readAllData.observe(viewLifecycleOwner) { entity ->
            adapter.setData(entity)
        }

        return b.root
    }
}