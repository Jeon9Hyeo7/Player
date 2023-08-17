package com.phoenix.phoenixplayer2.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.phoenix.phoenixplayer2.R
import com.phoenix.phoenixplayer2.databinding.FragmentActionBinding
import com.phoenix.phoenixplayer2.db.StateListener
import com.phoenix.phoenixplayer2.model.Portal

class PortalActionFragment(val portal: Portal? = null) : Fragment(){


    private lateinit var binding: FragmentActionBinding
    private lateinit var actionConnect: TextView
    private lateinit var actionEdit:TextView
    private lateinit var actionDelete:TextView
    private lateinit var stateListener: StateListener

    companion object{
        private const val TAG: String = "PortalActionFragment"
    }





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActionBinding.inflate(inflater, container, false)
        actionConnect = binding.actionConnect
        actionConnect.setOnClickListener{
            val selectedPortal = portal!!
            activity?.supportFragmentManager!!
                .beginTransaction().add(R.id.main_frame, LoadFragment(selectedPortal))
                .addToBackStack(null)
                .hide(this@PortalActionFragment).commit()

        }
        actionEdit = binding.actionEdit
        actionDelete = binding.actionDelete
        return binding.root
    }







}