package com.phoenix.phoenixplayer2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.phoenix.phoenixplayer2.databinding.FragmentEditBinding
import com.phoenix.phoenixplayer2.model.Portal

class PortalEditFragment(private var portal: Portal? = null) : Fragment(){


    private lateinit var binding: FragmentEditBinding


    companion object {
        private const val TAG:String = "PortalEditFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}