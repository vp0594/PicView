package com.example.picview

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.picview.databinding.FragmentTopActionBinding
import com.example.picview.databinding.FragmentVideoActionBinding


class VideoActionFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentVideoActionBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_video_action, container, false)
        binding = FragmentVideoActionBinding.bind(view)
        return binding.root
    }
}