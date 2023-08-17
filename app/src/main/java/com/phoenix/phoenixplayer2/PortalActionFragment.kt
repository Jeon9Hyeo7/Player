package com.phoenix.phoenixplayer2

import android.content.ComponentName
import android.content.ContentValues
import android.media.tv.TvInputInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.phoenix.phoenixplayer2.api.ConnectManager
import com.phoenix.phoenixplayer2.databinding.FragmentActionBinding
import com.phoenix.phoenixplayer2.db.StateListener
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.model.Portal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            val connectManager = ConnectManager(selectedPortal.serverUrl, selectedPortal.macAddress, selectedPortal.token)
            CoroutineScope(Dispatchers.IO).launch {
                val inputId:String = getInputId()
                context?.let { context -> connectManager.insert(inputId, context, stateListener) }
            }
        }
        actionEdit = binding.actionEdit
        actionDelete = binding.actionDelete
        return binding.root
    }


    private fun getInputId():String{
        val componentName = context?.let {
            ComponentName(it.packageName, InputService::class.java.name) }
        val builder: TvInputInfo.Builder = TvInputInfo.Builder(context, componentName)
        val tvInputInfo:TvInputInfo = builder.build()
        val intent = tvInputInfo.createSetupIntent()
        return intent.getStringExtra(TvInputInfo.EXTRA_INPUT_ID)!!
    }




}