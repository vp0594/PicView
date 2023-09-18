package com.example.picview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.picview.databinding.FragmentTopActionBinding


class TopActionFragment : Fragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentTopActionBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_top_action, container, false)
        binding = FragmentTopActionBinding.bind(view)
        return binding.root
    }


}