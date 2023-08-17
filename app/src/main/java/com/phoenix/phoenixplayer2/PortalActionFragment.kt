package com.phoenix.phoenixplayer2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.phoenix.phoenixplayer2.databinding.FragmentActionBinding
import com.phoenix.phoenixplayer2.model.Portal

class PortalActionFragment(portal: Portal? = null) : Fragment(){
    private lateinit var binding: FragmentActionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActionBinding.inflate(inflater, container, false)
        return binding.root
    }




}